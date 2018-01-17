package de.koalaworks.wccs.wordpresscrawler.job

import java.util.*

data class Job(
        val classificationService: ClassificationService = ClassificationService(),
        val sites: Array<Site> = emptyArray(),
        val crawler: Crawler = Crawler()){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Job

        if (classificationService != other.classificationService) return false
        if (!Arrays.equals(sites, other.sites)) return false
        if (crawler != other.crawler) return false

        return true
    }

    override fun hashCode(): Int {
        var result = classificationService.hashCode()
        result = 31 * result + Arrays.hashCode(sites)
        result = 31 * result + crawler.hashCode()
        return result
    }

    override fun toString(): String {
        return "Job(classificationService=$classificationService, sites=${Arrays.toString(sites)}, crawler=$crawler)"
    }
}