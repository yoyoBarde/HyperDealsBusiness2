package com.example.hyperdealsbusiness.Adapter

import com.example.hyperdealsbusiness.AddPromo

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hyperdealsbusiness.R
import kotlinx.android.synthetic.main.model_subcategory_business_all.view.*


class SelectedSubcategoryAdapterBusiness (var context: Context, var selectedList : ArrayList<String>) : RecyclerView.Adapter<SelectedSubcategoryAdapterBusiness.ViewHolder>(){


    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val mySubcategoryName =view.tv_subcategoryname!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.model_subcategory_business_all,parent,false))

    override fun getItemCount(): Int = selectedList.size


    override fun onBindViewHolder(holder: SelectedSubcategoryAdapterBusiness.ViewHolder, position: Int) {
        val mysubCategory = selectedList[position]
        if(position==itemCount-1)
            holder.mySubcategoryName.text = mysubCategory

        else{
            holder.mySubcategoryName.text = "$mysubCategory,"


        }

    }





}