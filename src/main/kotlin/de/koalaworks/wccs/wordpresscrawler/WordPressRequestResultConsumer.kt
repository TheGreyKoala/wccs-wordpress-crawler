package de.koalaworks.wccs.wordpresscrawler

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import de.koalaworks.wccs.wordpresscrawler.classification.RequestExecutor
import de.koalaworks.wccs.wordpresscrawler.wordpress.RequestResult
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

class WordPressRequestResultConsumer(
        private val executorService: ExecutorService,
        private val requestExecutor: RequestExecutor) : ExecutorCompletionService<RequestResult>(executorService) {

    private val logger = LoggerFactory.getLogger(WordPressRequestResultConsumer::class.java)

    private val submittedTasks = AtomicInteger(0)
    private val completedTasks = AtomicInteger(0)
    private val successfulItems = AtomicInteger(0)
    private val erroneousItems = AtomicInteger(0)

    override fun submit(task: Callable<RequestResult>): Future<RequestResult> {
        if (executorService.isTerminated) {
            throw UnsupportedOperationException("New tasks can not be submitted after consumer has been shutdown.")
        }
        submittedTasks.incrementAndGet()
        return super.submit(task)
    }

    fun start(): CompletableFuture<List<Future<HttpResponse<JsonNode>>>> {
        return CompletableFuture.supplyAsync { processRequestResults() }
    }

    private fun processRequestResults(): List<Future<HttpResponse<JsonNode>>> {
        val classificationRequestFutures = LinkedList<Future<HttpResponse<JsonNode>>>()
        do {
            /*
             * poll and don't take.
             * We would wait forever, if the last task was completed
             * before the shutdown signal was sent.
             */
            val future = poll(5, TimeUnit.SECONDS)
            if (future != null) {
                val requestResult = future.get()
                completedTasks.incrementAndGet()
                val classificationRequestFuture = requestExecutor.classify(requestResult.site, requestResult.items)
                classificationRequestFutures.add(classificationRequestFuture)
                successfulItems.addAndGet(requestResult.items.size)
                erroneousItems.addAndGet(requestResult.erroneousItems)
            }
        } while (!executorService.isTerminated || submittedTasks.get() > completedTasks.get())
        return classificationRequestFutures
    }

    fun processedResources() = successfulItems.get()
    fun errorneousResources() = erroneousItems.get()
}