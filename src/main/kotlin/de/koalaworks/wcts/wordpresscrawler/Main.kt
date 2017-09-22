package de.koalaworks.wcts.wordpresscrawler

import de.koalaworks.wcts.wordpresscrawler.jobs.JobReader
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Supplier
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Root")
    if (args.size != 1) {
        logger.error("Please provide the path to the job file.")
        exitProcess(1)
    }

    val jobFile = Paths.get(args[0])
    val job = JobReader(jobFile).readJob()
    logger.debug("Job parameters: {}", job)

    val executorService = Executors.newFixedThreadPool(job.crawler.maxConcurrentRequests)
    val master = Master(job, executorService)
    val start = System.currentTimeMillis()
    val run = master.run()
    run.join()
    val duration = System.currentTimeMillis() - start
    logger.info("Processed {} items, {} erroneous items of {} items in {} ms.", master.counter, master.errorCounter, master.totalItems, duration)
    executorService.shutdown()
}
