package de.koalaworks.wcts.wordpresscrawler.wordpress

import org.slf4j.LoggerFactory

class ErrorHandlingRequestExecutor(private val requestExecutor: SimpleRequestExecutor): RequestExecutor {
    private val logger = LoggerFactory.getLogger(ErrorHandlingRequestExecutor::class.java)

    override fun getPages(resultPage: Int, resultPageSize: Int): RequestResult {
        logger.debug("Requesting pages: site={}, resultPage={}, resultPageSize={}", requestExecutor.site, resultPage, resultPageSize)
        return queryResources(requestExecutor::getPages, resultPage, resultPageSize)
    }

    override fun getPosts(resultPage: Int, resultPageSize: Int): RequestResult {
        logger.debug("Requesting posts: site={}, resultPage={}, resultPageSize={}", requestExecutor.site, resultPage, resultPageSize)
        return queryResources(requestExecutor::getPosts, resultPage, resultPageSize)
    }

    private fun queryResources(executeQuery: (resultPage: Int, resultPageSize: Int) -> RequestResult, resultPage: Int, resultPageSize: Int): RequestResult {
        val normalizedPageSize = resultPageSize.greatestPowerOfTwo()

        val result = executeQuery(resultPage, normalizedPageSize)
        if (result.success) {
            logger.debug("Result: {}", result)
            return result
        } else {
            logger.error("Request failed: site={}, resultPage={}, resultPageSize={}", requestExecutor.site, resultPage, normalizedPageSize)
            if (normalizedPageSize == 1) {
                return result
            } else {
                // Do not run in parallel! This request was meant to be a single one.
                // Therefore, if we split it, we might break the maximum number of concurrent requests
                val leftHalfResult = queryResources(executeQuery, resultPage * 2 - 1, normalizedPageSize / 2)
                val rightHalfResult = queryResources(executeQuery, resultPage * 2, normalizedPageSize / 2)

                val resultsList = listOf(leftHalfResult, rightHalfResult)
                val successfulRequests = resultsList.filter { it.success }

                val success = !successfulRequests.isEmpty()
                // first() throws an exception, if it is empty
                val totalItems = if (success) successfulRequests.first().totalItems else -1
                val resultItems = successfulRequests.mergeItems()
                val newResult = RequestResult(success, totalItems, resultItems, normalizedPageSize, leftHalfResult.erroneousItems + rightHalfResult.erroneousItems, result.site)
                logger.debug("Result: {}", newResult)
                return newResult
            }
        }
    }

    private fun Int.greatestPowerOfTwo(): Int {
        return if (this == 0) { 8 }
        else {
            var powerOf2 = 1
            while (powerOf2 < this) {
                powerOf2 = powerOf2 shl 1
            }
            if (powerOf2 == this) this else powerOf2 shr 1
        }
    }

    private fun List<RequestResult>.mergeItems(): Collection<Resource> {
        return this.map { it.items }
                .flatten()
    }
}