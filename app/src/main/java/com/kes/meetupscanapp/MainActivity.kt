package com.kes.meetupscanapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.google.zxing.Result
import com.kes.meetupscanapp.databinding.ActivityMainBinding
import com.kes.meetupscanapp.db.AppDatabase
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), DecodeCallback, CoroutineScope {

    private lateinit var binding: ActivityMainBinding
    private lateinit var codeScanner: CodeScanner
    private var mPermissionGranted: Boolean = false
    private val appDatabase: AppDatabase? by lazy { App.appDatabase }
    private val job = Job()

    companion object {
        const val PERMISSION_REQUEST = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        codeScanner = CodeScanner(this, binding.codeScanner)
        codeScanner.decodeCallback = this

        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = false
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST)
            } else {
                mPermissionGranted = true
            }
        } else {
            mPermissionGranted = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true
                codeScanner.startPreview()
            } else {
                mPermissionGranted = false
            }
        }
    }

    override fun onDecoded(result: Result) {
        Timber.d(result.text)

    }

    fun showSuccess() {
        binding.resultIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check))
        binding.resultText.text = getString(R.string.success)

        launch {
            delay(2000 /* Нужно выбрать */)

            binding.resultIcon.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_qr_code))
            binding.resultText.text = getString(R.string.stub)

        }
    }

    fun showError(){
        binding.resultIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_error))
        binding.resultText.text = "Ошибка сканирования!" //@TODO какие ошибки могут быть

        launch {
            delay(2000 /* Нужно выбрать */)

            binding.resultIcon.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_qr_code))
            binding.resultText.text = getString(R.string.stub)

        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}