@file:Suppress("DEPRECATION")

package com.app.unicoffee.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.app.unicoffee.services.ConnectionType
import com.app.unicoffee.services.NetworkMonitorUtil
import com.app.unicoffee.R
import com.app.unicoffee.databinding.ActivityDetailsBinding
import com.squareup.picasso.Picasso
import java.lang.Exception
import kotlin.system.exitProcess

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private val networkMonitor = NetworkMonitorUtil(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        this.setFinishOnTouchOutside(false)

        controlledProcessDetail()
        showDetailsInfo()

        binding.detailsBackToMain.setOnClickListener {
            val detailsBackToMainButton = Intent(this,MainActivity::class.java)
            startActivity(detailsBackToMainButton)
            finish()
        }
    }

    fun showDetailsInfo () {

        val valueFromMain = intent.getSerializableExtra("1")
        val valueFromList = intent.getSerializableExtra("2")

        when {
            valueFromMain == 1 -> {

                val fromMain = intent

                val specialName = fromMain.getSerializableExtra("specialname1")
                val specialLocationName = fromMain.getSerializableExtra("specialLocationName")
                val detailsSpaciels = fromMain.getSerializableExtra("specialsdetails1")
                val imagelink = fromMain.getSerializableExtra("imagelink1") as String

                binding.detailsSpecialName.text = specialName.toString()
                binding.detailsMknName.text = specialLocationName.toString()
                binding.detailsSpecials.text = detailsSpaciels.toString()

                Picasso.get().load(imagelink).fit().centerCrop().into(binding.specialDetailsimage, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        binding.progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@DetailsActivity,"Yükleme sırasında sorun oluştu. Anlayışınızı rica ederiz.", Toast.LENGTH_LONG).show()
                    }

                })

            }
            valueFromList == 2 -> {

                val fromList = intent

                val specialName2 = fromList.getSerializableExtra("specialName2")
                val specialDetails2 = fromList.getSerializableExtra("specialDetails2")
                val imagelink2 = fromList.getSerializableExtra("imagelink2") as String
                val locationName = fromList.getSerializableExtra("locationName2")

                binding.detailsSpecialName.text = specialName2.toString()
                binding.detailsSpecials.text = specialDetails2.toString()
                binding.detailsMknName.text = locationName.toString()

                Picasso.get().load(imagelink2).fit().centerCrop().into(binding.specialDetailsimage,object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        binding.progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@DetailsActivity,"Yükleme sırasında sorun oluştu. Anlayışınızı rica ederiz.", Toast.LENGTH_LONG).show()
                    }

                })

            }
            else -> {

                Toast.makeText(this,"Opss, Sanırım Bir Sorun Var. Lütfen Liste Üzerinden Seçim Yapınız. ", Toast.LENGTH_LONG).show()

                val handlerDetails = Handler()
                handlerDetails.postDelayed({
                    val errorAfterListActivity = Intent(this, ListActivity::class.java)
                    startActivity(errorAfterListActivity)
                },4000)
            }
        }

    }

    override fun onBackPressed() {

        val intent = intent

        if (intent.getSerializableExtra("1") == 1) {

            val detailsPageToMainPage = Intent(this, MainActivity::class.java)
            startActivity(detailsPageToMainPage)

        }else {
            val detailsToList = Intent(this, ListActivity::class.java)
            startActivity(detailsToList)
            finish()
        }

        super.onBackPressed()
    }

    fun controlledProcessDetail() {

        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        when (type) {
                            ConnectionType.Wifi -> {

                                //WHAT IF WIFI IS CONNECTED

                                showDetailsInfo()

                            }
                            ConnectionType.Cellular -> {

                                //WHAT IF CELLULAR IS CONNECTED

                                showDetailsInfo()

                            }
                            else -> { }
                        }
                    }
                    false -> {

                        // ALERT DIALOG EXIT

                        val detailsActivityDialog = AlertDialog.Builder(this)
                        val mainView : View = layoutInflater.inflate(R.layout.dialog_for_layout,null)
                        detailsActivityDialog.setView(mainView)
                        val exitButton : TextView = mainView.findViewById(R.id.exit_app)
                        exitButton.setOnClickListener {
                            moveTaskToBack(true)
                            exitProcess(0)
                        }
                        detailsActivityDialog.setCancelable(false)
                        detailsActivityDialog.show().window!!.setBackgroundDrawableResource(R.drawable.dialog_background)

                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        networkMonitor.register()

    }
    override fun onStop() {
        super.onStop()
        networkMonitor.unregister()

    }

}