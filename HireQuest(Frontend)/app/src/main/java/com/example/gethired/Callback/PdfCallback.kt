package com.example.gethired.Callback

import com.example.gethired.entities.Pdf
import com.example.gethired.entities.Resume

interface PdfCallback {

    fun onPdfCallback(pdf: Pdf)
    fun onPdfError(error:String)
}