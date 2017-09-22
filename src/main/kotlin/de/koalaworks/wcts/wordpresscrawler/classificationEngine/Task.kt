package de.koalaworks.wcts.wordpresscrawler.classificationEngine

import de.koalaworks.wcts.wordpresscrawler.jobs.Site

data class Task(val site: Site, val pages: List<String>)