package de.koalaworks.wcts.wordpresscrawler

class PageResult(
    success: Boolean,
    totalItems: Int,
    items: Collection<Page>,
    effectivePageSize: Int,
    erroneousItems: Int) : RequestResult<Page>(success, totalItems, items, effectivePageSize, erroneousItems)