package de.koalaworks.wcts.wordpresscrawler

class Page (val id: Int, val link: String) {
    override fun toString(): String {
        return "Page(id=$id, link='$link')"
    }
}