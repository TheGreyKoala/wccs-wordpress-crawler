package de.koalaworks.wccs.wordpresscrawler.wordpress

class Resource(val id: Int, val type: String, val link: String) {
    override fun toString(): String {
        return "Resource(id=$id, type='$type', link='$link')"
    }
}