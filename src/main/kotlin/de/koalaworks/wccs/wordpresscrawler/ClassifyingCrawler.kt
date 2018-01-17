package de.koalaworks.wccs.wordpresscrawler

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import de.koalaworks.wccs.wordpresscrawler.classification.RequestExecutor
import de.koalaworks.wccs.wordpresscrawler.job.Job
import de.koalaworks.wccs.wordpresscrawler.rest.RestClient
import de.koalaworks.wccs.wordpresscrawler.wordpress.ErrorHandlingRequestExecutor
import de.koalaworks.wccs.wordpresscrawler.wordpress.RequestResult
import de.koalaworks.wccs.wordpresscrawler.wordpress.SimpleRequestExecutor
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Function
import java.util.function.Supplier

class ClassifyingCrawler(private val job: Job, private val executorService: ExecutorService) {

    var counter = AtomicInteger(0)
    var errorCounter = AtomicInteger(0)
    var totalItems = AtomicInteger(0)

    private val classificationEngine = RequestExecutor(job.classificationService, RestClient)
    private val classificationServiceFutures = ArrayList<Future<HttpResponse<JsonNode>>>()

    val logger = LoggerFactory.getLogger(ClassifyingCrawler::class.java)

    fun run(): CompletableFuture<Void> {
        val siteFutures = job.sites
            .map { SimpleRequestExecutor(it, RestClient) }
            .map { ErrorHandlingRequestExecutor(it) }
            .flatMap { crawler ->
                val pagesProcessingFutures = requestAsync(crawler::getPages, 1)
                        .thenApplyAsync { processFirstResultPage(it, crawler::getPages) }

                val postsProcessingFutures = requestAsync(crawler::getPosts, 1)
                        .thenApplyAsync { processFirstResultPage(it, crawler::getPosts) }

                // Do not merge these Futures into one using allOf.
                // Their results are Futures again, which will be collected in siteFutures.
                // If we call allOf, these nested Futures would be lost and
                // we would only wait for the Future for the first result page.
                listOf(pagesProcessingFutures, postsProcessingFutures)
            }
            .map {
                // Wait for first request (result page 1) to finish.
                // The result will be a Future, that is completed, when all pages resp. posts for a site were processed.
                it.join()
            }

        return allOf(siteFutures).thenAccept {
            // All pages and posts of all sites have been discovered
            // and for each a request to the classification engine was made.
            // Now we need for these requests to finish.
            classificationServiceFutures.forEach {
                try {
                    it.get()
                } catch (e: Exception) {
                    // Just prevent the exception from being thrown.
                    // Will be logged in Unirest callback.
                }
            }
        }
    }

    private fun processFirstResultPage(
            firstResultPage: RequestResult,
            remainingResultPagesOperation: (resultPage: Int, resultPageSize: Int) -> RequestResult): CompletableFuture<Void> {

        firstResultPage.increaseCounter()
        totalItems.addAndGet(firstResultPage.totalItems)
        val typingEngineFuture = CompletableFuture.supplyAsync(Supplier { callTypingEngine(firstResultPage) }, executorService)
        val processingFutures = processRemainingResultPagesAsync(firstResultPage, remainingResultPagesOperation)
        val list = listOf(typingEngineFuture, processingFutures)
        return allOf(list)
    }

    private fun requestAsync(
            asyncOperation: (resultPage: Int, resultPageSize: Int) -> RequestResult,
            resultPage: Int): CompletableFuture<RequestResult> {

        return CompletableFuture.supplyAsync(Supplier { asyncOperation(resultPage, job.crawler.resultPageSize) }, executorService)
    }

    private fun processRemainingResultPagesAsync(firstRequestResult: RequestResult, asyncOperation: (resultPage: Int, resultPageSize: Int) -> RequestResult): CompletableFuture<Void> {
        logger.debug("Requesting {} remaining result pages.", firstRequestResult.totalPages - 1)
        val futures = IntRange(2, firstRequestResult.totalPages).map {
            requestAsync(asyncOperation, it)
                    .thenApplyAsync(Function<RequestResult, Unit> {
                        it.increaseCounter()
                        callTypingEngine(it)
                    }, executorService)
        }
        return allOf(futures)
    }

    private fun callTypingEngine(result: RequestResult) {
        logger.info("Calling typing engine with {} items.", result.items.size)
        val future = classificationEngine.classify(result.site, result.items)
        synchronized(classificationServiceFutures) {
            classificationServiceFutures.add(future)
        }
    }

    private fun <T> allOf(futures: List<CompletableFuture<out T>>): CompletableFuture<Void> {
        return CompletableFuture.allOf(*Array(futures.size, { futures[it] }))
    }

    private fun RequestResult.increaseCounter() {
        counter.addAndGet(this.items.size)
        errorCounter.addAndGet(this.erroneousItems)
    }
}