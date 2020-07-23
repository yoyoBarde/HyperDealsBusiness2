package com.example.hyperdealsbusiness

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hyperdealsbusiness.Adapter.PromoListAdapter
import com.example.hyperdealsbusiness.Model.PromoModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_fragment_reach.*

class FragmentReach : Fragment() {

    private var myDialogBusiness: Dialog?=null
    private var myDialog: Dialog? = null
    private var promolist = ArrayList<PromoModel>()
    private var mAdapter : PromoListAdapter? = null
    private var mSelected: SparseBooleanArray = SparseBooleanArray()
    val TAG = "Reach"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_fragment_reach, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        promolist = ArrayList()

        val database = FirebaseFirestore.getInstance()

        val layoutManager = LinearLayoutManager(context)
        recyclerViewReach.layoutManager = layoutManager
        recyclerViewReach.itemAnimator = DefaultItemAnimator()

        database.collection("PromoDetails").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (DocumentSnapshot in task.result!!) {
                    val upload = DocumentSnapshot.toObject(PromoModel::class.java)
                    Log.d(TAG, DocumentSnapshot.getId() + " => " + DocumentSnapshot.getData())
                    upload.promoID = DocumentSnapshot.id
                    promolist.add(upload)
                    Log.e(TAG,"sucess")


                }
                Log.e("atay","MANA")
                setAdapter(promolist)


            } else
                Log.e(TAG,"sucess")
        }






    }

    fun setAdapter(promoList:ArrayList<PromoModel>){
        var newlist = ArrayList<PromoModel>()

        for (i in 0 until promoList.size){
            if(promoList[i].posterBy==LoginActivityBusinessman.userBusinessManUsername)
                newlist.add(promolist[i])
        }
        promolist = newlist
        mAdapter = PromoListAdapter(activity!!,mSelected, newlist)
        recyclerViewReach.adapter = mAdapter



    }
}
