package com.fadlurahmanfdev.example.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.data.FeatureModel
import com.fadlurahmanfdev.feature_barcode.FeatureBarcode
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage

class MainActivity : AppCompatActivity(), ListExampleAdapter.Callback {
    lateinit var featureBarcode: FeatureBarcode

    private val features: List<FeatureModel> = listOf<FeatureModel>(
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Barcode Scanner",
            desc = "Barcode Scanner",
            enum = "BARCODE_SCANNER"
        ),
        FeatureModel(
            featureIcon = R.drawable.baseline_developer_mode_24,
            title = "Pick Image",
            desc = "Pick Image",
            enum = "PICK_IMAGE"
        ),
    )

    private lateinit var rv: RecyclerView
    private lateinit var main: ConstraintLayout

    private lateinit var adapter: ListExampleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        rv = findViewById<RecyclerView>(R.id.rv)
        main = findViewById(R.id.main)

        rv.setItemViewCacheSize(features.size)
        rv.setHasFixedSize(true)

        adapter = ListExampleAdapter()
        adapter.setCallback(this)
        adapter.setList(features)
        adapter.setHasStableIds(true)
        rv.adapter = adapter

        featureBarcode = FeatureBarcode()
        featureBarcode.initialize()
    }

    override fun onClicked(item: FeatureModel) {
        when (item.enum) {
            "BARCODE_DETECTION" -> {
                val intent = Intent(this, BarcodeScannerActivity::class.java)
                startActivity(intent)
            }

            "PICK_IMAGE" -> {
                pickImageLauncher.launch("image/*")
            }
        }
    }

    val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            featureBarcode.processImage(InputImage.fromFilePath(this, uri)).addOnSuccessListener { barcodes ->
                if(barcodes.isNotEmpty()){
                    val intent = Intent(this, PreviewBarcodeActivity::class.java).apply {
                        putExtra("BARCODE_RAW_VALUE", barcodes.first().rawValue)
                    }
                    startActivity(intent)
                }else{
                    val snackbar = Snackbar.make(this, main.rootView, "No barcode detected", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
            }.addOnFailureListener {
                val snackbar = Snackbar.make(this, main.rootView, "Failed to detect barcode: ${it.message}", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        }
    }
}