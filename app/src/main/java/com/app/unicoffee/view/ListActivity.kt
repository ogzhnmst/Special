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
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.unicoffee.services.CoffeeData
import com.app.unicoffee.services.ConnectionType
import com.app.unicoffee.services.NetworkMonitorUtil
import com.app.unicoffee.R
import com.app.unicoffee.adapter.CoffeeAdapter
import com.app.unicoffee.databinding.ActivityListBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.system.exitProcess


class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private val networkMonitor = NetworkMonitorUtil(this)
    private lateinit var coffeeadapter : CoffeeAdapter
    private lateinit var coffeeArrayList : ArrayList<CoffeeData>
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
        coffeeArrayList = ArrayList<CoffeeData>()
        getDataList()
        RecyclerListActivity()
        controlledProcessList()


    }


    fun RecyclerListActivity () {

        binding.recyclercoffee.layoutManager = LinearLayoutManager(this)
        coffeeadapter = CoffeeAdapter(coffeeArrayList)
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
        db.collection("coffees").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->

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

                            println(coffeename)

                            val coffeeData = CoffeeData(coffeename,coffeetype,coffeehistorical,coffeeImageLink)
                            coffeeArrayList.add(coffeeData)
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
                        listActivityDialog.setView(mainView)
                        val exitButton : TextView = mainView.findViewById(R.id.exit_app)
                        exitButton.setOnClickListener {
                            moveTaskToBack(true)
                            exitProcess(0)
                        }
                        listActivityDialog.setCancelable(false)
                        listActivityDialog.show().window!!.setBackgroundDrawableResource(R.drawable.dialog_background)



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