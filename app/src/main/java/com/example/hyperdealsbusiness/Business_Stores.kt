package com.example.hyperdealsbusiness

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hyperdealsbusiness.Adapter.StoreAdapter
import com.example.hyperdealsbusiness.Model.StoreModel
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_business__stores.*
import org.jetbrains.anko.doAsync

class Business_Stores : AppCompatActivity() {
    var database = FirebaseFirestore.getInstance()
    val TAG = "BusinessStores"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business__stores)
        getUserStores()
        btn_add_store.setOnClickListener { startActivity(Intent(this, AddStore::class.java)) }
    }
    fun getUserStores() {
        var storeList = ArrayList<StoreModel>()
        database.collection("UserBusinessman").document(LoginActivityBusinessman.userBusinessManUsername).collection("Stores").addSnapshotListener(
            EventListener<QuerySnapshot> { snapshots, e ->
            if (e != null) {
                Log.w(TAG, "listen:error", e)
                return@EventListener
            }

            for (dc in snapshots!!.documentChanges) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    val storeName = dc.document.data.get("storeName").toString()
                    val storeDescription = dc.document.data.get("storeDescription").toString()
                    val storeAddress = dc.document.data.get("storeAddress").toString()
                    val storeBy  = dc.document.data.get("storeBy").toString()
                    val storeContact= dc.document.data.get("storeContact").toString()
                    val storeLink = dc.document.data.get("storeLink").toString()
                    val storeOpenTime  = dc.document.data.get("storeOpenTime").toString()
                    val storeCloseTime = dc.document.data.get("storeCloseTime").toString()
                    val storeCategories = dc.document.data.get("storeCategories") as ArrayList<String>
                    var storeLatLng = dc.document.data.get("storeLatLng") as GeoPoint
                    val storeImage  = dc.document.data.get("storeImage").toString()

                    val myStore  = StoreModel(storeImage,storeName,storeContact,storeDescription
                        ,storeLink, storeLatLng ,storeAddress, storeCategories,storeOpenTime,storeCloseTime,storeBy)
                    storeList.add(myStore)

                    var myAdapter = StoreAdapter(this, storeList)
                    recyclerStore.layoutManager = GridLayoutManager(this, 2)
                    recyclerStore.adapter = myAdapter
                    Log.e(TAG, "New Item: ${dc.document.data} ${myStore.storeName}")

                }
            }
        })

    }


}