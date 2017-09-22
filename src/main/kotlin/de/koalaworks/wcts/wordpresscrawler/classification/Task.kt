package de.koalaworks.wcts.wordpresscrawler.classification

import de.koalaworks.wcts.wordpresscrawler.job.Site

data class Task(val site: Site, val pages: List<String>)