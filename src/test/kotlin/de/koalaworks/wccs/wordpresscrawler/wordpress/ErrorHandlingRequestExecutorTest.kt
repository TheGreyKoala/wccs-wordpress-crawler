package de.koalaworks.wccs.wordpresscrawler.wordpress

import de.koalaworks.wccs.wordpresscrawler.job.Site
import de.koalaworks.wccs.wordpresscrawler.rest.RestClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

@DisplayName("ErrorHandlingRequestExecutor")
internal class ErrorHandlingRequestExecutorTest {

    @Test
    @DisplayName("should find and skip faulty resources")
    fun crawler() {
        val site = Site("test", "test", "test")
        val requestExecutor = object : SimpleRequestExecutor(site, RestClient) {
            override fun getPages(resultPage: Int, resultPageSize: Int): RequestResult {
                val startIndex = ((resultPage - 1) * resultPageSize)
                val endIndex = (resultPage * resultPageSize - 1)

                return if (45 in startIndex..endIndex) {
                    RequestResult(false, -1, emptyList(), resultPageSize, 4711, site)
                } else {
                    val resources = IntRange(startIndex, endIndex).map { Resource(it, "dummy", "") }
                    RequestResult(true, 97, resources, resultPageSize, 0, site)
                }
            }
        }

        val wordpressSiteCrawler = ErrorHandlingRequestExecutor(requestExecutor)
        val pages = wordpressSiteCrawler.getPages(6, 10)
        assertEquals(7, pages.items.size)
        val ids = pages.items.map(Resource::id)
        assertTrue(ids.contains(40))
        assertTrue(ids.contains(47))
        assertFalse(ids.contains(45))
    }
}