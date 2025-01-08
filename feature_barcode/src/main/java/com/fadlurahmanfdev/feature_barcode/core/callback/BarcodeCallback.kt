package com.fadlurahmanfdev.feature_barcode.core.callback

import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.common.Barcode
import java.lang.Exception

interface BarcodeCallback {
    fun onSuccessScanBarcode(barcodes:List<Barcode>)
    fun onFailedScanBarcode(exception: Exception)
    fun onCompletedScanBarcode(imageProxy: ImageProxy)
}