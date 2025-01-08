package com.fadlurahmanfdev.example.presentation

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fadlurahmanfdev.example.R
import com.fadlurahmanfdev.example.data.SharedModel


class PreviewBarcodeActivity : AppCompatActivity() {
    //    lateinit var imageView: CircleImageView
    lateinit var imageView: ImageView
    lateinit var tvSummary: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_preview_barcode)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.d(this::class.java.simpleName, "on preview barcode")
        imageView = findViewById(R.id.previewImage)
        imageView.visibility = View.GONE

        tvSummary = findViewById(R.id.tv_summary)
        val barcodeRawValue = intent.extras?.getString("BARCODE_RAW_VALUE", "TES-TES")
        tvSummary.visibility = View.VISIBLE
        tvSummary.text = barcodeRawValue

//        val bitmapImage = SharedModel.bitmap
//        val newBitmapImage = Bitmap.createBitmap(
//            bitmapImage,
//            0,
//            0,
//            bitmapImage.width,
//            bitmapImage.height,
//        )
//        imageView.setImageBitmap(newBitmapImage)
    }
}