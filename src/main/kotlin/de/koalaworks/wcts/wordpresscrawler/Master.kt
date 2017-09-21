package de.koalaworks.wcts.wordpresscrawler

import de.koalaworks.wcts.wordpresscrawler.jobs.Job
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.function.Consumer
import java.util.function.Supplier

class Master(private val job: Job, private val executorService: ExecutorService) {

    private companion object {
        val logger = LoggerFactory.getLogger(Master.javaClass)
    }

    fun run() {
        val restClient = RestClient()
        val initialRequests = job.sites
            .map { WordpressRequestExecutor(it, restClient) }
            .map { WordpressSiteCrawler(it) }
            .map { crawler ->
                requestResultPageAsync(1, crawler)
//                val initialRequestFuture = requestResultPageAsync(1, crawler)
//                val firstPageTypingEngineFuture = initialRequestFuture.thenAcceptAsync(Consumer { callTypingEngine(it) }, executorService)
//                val otherPagesRequestsFutures = initialRequestFuture.thenApply { requestRemainingResultPagesAsync(it.totalItems, crawler) }
//                val otherPagesTypingEngineFutures = otherPagesRequestsFutures.thenAccept { futures ->
//                    val map = futures.map {
//                        it.thenAcceptAsync(Consumer { callTypingEngine(it) }, executorService)
//                    }
//                }
//                otherPagesTypingEngineFutures


            }
    }

    private fun requestResultPageAsync(page: Int, crawler: WordpressSiteCrawler): CompletableFuture<RequestResult<Page>> {
        return CompletableFuture
            .supplyAsync(Supplier { crawler.getPages(page, job.crawler.pageSize) }, executorService)
    }

    private fun requestRemainingResultPagesAsync(totalItems: Int, crawler: WordpressSiteCrawler): Collection<CompletableFuture<RequestResult<Page>>> {
        val totalPages = totalItems / job.crawler.pageSize + (if (totalItems % job.crawler.pageSize != 0) 1 else 0)
        return IntRange(2, totalPages).map {
            requestResultPageAsync(it, crawler)
        }
    }

    private fun callTypingEngine(result: RequestResult<Page>) {
        logger.info("Calling typing engine")
    }
}