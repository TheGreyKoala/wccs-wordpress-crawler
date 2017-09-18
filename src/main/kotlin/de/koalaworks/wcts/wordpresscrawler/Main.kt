package de.koalaworks.wcts.wordpresscrawler

import de.koalaworks.wcts.wordpresscrawler.jobs.Job
import de.koalaworks.wcts.wordpresscrawler.jobs.JobReader
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger(Job::class.java)
    if (args.size != 1) {
        logger.error("Please provide the path to the job file.")
        exitProcess(1)
    }

    val jobFile = Paths.get(args[0])
    val job = JobReader(jobFile).readJob()
    println(job.sites.size)
    logger.info("Job parameters: " + job)
}
