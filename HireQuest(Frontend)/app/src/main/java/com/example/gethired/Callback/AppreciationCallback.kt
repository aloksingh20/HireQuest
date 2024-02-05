package com.example.gethired.Callback

import com.example.gethired.entities.Appreciation

interface AppreciationCallback {

    fun onAppreciationResponse(appreciation: Appreciation)
    fun onAppreciationError()
}