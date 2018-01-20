package de.koalaworks.wccs.wordpresscrawler

import de.koalaworks.wccs.wordpresscrawler.classification.RequestExecutor
import de.koalaworks.wccs.wordpresscrawler.job.Job
import de.koalaworks.wccs.wordpresscrawler.rest.RestClient
import de.koalaworks.wccs.wordpresscrawler.wordpress.ErrorHandlingRequestExecutor
import de.koalaworks.wccs.wordpresscrawler.wordpress.SimpleRequestExecutor
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicInteger

class ClassifyingCrawler(private val job: Job, private val executorService: ExecutorService) {

    private val classificationEngine = RequestExecutor(job.classificationService, RestClient)
    private val wordPressRequestResultConsumer = WordPressRequestResultConsumer(executorService, classificationEngine)

    private val logger = LoggerFactory.getLogger(ClassifyingCrawler::class.java)
    private val totalItems = AtomicInteger(0)

    fun run() {
        val wordPressRequests = wordPressRequestResultConsumer.start()

        val start = System.currentTimeMillis()
        val initialRequests = job.sites
                .map { SimpleRequestExecutor(it, RestClient) }
                .map { ErrorHandlingRequestExecutor(it) }
                .flatMap { crawler ->
                    listOf(crawler::getPages, crawler::getPosts).map { operation ->
                        CompletableFuture.runAsync(Runnable {
                            val requestResult = operation(1, job.crawler.resultPageSize)
                            wordPressRequestResultConsumer.submit { requestResult }
                            totalItems.addAndGet(requestResult.totalItems)
                            IntRange(2, requestResult.totalPages).map { page ->
                                wordPressRequestResultConsumer.submit {
                                    operation(page, job.crawler.resultPageSize)
                                }
                            }
                        }, executorService)
                    }
                }

        logger.debug("{} initial requests sent.", initialRequests.size)
        CompletableFuture.allOf(*initialRequests.toTypedArray()).thenRun {
            logger.debug("Initial requests finished. Will shutdown executor service.")
            executorService.shutdown()
        }

        val classificationRequests = wordPressRequests.join()
        logger.debug("WordPress requests finished. Waiting for classification requests to finish.")
        classificationRequests.forEach { it.get() }
        val duration = System.currentTimeMillis() - start
        logger.debug("Classification requests finished.")
        logger.info("Processed {} resources in {} ms. {} erroneous resources. {} total resources.",
                wordPressRequestResultConsumer.processedResources(),
                duration,
                wordPressRequestResultConsumer.errorneousResources(),
                totalItems)
    }
}