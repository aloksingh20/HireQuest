package com.example.gethired.SealedClasses

sealed class CommonResult{
    object Success : CommonResult()
    data class Failure(val error: Errors) : CommonResult()
}
