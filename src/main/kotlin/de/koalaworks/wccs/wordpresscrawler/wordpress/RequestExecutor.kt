package de.koalaworks.wccs.wordpresscrawler.wordpress

interface RequestExecutor {
    fun getPages(resultPage: Int, resultPageSize: Int): RequestResult
    fun getPosts(resultPage: Int, resultPageSize: Int): RequestResult
}