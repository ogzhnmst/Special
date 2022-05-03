package com.app.unicoffee.view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.app.unicoffee.services.SpecialsData
import com.app.unicoffee.services.ConnectionType
import com.app.unicoffee.services.NetworkMonitorUtil
import com.app.unicoffee.R
import com.app.unicoffee.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.lang.Exception
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val networkMonitor = NetworkMonitorUtil(this)
    private lateinit var specialsArrayList : ArrayList<SpecialsData>
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
        specialsArrayList = ArrayList<SpecialsData>()

        controlledProcessMain()
        getDataMain()

    }

    override fun onBackPressed() {
        exitProcess(0)
        super.onBackPressed()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDataMain() {
        db.collection("specials").orderBy("date",
            Query.Direction.DESCENDING).addSnapshotListener { value, error ->

            if (error != null) {
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {

                if (value != null) {
                    if (!value.isEmpty) {

                        val documents = value.documents

                        specialsArrayList.clear()

                        for (document in documents) {

                            val specialName = document.get("specialName") as String
                            val specialDetails = document.get("specialDetails") as String
                            val locationlink = document.get("locationlink") as String
                            val imagelink = document.get("imagelink") as String
                            val newOrOld = document.get("neworold") as String
                            val date = document.get("date") as Timestamp
                            val specialPrice = document.get("specialPrice") as String
                            val specialLocationName = document.get("specialLocationName") as String

                            binding.specialPricetext.text = specialPrice

                            val specialData = SpecialsData(specialName,specialDetails,locationlink,imagelink,date,specialPrice,specialLocationName)
                            specialsArrayList.add(specialData)

                            binding.locationButton.setOnClickListener {

                                if (true) {
                                    val getMapsURL = Uri.parse(locationlink)
                                    val mapIntent = Intent(Intent.ACTION_VIEW,getMapsURL)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    startActivity(mapIntent)
                                }else {
                                    Toast.makeText(this,"Sanırım Konum bilgisinde bir hata almaktayız. Lütfen daha sonra tekrar deneyiniz", Toast.LENGTH_LONG).show()
                                }
                            }

                            if (newOrOld != null) {

                                if (newOrOld == "n") {

                                    Picasso.get().load(imagelink).fit().centerCrop().into(binding.mainImageView, object : com.squareup.picasso.Callback {
                                        override fun onSuccess() {
                                            binding.progressBarMain.visibility = View.GONE
                                        }

                                        override fun onError(e: Exception?) {
                                            binding.progressBarMain.visibility = View.GONE
                                            Toast.makeText(this@MainActivity,"Yükleme sırasında sorun oluştu. Anlayışınızı rica ederiz.", Toast.LENGTH_LONG).show()
                                        }

                                    })

                                    binding.specialName.text = specialName

                                    binding.descButton.setOnClickListener {
                                        val homeDetailsTextNextActivity = Intent(this,
                                            DetailsActivity::class.java)
                                        val mainToDetailsActivity = 1
                                        homeDetailsTextNextActivity.putExtra("1",mainToDetailsActivity)
                                        homeDetailsTextNextActivity.putExtra("specialname1",specialName)
                                        homeDetailsTextNextActivity.putExtra("specialLocationName",specialLocationName)
                                        homeDetailsTextNextActivity.putExtra("specialsdetails1",specialDetails)
                                        homeDetailsTextNextActivity.putExtra("imagelink1",imagelink)
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

                                binding.nextListActivityButton.setOnClickListener {
                                    val homePageNextListButton = Intent(this, ListActivity::class.java)
                                    startActivity(homePageNextListButton)
                                }

                            }
                            ConnectionType.Cellular -> {

                                //WHAT IF CELLULAR IS CONNECTED

                                getDataMain()

                                binding.nextListActivityButton.setOnClickListener {
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