package de.koalaworks.wcts.wordpresscrawler.jobs

class Crawler(val pageSize: Int = 8, val maxConcurrentRequests: Int = 5) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Crawler

        if (pageSize != other.pageSize) return false
        if (maxConcurrentRequests != other.maxConcurrentRequests) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pageSize
        result = 31 * result + maxConcurrentRequests
        return result
    }

    override fun toString(): String {
        return "Crawler(pageSize=$pageSize, maxConcurrentRequests=$maxConcurrentRequests)"
    }
}