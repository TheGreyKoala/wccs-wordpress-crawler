package de.koalaworks.wccs.wordpresscrawler

import de.koalaworks.wccs.wordpresscrawler.job.JobReader
import de.koalaworks.wccs.wordpresscrawler.rest.RestClient
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import java.util.concurrent.*
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
    val crawler = ClassifyingCrawler(job, executorService)
    crawler.run()
    RestClient.shutdown()
}
