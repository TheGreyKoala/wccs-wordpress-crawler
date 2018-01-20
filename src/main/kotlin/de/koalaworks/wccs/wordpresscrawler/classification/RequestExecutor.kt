package de.koalaworks.wccs.wordpresscrawler.classification

import com.google.gson.Gson
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.async.Callback
import de.koalaworks.wccs.wordpresscrawler.job.ClassificationService
import de.koalaworks.wccs.wordpresscrawler.job.Site
import java.util.concurrent.Future
import com.mashape.unirest.http.exceptions.UnirestException
import de.koalaworks.wccs.wordpresscrawler.rest.RestClient
import de.koalaworks.wccs.wordpresscrawler.wordpress.Resource
import org.slf4j.LoggerFactory

class RequestExecutor(
        private val classificationService: ClassificationService,
        private val restClient: RestClient) {

    private val logger = LoggerFactory.getLogger(RequestExecutor::class.java)

    fun classify(site: Site, resources: Collection<Resource>): Future<HttpResponse<JsonNode>> {
        val resourceLinks = resources.map(Resource::link)
        val task = Task(site, resourceLinks)
        val gson = Gson()
        val job = Job(listOf(task))
        val jobJson = gson.toJson(job)

        logger.info("Starting classification of {} resources.", resources.size)

        return restClient
            .post(classificationService.fullUrl)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json;charset=utf-8")
            .body(jobJson)
            .asJsonAsync(object : Callback<JsonNode> {
                override fun failed(e: UnirestException) {
                    val siteAsString = site.toString()
                    val resourcesAsString = resources.toString()
                    logger.error("Classification failed for site=$siteAsString, resources=$resourcesAsString", e)
                }

                override fun completed(response: HttpResponse<JsonNode>) {
                    logger.debug("Classification completed for site={}, resources={}. Status={}", site, resources, response.status)
                }

                override fun cancelled() {
                    logger.warn("Classification cancelled for site={}, resources={}", site, resources)
                }
            })
    }
}