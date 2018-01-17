package de.koalaworks.wccs.wordpresscrawler.wordpress

import de.koalaworks.wccs.wordpresscrawler.job.Site

class RequestResult(
        val success: Boolean,
        val totalItems: Int,
        val items: Collection<Resource>,
        effectivePageSize: Int,
        val erroneousItems: Int,
        val site: Site) {

    val totalPages: Int = totalItems / effectivePageSize + (if (totalItems % effectivePageSize != 0) 1 else 0)
    override fun toString(): String {
        return "RequestResult(success=$success, totalItems=$totalItems, erroneousItems=$erroneousItems, totalPages=$totalPages)"
    }
}