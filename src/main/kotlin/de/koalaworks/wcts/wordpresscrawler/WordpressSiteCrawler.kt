package de.koalaworks.wcts.wordpresscrawler

class WordpressSiteCrawler(val siteUrl: String, pageSize: Int, val requestExecutor: WordpressRequestExecutor) {

    private var totalItems: Int = -1
    private val pageSize: Int

    init {
        // TODO Reference default value from Job class
        this.pageSize = if (pageSize == 0) 8 else this.getHighestPowerOfTwoBeneath(pageSize)
    }

    private fun getHighestPowerOfTwoBeneath(number: Int): Int {
        var powerOf2 = 1
        while (powerOf2 < number) {
            powerOf2 = powerOf2 shl 1
        }
        return powerOf2 shr 1
    }

    fun doIt() {
        doIt(1, pageSize)
        val totalPages = totalItems / pageSize + (if (totalItems % pageSize != 0) 1 else 0)
        for (i in 2..totalPages) {
            doIt(i, pageSize)
        }
    }

    private fun doIt(page: Int, pageSize: Int) {
        val result = requestExecutor.download(page, pageSize)
        if (result.success) {
            if (totalItems == -1) {
                totalItems = result.totalItems
            }
            //return result.items
        } else {
            if (pageSize == 1) {
                println("Page " + page + " (item " + (page - 1) + ") is evil")
            } else {
                doIt(page * 2 - 1, pageSize / 2)
                doIt(page * 2, pageSize / 2)
            }
        }
    }
}