package de.koalaworks.wcts.wordpresscrawler

data class RequestResult(val success: Boolean, val totalItems: Int, val items: Collection<String>)