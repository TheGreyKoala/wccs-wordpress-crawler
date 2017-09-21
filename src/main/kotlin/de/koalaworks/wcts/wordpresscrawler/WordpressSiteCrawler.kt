package de.koalaworks.wcts.wordpresscrawler

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WordpressSiteCrawler(val requestExecutor: WordpressRequestExecutor) {

    private companion object {
        val logger = LoggerFactory.getLogger(WordpressSiteCrawler.javaClass)
    }

    fun getPages(page: Int, pageSize: Int): RequestResult<Page> {
        val normalizedPageSize = pageSize.greatestPowerOfTwo()
        logger.debug("Requesting pages: site={}, page={}, pageSize={}, normalizedPageSize={}", requestExecutor.siteUrl, page, pageSize, normalizedPageSize)

        val result = requestExecutor.downloadPages(page, pageSize)
        logger.debug("Result: {}", result)
        if (result.success || pageSize == 1) {
            return result
        } else {
            // Do not run in parallel! This request was meant to be a single one.
            // Therefore, if we split it, we might break the maximum number of concurrent requests
            val leftHalfResult = getPages(page * 2 - 1, pageSize / 2)
            val rightHalfResult = getPages(page * 2, pageSize / 2)

            val resultsList = listOf(leftHalfResult, rightHalfResult)
            val successfulRequests = resultsList.filter { it.success }
            val failedRequests = resultsList.filter { !it.success }
            failedRequests.logAll(logger)

            val success = !successfulRequests.isEmpty()
            // first() throws an exception, if it is empty
            val totalItems = if (success) successfulRequests.first().totalItems else -1
            val resultItems = successfulRequests.mergeItems()
            return RequestResult(success, totalItems, resultItems)
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

    private fun List<RequestResult<Page>>.logAll(logger: Logger) {
        this.forEach {
            logger.error("Failed request. Result: {}", it)
        }
    }

    private fun List<RequestResult<Page>>.mergeItems(): Collection<Page> {
        return this.map { it.items }
                .flatten()
    }
}