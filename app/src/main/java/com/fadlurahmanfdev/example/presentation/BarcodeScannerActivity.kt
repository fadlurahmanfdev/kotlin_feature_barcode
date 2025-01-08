package com.fadlurahmanfdev.example.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.view.PreviewView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.feature_barcode.FeatureBarcode
import com.fadlurahmanfdev.feature_barcode.core.callback.BarcodeCallback
import com.fadlurahmanfdev.kotlin_feature_camera.data.enums.FeatureCameraPurpose
import com.fadlurahmanfdev.kotlin_feature_camera.domain.common.BaseCameraActivity
import com.google.mlkit.vision.barcode.common.Barcode
import java.lang.Exception

class BarcodeScannerActivity : BaseCameraActivity(), BarcodeCallback {
    lateinit var cameraPreview: PreviewView
    lateinit var ivFlash: ImageView
    lateinit var ivCamera: ImageView
    lateinit var ivStopCamera: ImageView
    lateinit var ivSwitch: ImageView
    lateinit var tvResult: TextView
    lateinit var featureBarcode: FeatureBarcode

    override var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    override var cameraPurpose: FeatureCameraPurpose = FeatureCameraPurpose.IMAGE_ANALYSIS
    override var resolutionMode: Int? = ResolutionSelector.PREFER_HIGHER_RESOLUTION_OVER_CAPTURE_RATE
    override var resolutionStrategy: ResolutionStrategy? = ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY

    override fun onCreateBaseCamera(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_barcode_scanner)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        cameraPreview = findViewById<PreviewView>(R.id.cameraPreview)
        ivFlash = findViewById<ImageView>(R.id.iv_flash)
        ivCamera = findViewById<ImageView>(R.id.iv_camera)
        ivStopCamera = findViewById<ImageView>(R.id.iv_stop_camera)
        ivSwitch = findViewById<ImageView>(R.id.iv_switch_camera)
        tvResult = findViewById(R.id.tv_result)

        featureBarcode = FeatureBarcode()
        featureBarcode.initialize()

//        ivCamera.setOnClickListener {
//
//        }
//
//        ivStopCamera.setOnClickListener {
//            ivCamera.visibility = View.VISIBLE
//            ivStopCamera.visibility = View.GONE
//            stopAnalyze()
//        }
    }

    override fun setSurfaceProviderBaseCamera(preview: Preview) {
        preview.setSurfaceProvider(cameraPreview.surfaceProvider)
    }

    var barcodeRawValue: String? = null
    var isScanningBarcode: Boolean = false
    override fun onSuccessScanBarcode(barcodes: List<Barcode>) {
        Log.d(this::class.java.simpleName, "on success scan barcode: ${barcodes.size}")
        barcodes.forEach { barcode ->
            Log.d(this::class.java.simpleName, "barcode raw value: ${barcode.rawValue}")
        }

        for (barcode in barcodes) {
            if (barcode.rawValue != null && barcode.rawValue?.isNotEmpty() == true && barcode.rawValue?.isNotBlank() == true) {
                barcodeRawValue = barcodes.first().rawValue
            }
        }
    }

    override fun onFailedScanBarcode(exception: Exception) {
        Log.d(this::class.java.simpleName, "on failed scan barcode: ${exception.message}")
    }

    override fun onCompletedScanBarcode(imageProxy: ImageProxy) {
        imageProxy.close()
        Log.d(this::class.java.simpleName, "on complete scan barcode")

        if (barcodeRawValue != null && !isScanningBarcode) {
            isScanningBarcode = true
            ivCamera.visibility = View.VISIBLE
            ivStopCamera.visibility = View.GONE
            stopAnalyze()
            Log.d(this::class.java.simpleName, "barcode raw value: $barcodeRawValue")
            val intent = Intent(this, PreviewBarcodeActivity::class.java).apply {
                putExtra("BARCODE_RAW_VALUE", barcodeRawValue)
            }
            startActivity(intent)
        } else {
            Log.d(this::class.java.simpleName, "try to scan another barcode")
        }
    }

    private val handler = Handler()

    override fun onResume() {
        super.onResume()
        barcodeRawValue = null
        isScanningBarcode = false

        ivCamera.visibility = View.GONE
        ivStopCamera.visibility = View.GONE
    }

    @ExperimentalGetImage
    override fun onCameraStarted() {
        startAnalyze { imageProxy ->
            Log.d(this::class.java.simpleName, "start analyze barcode from camera")
            featureBarcode.processImage(imageProxy, this)
        }
    }
}