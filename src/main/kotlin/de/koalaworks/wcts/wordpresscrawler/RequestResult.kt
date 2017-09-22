package de.koalaworks.wcts.wordpresscrawler

open class RequestResult<out T>(
    val success: Boolean,
    val totalItems: Int,
    val items: Collection<T>,
    effectivePageSize: Int,
    val erroneousItems: Int) {

    val totalPages: Int = totalItems / effectivePageSize + (if (totalItems % effectivePageSize != 0) 1 else 0)
    override fun toString(): String {
        return "RequestResult(success=$success, totalItems=$totalItems, erroneousItems=$erroneousItems, totalPages=$totalPages)"
    }
}