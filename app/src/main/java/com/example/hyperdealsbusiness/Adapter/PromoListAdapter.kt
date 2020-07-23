package com.example.hyperdealsbusiness.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.hyperdealsbusiness.LoginActivityBusinessman
import com.example.hyperdealsbusiness.Model.*
import com.example.hyperdealsbusiness.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.notification_layout_row.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

class PromoListAdapter(val context: Context, private val selectedItem: SparseBooleanArray, private val promolist : ArrayList<PromoModel>) : RecyclerView.Adapter<PromoListAdapter.ViewHolder>(){
    companion object {
        lateinit  var promoProfile: PromoModel
    }
    val database = FirebaseFirestore.getInstance()
    val TAG = "PromoListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):

            ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notification_layout_row,parent,false))


    override fun getItemCount(): Int  = promolist.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val promos = promolist[position]


        Picasso.get()
            .load(promos.promoImageLink)
            .placeholder(R.drawable.hyperdealslogofinal)
            .into(holder.ivPromoImage)

        /* holder.ivPromoImage.setImageResource(R.drawable.bench)*/

        holder.tvPromoTitile.text = promos.promoname
        holder.tvPromoDescription.text = promos.promodescription
        holder.tvPromoLocation.text = promos.promoPlace
        holder.tvPromoContact.text = promos.promoContactNumber
        holder.tvPromoStore.text = promos.promoStore

        holder.container.setOnClickListener { showDialog(position) }


        holder.container.isSelected = selectedItem.get(position,false)

    }



    inner class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        val ivPromoImage = view.PromoImage!!
        val tvPromoTitile = view.PromoTitle!!
        val tvPromoDescription = view.PromoDescription!!
        val tvPromoLocation = view.PromoPlace!!
        val tvPromoContact = view.PromoConctact!!
        val tvPromoStore = view.PromoStore!!




        val container = view.PromoContainer!!
    }




    fun showDialog(position:Int) {



        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogbox, null)

        dialogBuilder.setCancelable(false)

        dialogBuilder.setView(dialogView)




        val interested = dialogView.findViewById(R.id.interested) as ImageView
        val call = dialogView.findViewById(R.id.call) as ImageView
        val showNavigation = dialogView.findViewById(R.id.map) as ImageView
        val promoPicture = dialogView.findViewById(R.id.promoPicture) as ImageView
        val promoStore = dialogView.findViewById(R.id.promoSTORE) as TextView
        val promoDescription = dialogView.findViewById(R.id.promoDESCRIPTION) as TextView
        val promoName = dialogView.findViewById(R.id.promoNAME) as TextView
        val promoNumber = dialogView.findViewById(R.id.promoNUMBER) as TextView
        val promoLocation = dialogView.findViewById(R.id.promoLOCATION) as TextView
        val ccntainer = dialogView.findViewById(R.id.specificPromoContainer) as ConstraintLayout



        var promoLikes = 0
        database.collection("PromoIntrested").document(promolist[position].promoStore).
        get().addOnSuccessListener { document ->

            if(document.exists())
            {

                var promoLikeCountParce = document.toObject(promoLikesCountParce::class.java)
                promoLikes = promoLikeCountParce!!.LikeCount
                Log.e(TAG,"promolikes ${promoLikes}")
            }
            else{
                Log.e(TAG,"dont exist")
            }

        }





        var likeRetrieved = false
        database.collection("PromoIntrested").document(promolist[position].promoStore).
        collection("interested_users").document(LoginActivityBusinessman.userBusinessManUsername).get().addOnSuccessListener { document -> Log.e(TAG,"Naa")

            if(document.exists())
            {

                var userLikeParce = document.toObject(userLikeParce::class.java)
                likeRetrieved =true

                interested.setImageResource(R.drawable.ic_interest_like)
            }
            else{
                Log.e(TAG,"dont exist")
                likeRetrieved =false

            }

        }.addOnFailureListener {

            Log.e(TAG,"WALA")
        }




        Picasso.get()
            .load(promolist[position].promoImageLink)
            .placeholder(R.drawable.hyperdealslogofinal)
            .into(promoPicture)
        promoStore.text = promolist[position].promoStore
        promoDescription.text = promolist[position].promodescription
        promoName.text = promolist[position].promoname
        promoNumber.text = promolist[position].promoContactNumber
        promoLocation.text = promolist[position].promoPlace



        val b = dialogBuilder.create()
        b.show()
        b.setCancelable(true)


        ccntainer.setOnClickListener {
            incrementPromos(promolist[position])





            b.dismiss()
//            val intent = Intent(context, Business_PromoProfile::class.java)
//            PromoListAdapter.promoProfile = promolist[position]
//            context.startActivity(intent)



        }
        interested.setOnClickListener {
            Log.e(TAG,"atay")
            doAsync {


                if (!likeRetrieved) {
                    userLikedSubcategory(promolist[position])

                    uiThread {     interested.setImageResource(R.drawable.ic_interest_like)}
                    var myUserLike = userLike(LoginActivityBusinessman.userBusinessManUsername, true)
                    database.collection("PromoIntrested").document(promolist[position].promoStore)
                        .collection("interested_users").document(LoginActivityBusinessman.userBusinessManUsername).set(myUserLike).addOnCompleteListener {
                        Log.e(TAG, "liked")
                        likeRetrieved = true
                        database.collection("UserLikes").document(LoginActivityBusinessman.userBusinessManUsername).collection("Promo").document(promolist[position].promoStore).set(userPromoiked(promolist[position].promoStore))
                        database.collection("PromoIntrested").document(promolist[position].promoStore).set(
                            promoLikesCount(promoLikes+1)
                        )
                    }

                } else {
                    userLikedSubcategoryRemoved(promolist[position])

                    uiThread {   interested.setImageResource(R.drawable.interested)}
                    database.collection("PromoIntrested").document(promolist[position].promoStore).collection("interested_users").document(LoginActivityBusinessman.userBusinessManUsername).delete().addOnCompleteListener {
                        Log.e(TAG, "deleted")
                        likeRetrieved = false
                        database.collection("UserLikes").document(LoginActivityBusinessman.userBusinessManUsername).collection("Promo").document(promolist[position].promoStore).delete()
                        database.collection("PromoIntrested").document(promolist[position].promoStore).set(promoLikesCount(promoLikes-1))

                    }


                }

            }


        }


        call.setOnClickListener {

            var intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${promolist[position].promoContactNumber}")
            context.startActivity(intent)

        }
        showNavigation.setOnClickListener {

            val gmmIntentUri = Uri.parse("google.navigation:q="+ promolist[position].promoLatLng)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.`package` = "com.google.android.apps.maps"
            context.startActivity(mapIntent)
        }
    }
    fun incrementPromos(myModel: PromoModel){


    }

    fun userLikedSubcategory(myPromo:PromoModel) {
        Log.e(TAG, "userViewedSubcategory")
        database.collection("PromoData").document("PromoLikes").collection("Promos").document(myPromo.promoID).collection("Users").document(LoginActivityBusinessman.userBusinessManUsername).set(LoginActivityBusinessman.userBusinessManUsername).addOnSuccessListener {
            Log.e(TAG, "Store is fucking satored")

        }
        for (i in 0 until myPromo.subcategories.size) {
            doAsync {

                var prevCountLiked = 0

                database.collection("UserLikedPreferences").document(LoginActivityBusinessman.userBusinessManUsername).collection("Subcategories").document(myPromo.subcategories[i]).get().addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val document = task.result
                        if (document!!.exists()) {

                            var mySubcategoryPref = document!!.toObject(UserSubcategoriesPreferencesParcelable::class.java)
                            prevCountLiked = 1
                            Log.e(TAG, "Cached document data: ${document?.data}")

                            var mySubcategoryPreference = UserSubcategoriesPreferences(myPromo.subcategories[i], prevCountLiked)
                            database.collection("UserLikedPreferences").document(LoginActivityBusinessman.userBusinessManUsername).collection("Subcategories").document(myPromo.subcategories[i]).set(mySubcategoryPreference).addOnCompleteListener {


                                Log.e(TAG, "UserLikedPreferences Success")
                            }
                        } else {
                            Log.e(TAG, "Cached get failed: ", task.exception)
                            var mySubcategoryPreference = UserSubcategoriesPreferences(myPromo.subcategories[i], 1)
                            database.collection("UserLikedPreferences").document(LoginActivityBusinessman.userBusinessManUsername).collection("Subcategories").document(myPromo.subcategories[i]).set(mySubcategoryPreference).addOnCompleteListener {


                                Log.e(TAG, "UserLikedPreferences Success")
                            }
                        }

                    } else {
                        Log.e(TAG, "Cached get failed: ", task.exception)
                        var mySubcategoryPreference = UserSubcategoriesPreferences(myPromo.subcategories[i], 1)
                        database.collection("UserLikedPreferences").document(LoginActivityBusinessman.userBusinessManUsername).collection("Subcategories").document(myPromo.subcategories[i]).set(mySubcategoryPreference).addOnCompleteListener {


                            Log.e(TAG, "UserLikedPreferences Success")
                        }
                    }

                }




            }
        }
    }
    fun userLikedSubcategoryRemoved(myPromo:PromoModel) {
        Log.e(TAG, "userViewedSubcategory")
        database.collection("PromoData").document("PromoLikes").collection("Promos")
            .document(myPromo.promoID).collection("Users").document(LoginActivityBusinessman.userBusinessManUsername).delete()


        for (i in 0 until myPromo.subcategories.size) {
            doAsync {

                var prevCountLiked = 0

                database.collection("UserLikedPreferences").document(LoginActivityBusinessman.userBusinessManUsername).collection("Subcategories").document(myPromo.subcategories[i]).get().addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val document = task.result
                        if (document!!.exists()) {

                            var mySubcategoryPref = document!!.toObject(UserSubcategoriesPreferencesParcelable::class.java)
                            prevCountLiked = 0
                            Log.e(TAG, "Cached document data: ${document?.data}")

                            var mySubcategoryPreference = UserSubcategoriesPreferences(myPromo.subcategories[i], prevCountLiked)
                            database.collection("UserLikedPreferences").document(LoginActivityBusinessman.userBusinessManUsername).collection("Subcategories").document(myPromo.subcategories[i]).set(mySubcategoryPreference).addOnCompleteListener {


                                Log.e(TAG, "UserLikedPreferences Success")
                            }
                        } else {
                            Log.e(TAG, "Cached get failed: ", task.exception)
                            var mySubcategoryPreference = UserSubcategoriesPreferences(myPromo.subcategories[i], 1)
                            database.collection("UserLikedPreferences").document(LoginActivityBusinessman.userBusinessManUsername).collection("Subcategories").document(myPromo.subcategories[i]).set(mySubcategoryPreference).addOnCompleteListener {


                                Log.e(TAG, "UserLikedPreferences Success")
                            }
                        }

                    } else {
                        Log.e(TAG, "Cached get failed: ", task.exception)
                        var mySubcategoryPreference = UserSubcategoriesPreferences(myPromo.subcategories[i], 1)
                        database.collection("UserLikedPreferences").document(LoginActivityBusinessman.userBusinessManUsername).collection("Subcategories").document(myPromo.subcategories[i]).set(mySubcategoryPreference).addOnCompleteListener {


                            Log.e(TAG, "UserLikedPreferences Success")
                        }
                    }

                }




            }
        }
    }
}