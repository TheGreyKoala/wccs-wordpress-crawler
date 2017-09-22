package de.koalaworks.wcts.wordpresscrawler.wordpress

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import com.google.gson.reflect.TypeToken
import de.koalaworks.wcts.wordpresscrawler.rest.RestClient
import de.koalaworks.wcts.wordpresscrawler.job.Site

open class SimpleRequestExecutor(val site: Site, private val restClient: RestClient): RequestExecutor {
    private val pagesUrl: String = site.url + "/wp-json/wp/v2/pages"
    private val postsUrl: String = site.url + "/wp-json/wp/v2/posts"
    private val gson: Gson = Gson()
    private val resourceCollectionType = object : TypeToken<Collection<Resource>>() {}.type

    private val logger = LoggerFactory.getLogger(SimpleRequestExecutor::class.java)

    override fun getPages(resultPage: Int, resultPageSize:Int): RequestResult {
        return query(pagesUrl, resultPage, resultPageSize)
    }

    override fun getPosts(resultPage: Int, resultPageSize:Int): RequestResult {
        return query(postsUrl, resultPage, resultPageSize)
    }

    private fun query(url: String, resultPage: Int, resultPageSize: Int): RequestResult {
        logger.debug("Executing query: url={}, resultPage={}, resultPageSize={}", url, resultPage, resultPageSize)
        return try {
            val response = restClient.
                    get(url)
                    .queryString(mapOf("page" to resultPage, "per_page" to resultPageSize, "context" to "embed"))
                    .asString()

            if (response.status == 200) {
                val resources: Collection<Resource> = gson.fromJson(response.body, resourceCollectionType)
                val totalPages = response.headers["X-WP-Total"]!![0].toInt()
                RequestResult(true, totalPages, resources, resultPageSize, 0, site)
            } else {
                RequestResult(false, -1, emptyList(), resultPageSize, resultPageSize, site)
            }
        } catch (e: Exception) {
            RequestResult(false, -1, emptyList(), resultPageSize, resultPageSize, site)
        }
    }
}