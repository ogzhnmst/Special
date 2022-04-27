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
    }

    fun showDetailsInfo () {

        val valueFromMain = intent.getSerializableExtra("1")
        val valueFromList = intent.getSerializableExtra("2")

        when {
            valueFromMain == 1 -> {

                val fromMain = intent

                val coffeename = fromMain.getSerializableExtra("coffeename1")
                val coffeehistorical = fromMain.getSerializableExtra("coffeehistorical1")
                val coffeetype = fromMain.getSerializableExtra("coffeetype1")
                val imagelink = fromMain.getSerializableExtra("imagelink1") as String

                binding.detailsCoffeeName.text = coffeename.toString()
                binding.detailsCoffeeHistorical.text = coffeehistorical.toString()
                binding.detailsCoffeeType.text = coffeetype.toString()

                Picasso.get().load(imagelink).fit().centerCrop().into(binding.coffeeImage, object : com.squareup.picasso.Callback {
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

                val coffeename2 = fromList.getSerializableExtra("coffeename2")
                val coffeetype2 = fromList.getSerializableExtra("coffeetype2")
                val coffeehistorical2 = fromList.getSerializableExtra("coffeehistorical2")
                val imagelink2 = fromList.getSerializableExtra("imagelink2") as String

                binding.detailsCoffeeName.text = coffeename2.toString()
                binding.detailsCoffeeHistorical.text = coffeehistorical2.toString()
                binding.detailsCoffeeType.text = coffeetype2.toString()

                Picasso.get().load(imagelink2).fit().centerCrop().into(binding.coffeeImage,object : com.squareup.picasso.Callback {
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