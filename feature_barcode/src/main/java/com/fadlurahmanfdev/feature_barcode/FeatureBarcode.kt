package com.fadlurahmanfdev.feature_barcode

import android.media.Image
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.fadlurahmanfdev.feature_barcode.core.callback.BarcodeCallback
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class FeatureBarcode {
    private lateinit var options: BarcodeScannerOptions
    private lateinit var barcodeScanner: BarcodeScanner
    private var callback: BarcodeCallback? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var currentImageProxy: ImageProxy

    fun initialize() {
        options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC,
            )
            .enableAllPotentialBarcodes()
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)
    }

    private fun getInputImageFromImageProxy(image: Image, imageProxy: ImageProxy): InputImage {
        return InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
    }

    @ExperimentalGetImage
    private val runnableProcessImage = Runnable {
        val image = currentImageProxy.image
        if (image != null) {
            val inputImage =
                getInputImageFromImageProxy(image = image, imageProxy = currentImageProxy)
            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    this.callback?.onSuccessScanBarcode(barcodes)
                }
                .addOnFailureListener {
                    this.callback?.onFailedScanBarcode(it)
                }
                .addOnCompleteListener {
                    this.callback?.onCompletedScanBarcode(currentImageProxy)
                }
        } else {
            Log.d(this::class.java.simpleName, "image inside imageProxy didn't detected")
        }
    }

    @ExperimentalGetImage
    fun processImage(imageProxy: ImageProxy, callback: BarcodeCallback) {
        if (this.callback == null) {
            this.callback = callback
        }
        currentImageProxy = imageProxy
        handler.postDelayed(runnableProcessImage, 500)
    }

    fun processImage(inputImage: InputImage): Task<MutableList<Barcode>> {
        return barcodeScanner.process(inputImage)
    }

}