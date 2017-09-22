package de.koalaworks.wcts.wordpresscrawler

import de.koalaworks.wcts.wordpresscrawler.jobs.Job
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Function
import java.util.function.Supplier

class Master(private val job: Job, private val executorService: ExecutorService) {

    var counter: AtomicInteger = AtomicInteger(0)
    var errorCounter: AtomicInteger = AtomicInteger(0)
    var totalItems: AtomicInteger = AtomicInteger(0)

    private companion object {
        val logger = LoggerFactory.getLogger(Master.javaClass)
    }

    fun run(): CompletableFuture<Void> {
        val restClient = RestClient()
        val siteFutures = job.sites
            .map { WordpressRequestExecutor(it, restClient) }
            .map { WordpressSiteCrawler(it) }
            .map { crawler ->
                requestResultPageAsync(1, crawler).thenApplyAsync {
                    it.increaseCounter()
                    totalItems.addAndGet(it.totalItems)
                    val typingEngineFuture = CompletableFuture.supplyAsync(Supplier { callTypingEngine(it) }, executorService)
                    val remainingPagesTypingEngineFutures = requestRemainingResultPagesAsync(it, crawler)
                    val list = listOf(typingEngineFuture, remainingPagesTypingEngineFutures)
                    allOf(list)
                }
            }
            .map {
                // Wait for initial request (page 1) to finish.
                // The result will be a Future for each site.
                // Each Future indicates when all tasks for a site have finished
                it.join()
            }
        return allOf(siteFutures)
    }

    private fun requestResultPageAsync(page: Int, crawler: WordpressSiteCrawler): CompletableFuture<PageResult> {
        return CompletableFuture.supplyAsync(Supplier { crawler.getPages(page, job.crawler.pageSize) }, executorService)
    }

    private fun requestRemainingResultPagesAsync(initialRequestResult: PageResult, crawler: WordpressSiteCrawler): CompletableFuture<Void> {
        logger.debug("Request {} remaining pages.", initialRequestResult.totalPages - 1)
        val futures = IntRange(2, initialRequestResult.totalPages).map {
            requestResultPageAsync(it, crawler)
                    .thenApplyAsync(Function<PageResult, Unit> {
                        it.increaseCounter()
                        callTypingEngine(it)
                    }, executorService)
        }
        return allOf(futures)
    }

    private fun callTypingEngine(result: PageResult) {
        logger.info("Calling typing engine with {} items.", result.items.size)
    }

    private fun <T> allOf(futures: List<CompletableFuture<out T>>): CompletableFuture<Void> {
        return CompletableFuture.allOf(*Array(futures.size, { futures[it] }))
    }

    private fun PageResult.increaseCounter() {
        counter.addAndGet(this.items.size)
        errorCounter.addAndGet(this.erroneousItems)
    }
}