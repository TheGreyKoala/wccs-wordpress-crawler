package de.koalaworks.wccs.wordpresscrawler.classification

import de.koalaworks.wccs.wordpresscrawler.job.Site

data class Task(val site: Site, val pages: List<String>)