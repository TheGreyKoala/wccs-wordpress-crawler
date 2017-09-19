package de.koalaworks.wcts.wordpresscrawler.jobs

import java.util.*

data class Job(
        val classificationService: ClassificationService = ClassificationService(),
        val sites: Array<String> = emptyArray(),
        val pageSize: Int = 10){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Job

        if (classificationService != other.classificationService) return false
        if (!Arrays.equals(sites, other.sites)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = classificationService.hashCode()
        result = 31 * result + Arrays.hashCode(sites)
        return result
    }

    override fun toString(): String {
        return "Job(classificationService=$classificationService, sites=${Arrays.toString(sites)})"
    }
}