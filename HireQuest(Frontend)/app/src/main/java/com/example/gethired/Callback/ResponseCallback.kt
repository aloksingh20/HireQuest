package com.example.gethired.Callback

import com.example.gethired.entities.Response

interface ResponseCallback {
    fun onResponseCallback(response:Response)
    fun onErrorResponseCallback()
}