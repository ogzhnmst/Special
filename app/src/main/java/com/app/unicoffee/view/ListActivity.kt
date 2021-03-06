package com.app.unicoffee.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.unicoffee.services.SpecialsData
import com.app.unicoffee.services.ConnectionType
import com.app.unicoffee.services.NetworkMonitorUtil
import com.app.unicoffee.R
import com.app.unicoffee.adapter.SpecialAdapter
import com.app.unicoffee.databinding.ActivityListBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.system.exitProcess


class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private val networkMonitor = NetworkMonitorUtil(this)
    private lateinit var coffeeadapter : SpecialAdapter
    private lateinit var specialsArrayList : ArrayList<SpecialsData>
    private lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityListBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        this.setFinishOnTouchOutside(false)

        db = Firebase.firestore
        specialsArrayList = ArrayList<SpecialsData>()
        getDataList()
        RecyclerListActivity()
        controlledProcessList()


    }


    fun RecyclerListActivity () {

        binding.recyclercoffee.layoutManager = LinearLayoutManager(this)
        coffeeadapter = SpecialAdapter(specialsArrayList)
        binding.recyclercoffee.adapter = coffeeadapter

    }

    override fun onBackPressed() {
        val listToMain = Intent(this, MainActivity::class.java)
        startActivity(listToMain)
        finish()
        super.onBackPressed()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDataList() {
        db.collection("specials").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->

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
                            val date = document.get("date") as Timestamp
                            val specialPrice = document.get("specialPrice") as String
                            val specialLocationName = document.get("specialLocationName") as String


                            val specialData = SpecialsData(specialName,specialDetails,locationlink,imagelink,date,specialPrice,specialLocationName)
                            specialsArrayList.add(specialData)
                        }

                        coffeeadapter.notifyDataSetChanged()

                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun controlledProcessList() {

        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        when (type) {
                            ConnectionType.Wifi -> {

                                //WHAT IF WIFI IS CONNECTED
                                getDataList()
                            }
                            ConnectionType.Cellular -> {

                                //WHAT IF CELLULAR IS CONNECTED
                                getDataList()
                            }
                            else -> { }
                        }
                    }
                    false -> {

                        // ALERT DIALOG EXIT

                        val listActivityDialog = AlertDialog.Builder(this)
                        val mainView : View = layoutInflater.inflate(R.layout.dialog_for_layout,null)
                        listActivityDialog.show().window!!.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                        listActivityDialog.setView(mainView)
                        val exitButton : TextView = mainView.findViewById(R.id.exitApp)
                        exitButton.setOnClickListener {
                            moveTaskToBack(true)
                            exitProcess(0)
                        }
                        listActivityDialog.setCancelable(false)
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