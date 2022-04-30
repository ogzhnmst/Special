package com.app.unicoffee.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.unicoffee.services.SpecialsData
import com.app.unicoffee.view.DetailsActivity
import com.app.unicoffee.databinding.CoffeeRowBinding
import com.squareup.picasso.Picasso
import java.lang.Exception

class CoffeeAdapter(private var specialsList: ArrayList<SpecialsData>) : RecyclerView.Adapter<CoffeeAdapter.CoffeeHolder>() {

    private lateinit var context: Context

    class CoffeeHolder (val binding: CoffeeRowBinding) : RecyclerView.ViewHolder (binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeHolder {
        val binding = CoffeeRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CoffeeHolder(binding)
        println(specialsList)
    }

    override fun onBindViewHolder(holder: CoffeeHolder, position: Int) {

        Picasso.get().load(specialsList[position].imagelink).fit().centerCrop().into(holder.binding.cardImage, object : com.squareup.picasso.Callback {
            override fun onSuccess() {
                holder.binding.progressBar.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                holder.binding.progressBar.visibility = View.GONE
            }

        })
        holder.binding.dateText.text = specialsList[position].date.toDate().toString()
        holder.binding.nametext.text = specialsList[position].specialName
        holder.binding.detailstext.text = specialsList[position].specialDetails
        holder.itemView.setOnClickListener {
            val listToDetailsActivity = Intent(holder.itemView.context, DetailsActivity::class.java)
            val listToDetailsData = 2
            listToDetailsActivity.putExtra("2",listToDetailsData)
            listToDetailsActivity.putExtra("specialName2",specialsList[position].specialName)
            listToDetailsActivity.putExtra("specialDetails2",specialsList[position].specialDetails)
            listToDetailsActivity.putExtra("imagelink2",specialsList[position].imagelink)
            listToDetailsActivity.putExtra("locationName2",specialsList[position].specialLocationName)
            holder.itemView.context.startActivity(listToDetailsActivity)
        }
    }

    override fun getItemCount(): Int {
        return specialsList.size
    }
}