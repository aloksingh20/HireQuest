package com.example.gethired.Callback

import com.example.gethired.entities.SearchResponse

interface SearchUserProfileCallback {
    fun onSearchResponse(userProfiles: SearchResponse)
    fun onSearchError()
}
