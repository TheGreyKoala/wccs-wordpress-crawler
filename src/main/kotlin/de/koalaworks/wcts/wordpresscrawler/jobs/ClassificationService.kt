package de.koalaworks.wcts.wordpresscrawler.jobs

data class ClassificationService(
        val scheme: String = "http",
        val host: String = "localhost",
        val port:String = "44284",
        val path: String = "/") {

    val fullUrl = "$scheme://$host:$port$path"

    override fun toString(): String {
        return "ClassificationService(scheme='$scheme', host='$host', port='$port', path='$path')"
    }
}