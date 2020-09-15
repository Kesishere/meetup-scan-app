package com.kes.meetupscanapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.google.gson.Gson
import com.google.zxing.Result
import com.kes.meetupscanapp.databinding.ActivityMainBinding
import com.kes.meetupscanapp.db.AppDatabase
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), DecodeCallback, CoroutineScope {

    private lateinit var binding: ActivityMainBinding
    private lateinit var codeScanner: CodeScanner
    private var mPermissionGranted: Boolean = false
    private val appDatabase: AppDatabase? by lazy { App.appDatabase }
    private val job = Job()
    private var user: UserModel? = null

    companion object {
        const val PERMISSION_REQUEST = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        codeScanner = CodeScanner(this, binding.codeScanner)
        codeScanner.decodeCallback = this

        codeScanner!!.setErrorCallback {
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Scan error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        requestCameraPermission()

        binding.print.setOnClickListener {
            startPrint()
        }

//        val client = OkHttpClient()
//
//
//        val body: RequestBody = "kek".toRequestBody()
//        val request: Request = Request.Builder()
//            .url("https://kkin.ru/api/qr_check/aGVsbG8gd29ybGQ=")
//            .post(body)
//            .build()
//
//        launch(Dispatchers.IO) { client.newCall(request).execute().use { response ->  Timber.d(response.body.toString()) }  }


    }

    private fun startPrint() {
        this.also { context ->
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val jobName = "${context.getString(R.string.app_name)} Document"
            val printAttributes = PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A5).build()
            printManager.print(jobName, CardPrintAdapter(context, user), printAttributes)
        }
    }

    private fun requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = false
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST)
            } else {
                mPermissionGranted = true
                codeScanner.startPreview()
            }
        } else {
            mPermissionGranted = true
            codeScanner.startPreview()

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

    override fun onDecoded(decodeResult: Result) {
        runBlocking {
            if (decodeResult.text != null) {
                val client = OkHttpClient()

                val dataRequest = async(Dispatchers.IO) {
                    val request: Request = Request.Builder()
                        .url("https://hookah.vcdev.online/api/qr_check/${decodeResult.text}")
                        .build()

                    client.newCall(request).execute()
                        .use { response -> return@async response.body?.string() }
                }

                val data = dataRequest.await()

                user = Gson().fromJson(data, UserModel::class.java)
                withContext(Dispatchers.Main){ binding.print.visibility = View.VISIBLE}


            } else {
                Timber.w("Unable to get text result: %s", decodeResult.toString())
            }

            if (!codeScanner.isPreviewActive) {
                codeScanner.startPreview()
            }
        }
    }

    fun showSuccess() {
        binding.resultIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check))
        binding.resultText.text = getString(R.string.success)

        launch {
            delay(2000 /* Нужно выбрать */)

            binding.resultIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.ic_qr_code
                )
            )
            binding.resultText.text = getString(R.string.stub)

        }
    }

    fun showError() {
        binding.resultIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_error))
        binding.resultText.text = "Ошибка сканирования!" //@TODO какие ошибки могут быть

        launch {
            delay(2000 /* Нужно выбрать */)

            binding.resultIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.ic_qr_code
                )
            )
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