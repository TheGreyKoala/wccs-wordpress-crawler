package de.koalaworks.wccs.wordpresscrawler.job

class Crawler(val resultPageSize: Int = 8, val maxConcurrentRequests: Int = 5) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Crawler

        if (resultPageSize != other.resultPageSize) return false
        if (maxConcurrentRequests != other.maxConcurrentRequests) return false

        return true
    }

    override fun hashCode(): Int {
        var result = resultPageSize
        result = 31 * result + maxConcurrentRequests
        return result
    }

    override fun toString(): String {
        return "Crawler(resultPageSize=$resultPageSize, maxConcurrentRequests=$maxConcurrentRequests)"
    }
}