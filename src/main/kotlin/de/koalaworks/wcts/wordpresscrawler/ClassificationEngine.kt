package de.koalaworks.wcts.wordpresscrawler

import com.google.gson.Gson
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import de.koalaworks.wcts.wordpresscrawler.classificationEngine.Job
import de.koalaworks.wcts.wordpresscrawler.classificationEngine.Task
import de.koalaworks.wcts.wordpresscrawler.jobs.ClassificationService
import de.koalaworks.wcts.wordpresscrawler.jobs.Site
import java.util.concurrent.Future

class ClassificationEngine(
        private val classificationService: ClassificationService,
        private val restClient: RestClient) {

    fun classify(site: Site, resources: Collection<WordpressResource>): Future<HttpResponse<JsonNode>> {
        val resourceLinks = resources.map(WordpressResource::link)
        val task = Task(site, resourceLinks)
        val gson = Gson()
        val job = Job(listOf(task))
        val jobJson = gson.toJson(job)

        return restClient
            .post(classificationService.fullUrl)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json;charset=utf-8")
            .body(jobJson)
            .asJsonAsync()
    }
}