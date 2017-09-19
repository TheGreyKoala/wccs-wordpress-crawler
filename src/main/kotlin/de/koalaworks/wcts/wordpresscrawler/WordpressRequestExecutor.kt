package de.koalaworks.wcts.wordpresscrawler

import org.slf4j.LoggerFactory

open class WordpressRequestExecutor {

    private val logger = LoggerFactory.getLogger(WordpressRequestExecutor::class.java)

    open fun download(page: Int, pageSize: Int): RequestResult {
        logger.debug("Downloading pageSize={}, page={}", pageSize, page)
        return RequestResult(true, 100, emptyList())
    }
}