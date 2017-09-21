package de.koalaworks.wcts.wordpresscrawler

data class RequestResult<out T>(val success: Boolean, val totalItems: Int, val items: Collection<T>) {
    override fun toString(): String {
        return "RequestResult(success=$success, totalItems=$totalItems, items=$items)"
    }
}