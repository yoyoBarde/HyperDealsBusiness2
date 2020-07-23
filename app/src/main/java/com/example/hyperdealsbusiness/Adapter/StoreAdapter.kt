package com.example.hyperdealsbusiness.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hyperdealsbusiness.Model.StoreModel
import com.example.hyperdealsbusiness.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.model_similar_promo.view.*
import java.util.ArrayList

class StoreAdapter(private val context: Context, private val storeList : ArrayList<StoreModel>) : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    val database = FirebaseFirestore.getInstance()
    val TAG = "PreferredAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            StoreAdapter.ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.model_similar_promo, parent, false))

    override fun getItemCount(): Int = storeList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val myStore = storeList[position]

        Picasso.get()
            .load(myStore.storeImage)
            .placeholder(R.drawable.hyperdealslogofinal)
            .into(holder.ivHomeImage)

        holder.tvHomeText.text = myStore.storeName
        holder.tvHomeStore.text = myStore.storeAddress


    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivHomeImage = view.iv_promoImage!!
        val tvHomeText = view.tv_promoName!!
        val tvHomeStore = view.tv_promoStore!!
        val containter = view.containerLinear!!
    }


}
