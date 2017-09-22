package de.koalaworks.wcts.wordpresscrawler

import de.koalaworks.wcts.wordpresscrawler.jobs.ClassificationService
import de.koalaworks.wcts.wordpresscrawler.jobs.Site
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class ClassificationEngineTest {

    val logger = LoggerFactory.getLogger(ClassificationEngineTest::class.java)

    @Test
    fun classify() {
        val classificationService = ClassificationService()
        val engine = ClassificationEngine(classificationService, RestClient())
        val site = Site("babw", "Blupp", "http://blupp.de")
        val wordpressResource = WordpressResource(4711, "post", "http://blupp.de/test")

        val classify = engine.classify(site, listOf(wordpressResource))
        try {
            val get = classify!!.get()
            logger.info("Finished with status {}", get.status)
        } catch (e: Exception) {
            logger.error("Error", e)
        }
    }
}