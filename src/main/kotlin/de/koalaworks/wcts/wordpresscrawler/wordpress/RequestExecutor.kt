package de.koalaworks.wcts.wordpresscrawler.wordpress

interface RequestExecutor {
    fun getPages(resultPage: Int, resultPageSize: Int): RequestResult
    fun getPosts(resultPage: Int, resultPageSize: Int): RequestResult
}