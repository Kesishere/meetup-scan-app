package com.kes.meetupscanapp

import android.content.Context
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kes.meetupscanapp.databinding.DialogUserBinding
import timber.log.Timber

class UserActivity : AppCompatActivity() {

    private lateinit var binding: DialogUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userExtra = intent.getParcelableExtra<UserModel>("user")
        if (userExtra != null) {
            val user = userExtra.user
            binding.fio.text = "${user?.surname} ${user?.name} ${user?.middle_name ?: ""}"
            binding.company.text = "${user?.company}"
            binding.job.text = "${user?.position}"
            binding.number.text = "${user?.phone}"
            binding.email.text = "${user?.email}"


            Timber.d(user.toString())
            binding.print.visibility = View.VISIBLE

            binding.print.setOnClickListener {
                startPrint(userExtra)
            }
        }
    }


    private fun startPrint(user: UserModel) {
        this.also { context ->
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val jobName = "${context.getString(R.string.app_name)} Document"
            val printAttributes =
                PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.NA_INDEX_4X6)
                    .build()
            printManager.print(jobName, CardPrintAdapter(context, user), printAttributes)
        }
    }
}