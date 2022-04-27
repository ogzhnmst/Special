package com.app.unicoffee.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.app.unicoffee.services.CoffeeData
import com.app.unicoffee.services.ConnectionType
import com.app.unicoffee.services.NetworkMonitorUtil
import com.app.unicoffee.R
import com.app.unicoffee.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val networkMonitor = NetworkMonitorUtil(this)
    private lateinit var coffeeArrayList : ArrayList<CoffeeData>
    private lateinit var db : FirebaseFirestore
    lateinit var mAdView : AdView

    // ca-app-pub-2824425182560416~9112709702
    // ca-app-pub-2824425182560416/6362423288 == Banner ID
    // ca-app-pub-3940256099942544/6300978111 == Test ID Banner

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        this.setFinishOnTouchOutside(false)

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        db = Firebase.firestore
        coffeeArrayList = ArrayList<CoffeeData>()

        controlledProcessMain()
        getDataMain()

        // İlk commit

        binding.contactText.setOnClickListener {

            val contactDialog = AlertDialog.Builder(this)
            val mainView : View = layoutInflater.inflate(R.layout.contact_layout,null)
            contactDialog.setView(mainView)
            contactDialog.show().window!!.setBackgroundDrawableResource(R.drawable.dialog_background)
            //CONTACT SHOW IN ALERT DIALOG

        }

    }

    override fun onBackPressed() {
        exitProcess(0)
        super.onBackPressed()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDataMain() {
        db.collection("coffees").orderBy("date",
            Query.Direction.DESCENDING).addSnapshotListener { value, error ->

            if (error != null) {
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {

                if (value != null) {
                    if (!value.isEmpty) {

                        val documents = value.documents

                        coffeeArrayList.clear()

                        for (document in documents) {

                            val coffeename = document.get("coffeename") as String
                            val coffeetype = document.get("coffeetype") as String
                            val coffeehistorical = document.get("coffeehistorical") as String
                            val coffeeImageLink = document.get("imagelink") as String
                            val newOrOld = document.get("neworold") as String

                            val coffeeData = CoffeeData(coffeename,coffeetype,coffeehistorical,coffeeImageLink)
                            coffeeArrayList.add(coffeeData)

                            if (newOrOld != null) {

                                if (newOrOld == "new") {

                                    binding.coffeeName.text = coffeename
                                    binding.coffeeDesc.text = coffeehistorical

                                    binding.detailsText.setOnClickListener {
                                        val homeDetailsTextNextActivity = Intent(this,
                                            DetailsActivity::class.java)
                                        val mainToDetailsActivity = 1
                                        homeDetailsTextNextActivity.putExtra("1",mainToDetailsActivity)
                                        homeDetailsTextNextActivity.putExtra("coffeename1",coffeename)
                                        homeDetailsTextNextActivity.putExtra("coffeetype1",coffeetype)
                                        homeDetailsTextNextActivity.putExtra("coffeehistorical1",coffeehistorical)
                                        homeDetailsTextNextActivity.putExtra("imagelink1",coffeeImageLink)
                                        startActivity(homeDetailsTextNextActivity)

                                    }

                                }
                            }else {
                                Toast.makeText(this,"Haftalık Kahve Bilgisine Ulaşılamıyor. Lütfen Liste Üzerinden Seçim Yapınız.",Toast.LENGTH_LONG).show()

                                val handlerMain = Handler()
                                handlerMain.postDelayed({
                                    val errorAfterMainActivity = Intent(this, ListActivity::class.java)
                                    startActivity(errorAfterMainActivity)
                                },4000)
                            }
                        }
                    }
                }
            }
        }
    }

    fun controlledProcessMain() {

        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        when (type) {
                            ConnectionType.Wifi -> {

                                //WHAT IF WIFI IS CONNECTED

                                getDataMain()

                                binding.btnNextList.setOnClickListener {
                                    val homePageNextListButton = Intent(this, ListActivity::class.java)
                                    startActivity(homePageNextListButton)
                                }

                            }
                            ConnectionType.Cellular -> {

                                //WHAT IF CELLULAR IS CONNECTED

                                getDataMain()

                                binding.btnNextList.setOnClickListener {
                                    val homePageNextListButton = Intent(this, ListActivity::class.java)
                                    startActivity(homePageNextListButton)
                                }

                            }
                            else -> { }
                        }
                    }
                    false -> {

                        // ALERT DIALOG EXIT

                        val mainActivityDialog = AlertDialog.Builder(this)
                        val mainView : View = layoutInflater.inflate(R.layout.dialog_for_layout,null)
                        mainActivityDialog.setView(mainView)
                        val exitButton : TextView = mainView.findViewById(R.id.exit_app)
                        exitButton.setOnClickListener {
                            moveTaskToBack(true)
                            exitProcess(0)
                        }
                        mainActivityDialog.setCancelable(false)
                        mainActivityDialog.show().window!!.setBackgroundDrawableResource(R.drawable.dialog_background)

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