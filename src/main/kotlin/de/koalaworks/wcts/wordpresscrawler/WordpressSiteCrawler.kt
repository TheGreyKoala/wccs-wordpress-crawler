package de.koalaworks.wcts.wordpresscrawler

import org.slf4j.LoggerFactory

class WordpressSiteCrawler(private val requestExecutor: WordpressRequestExecutor) {

    private companion object {
        val logger = LoggerFactory.getLogger(WordpressSiteCrawler.javaClass)
    }

    fun getPages(page: Int, pageSize: Int): PageResult {
        val normalizedPageSize = pageSize.greatestPowerOfTwo()
        logger.debug("Requesting pages: site={}, page={}, pageSize={}, normalizedPageSize={}", requestExecutor.siteUrl, page, pageSize, normalizedPageSize)

        val result = requestExecutor.downloadPages(page, normalizedPageSize)
        if (result.success) {
            logger.debug("Result: {}", result)
            return result
        } else {
            logger.error("Request failed: site={}, page={}, pageSize={}", requestExecutor.siteUrl, page, normalizedPageSize)
            if (normalizedPageSize == 1) {
                return result
            } else {
                // Do not run in parallel! This request was meant to be a single one.
                // Therefore, if we split it, we might break the maximum number of concurrent requests
                val leftHalfResult = getPages(page * 2 - 1, normalizedPageSize / 2)
                val rightHalfResult = getPages(page * 2, normalizedPageSize / 2)

                val resultsList = listOf(leftHalfResult, rightHalfResult)
                val successfulRequests = resultsList.filter { it.success }

                val success = !successfulRequests.isEmpty()
                // first() throws an exception, if it is empty
                val totalItems = if (success) successfulRequests.first().totalItems else -1
                val resultItems = successfulRequests.mergeItems()
                val newResult = PageResult(success, totalItems, resultItems, normalizedPageSize, leftHalfResult.erroneousItems + rightHalfResult.erroneousItems)
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

    private fun List<PageResult>.mergeItems(): Collection<Page> {
        return this.map { it.items }
                .flatten()
    }
}