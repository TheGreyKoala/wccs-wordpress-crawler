package de.koalaworks.wcts.wordpresscrawler

class WordpressResource(val id: Int, val type: String, val link: String) {
    override fun toString(): String {
        return "WordpressResource(id=$id, link='$link')"
    }
}