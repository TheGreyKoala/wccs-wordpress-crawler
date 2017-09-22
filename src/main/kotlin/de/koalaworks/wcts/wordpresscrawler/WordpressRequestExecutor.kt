package de.koalaworks.wcts.wordpresscrawler

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import com.google.gson.reflect.TypeToken

open class WordpressRequestExecutor(val siteUrl: String, private val restClient: RestClient) {
    private val pagesUrl: String = siteUrl + "/wp-json/wp/v2/pages"
    private val postsUrl: String = siteUrl + "/wp-json/wp/v2/posts"
    private val gson: Gson = Gson()
    private val wordpressResourceCollectionType = object : TypeToken<Collection<WordpressResource>>() {}.type

    private val logger = LoggerFactory.getLogger(WordpressRequestExecutor::class.java)

    fun downloadPages(resultPage: Int, resultPageSize:Int): RequestResult {
        return query(pagesUrl, resultPage, resultPageSize)
    }

    fun downloadPosts(resultPage: Int, resultPageSize:Int): RequestResult {
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
                val wordpressResources: Collection<WordpressResource> = gson.fromJson(response.body, wordpressResourceCollectionType)
                val totalPages = response.headers["X-WP-Total"]!![0].toInt()
                RequestResult(true, totalPages, wordpressResources, resultPageSize, 0)
            } else {
                RequestResult(false, -1, emptyList(), resultPageSize, resultPageSize)
            }
        } catch (e: Exception) {
            RequestResult(false, -1, emptyList(), resultPageSize, resultPageSize)
        }
    }
}