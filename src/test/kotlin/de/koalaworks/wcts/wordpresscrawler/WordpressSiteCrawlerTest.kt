package de.koalaworks.wcts.wordpresscrawler

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class WordpressSiteCrawlerTest {

    /*@Test
    fun crawler() {
        val crawler = object : WordpressRequestExecutor("", RestClient()) {
            override fun download(page: Int, pageSize: Int): RequestResult {
                val startIndex = ((page - 1) * pageSize)
                val endIndex = (page * pageSize - 1)
                println("Request parameters page=$page, pageSize=$pageSize")
                println("Requesting items $startIndex..$endIndex")
                return if (47 in startIndex..endIndex) {
                    RequestResult(false, -1, emptyList())
                } else {
                    RequestResult(true, 97, emptyList())
                }
            }
        }

        val wordpressSiteCrawler = WordpressSiteCrawler("http://myhost", 10, crawler)
        wordpressSiteCrawler.doIt()
    }

    @Test
    fun crawler2() {
        val crawler = object : WordpressRequestExecutor("", RestClient()) {
            override fun download(page: Int, pageSize: Int): RequestResult {
                val startIndex = ((page - 1) * pageSize)
                val endIndex = (page * pageSize - 1)
                println("Request parameters page=$page, pageSize=$pageSize")
                println("Requesting items $startIndex..$endIndex")
                return if (3 in startIndex..endIndex) {
                    RequestResult(false, -1, emptyList())
                } else {
                    RequestResult(true, 6, emptyList())
                }
            }
        }

        val wordpressSiteCrawler = WordpressSiteCrawler("http://myhost", 5, crawler)
        wordpressSiteCrawler.doIt()
    }

    @Test
    fun crawler3() {
        val crawler = object : WordpressRequestExecutor("", RestClient()) {
            override fun download(page: Int, pageSize: Int): RequestResult {
                val startIndex = ((page - 1) * pageSize)
                val endIndex = (page * pageSize - 1)
                println("Request parameters page=$page, pageSize=$pageSize")
                println("Requesting items $startIndex..$endIndex")
                return if (8 in startIndex..endIndex) {
                    RequestResult(false, -1, emptyList())
                } else {
                    RequestResult(true, 16, emptyList())
                }
            }
        }

        val wordpressSiteCrawler = WordpressSiteCrawler("http://myhost", 14, crawler)
        wordpressSiteCrawler.doIt()
    }*/

    @Test
    fun downloadPages() {
        val wordpressRequestExecutor = WordpressRequestExecutor("http://www.fernuni-hagen.de/KSW/portale/babw", RestClient())
        val wordpressSiteCrawler = WordpressSiteCrawler("", 8, wordpressRequestExecutor)
        wordpressSiteCrawler.getPages()
    }
}