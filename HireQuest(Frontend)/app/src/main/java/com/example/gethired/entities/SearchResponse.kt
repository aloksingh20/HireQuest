package com.example.gethired.entities

data class SearchResponse(
    val content: List<UserProfile>,
    val pageable: PageableInfo,
    val last: Boolean,
    val totalPages: Int,
    val totalElements: Int,
    val number: Int,
    val sort: SortingInfo,
    val first: Boolean,
    val size: Int,
    val numberOfElements: Int,
    val empty: Boolean
)

data class PageableInfo(
    val sort: SortingInfo,
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val unpaged: Boolean
)

data class SortingInfo(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)
