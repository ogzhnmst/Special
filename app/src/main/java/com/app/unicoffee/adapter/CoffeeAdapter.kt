package com.app.unicoffee.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.unicoffee.services.CoffeeData
import com.app.unicoffee.view.DetailsActivity
import com.app.unicoffee.databinding.CoffeeRowBinding

open class CoffeeAdapter(private var coffeeList: ArrayList<CoffeeData>) : RecyclerView.Adapter<CoffeeAdapter.CoffeeHolder>() {

    class CoffeeHolder (val binding: CoffeeRowBinding) : RecyclerView.ViewHolder (binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeHolder {
        val binding = CoffeeRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CoffeeHolder(binding)
    }

    override fun onBindViewHolder(holder: CoffeeHolder, position: Int) {

        holder.binding.coffeeNameList.text = coffeeList[position].coffeeName
        holder.itemView.setOnClickListener {
            val listToDetailsActivity = Intent(holder.itemView.context, DetailsActivity::class.java)
            val listToDetailsData = 2
            listToDetailsActivity.putExtra("2",listToDetailsData)
            listToDetailsActivity.putExtra("coffeename2",coffeeList[position].coffeeName)
            listToDetailsActivity.putExtra("coffeetype2",coffeeList[position].coffeeType)
            listToDetailsActivity.putExtra("coffeehistorical2",coffeeList[position].coffeeHistorical)
            listToDetailsActivity.putExtra("imagelink2",coffeeList[position].imageLink)
            holder.itemView.context.startActivity(listToDetailsActivity)
        }
    }

    override fun getItemCount(): Int {
        return coffeeList.size
    }
}