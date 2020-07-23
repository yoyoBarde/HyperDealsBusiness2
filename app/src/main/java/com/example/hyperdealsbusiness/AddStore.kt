package com.example.hyperdealsbusiness

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import android.widget.TimePicker
import com.example.hyperdealsbusiness.Adapter.SelectedSubcategoryAdapterBusiness
import com.example.hyperdealsbusiness.Model.CategoryParse
import com.example.hyperdealsbusiness.Model.StoreModel
import com.example.hyperdealsbusiness.Model.SubcategoryParse
import com.example.hyperdealsbusiness.Model.myInterfaces

import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.api.Status

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_promo.*
import kotlinx.android.synthetic.main.activity_add_store.*
import kotlinx.android.synthetic.main.activity_add_store.btnGetLocation
import kotlinx.android.synthetic.main.activity_add_store.btn_add_categoru

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.lang.Exception
import java.text.DateFormat
import java.util.*


class AddStore : AppCompatActivity(), myInterfaces, TimePickerDialog.OnTimeSetListener {
    companion object {
        lateinit var recyclerViewSub: RecyclerView
        var globalCategorylist = ArrayList<CategoryParse>()
        var globalSubcategoryStringList = ArrayList<String>()
        var Store = false

    }

    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    lateinit var placesClient: PlacesClient
    var placeFields = Arrays.asList(Place.Field.ID,Place.Field.NAME,Place.Field.ADDRESS,Place.Field.LAT_LNG)

    var database = FirebaseFirestore.getInstance()

    var selectedEndorStart = 0
    val TAG = "AddStore"
    val PICK_IMAGE_REQUEST = 111
    var imageUri: Uri? = null
    var myGeolocation: GeoLocation = GeoLocation(2.2, 2.2)
    private var mStorage: FirebaseStorage? = null
    private var mStorageReference: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_store)
        getStores2()
        getCategories()
        recyclerViewSub = findViewById(R.id.Recycler_selectedSubcategory)
        iv_promo_image.setOnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }
        initPlaces()
        setupPlacesAutocomplete()




        btn_add_categoru.setOnClickListener {
            showDialog()


        }

        btnCreateStore.setOnClickListener {
            uploadFile()
        }


        tv_open_time.setOnClickListener {

            selectedEndorStart = 3
            var myTimepicker = TimePickerDialog(
                this,
                this,
                8,
                0,
                android.text.format.DateFormat.is24HourFormat(this)
            )
            myTimepicker.show()
        }
        tv_close_time.setOnClickListener {

            selectedEndorStart = 4
            var myTimepicker = TimePickerDialog(
                this,
                this,
                17,
                0,
                android.text.format.DateFormat.is24HourFormat(this)
            )
            myTimepicker.show()
        }

        btnGetLocation.setOnClickListener { getLocation() }
    }
    fun initPlaces(){
        Places.initialize(this,getString(R.string.places_api_key))
        placesClient  = Places.createClient(this)

    }
    fun setupPlacesAutocomplete(){
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(placeFields)

        autocompleteFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                val getPlaceName = p0.name.toString()

                et_store_address.setText(getPlaceName)

                myGeolocation = GeoLocation(p0.latLng!!.latitude,p0.latLng!!.longitude)
                //      Log.e(TAG,"${p0.address}  Lat - ${p0.latLng!!.latitude} Long - ${p0.latLng!!.longitude}")
            }

            override fun onError(p0: Status) {
                Log.e(TAG,"error")

            }
        })

    }
    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        var amorpm = "am"

        if (selectedEndorStart == 3) {

            if (p1 == 0)
                p1 - 1
            if (p1 > 12) {
                amorpm = "pm"
                p1 - 12

            }
            tv_open_time.text = "$p1:$p2 $amorpm"

        } else if (selectedEndorStart == 4) {
            if (p1 == 0)
                p1 - 1
            if (p1 > 12) {
                amorpm = "pm"
                p1 - 12

            }

            tv_close_time.text = "$p1:$p2 $amorpm"
        }

    }

    private fun showDialog() {
        Store = true
        AddPromo.globalCategorylist = globalCategorylist
        var myDialog = DialogFragmentAddCategoryBusiness()
        myDialog.show(fragmentManager, "myCustomDialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, dataa: Intent?) {
        super.onActivityResult(requestCode, resultCode, dataa)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && dataa != null) {
            imageUri = dataa.data
            Picasso.get().load(imageUri).into(iv_promo_image)
        }
    }


    override fun saveCategoriesBusiness(myCategoryList: ArrayList<CategoryParse>) {
        globalCategorylist = myCategoryList

        Log.e(TAG, "saveCategoriesBusiness")
        var subcategoryList = ArrayList<String>()
        for (i in 0 until myCategoryList.size) {
            Log.e(TAG, "loop1")

            for (j in 0 until myCategoryList[i].Subcategories.size) {
                Log.e(TAG, "loop2")
                if (myCategoryList[i].Subcategories[j].Selected) {
                    subcategoryList.add(myCategoryList[i].Subcategories[j].SubcategoryName)
                }
            }


        }
        globalSubcategoryStringList = subcategoryList
        for (k in 0 until subcategoryList.size) {

            Log.e(TAG, "selected - " + subcategoryList[k])
        }
        setAdapter(subcategoryList)
    }

    fun setAdapter(myList: ArrayList<String>) {
        var selectedSubAdapter = SelectedSubcategoryAdapterBusiness(this, myList)
        Log.e(TAG, "${myList.size}")
        var myStagger = StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL)
        recyclerViewSub.layoutManager = myStagger
        recyclerViewSub.adapter = selectedSubAdapter
    }

    private fun uploadFile() {

        mStorage = FirebaseStorage.getInstance()
        mStorageReference = mStorage!!.reference

        if (imageUri != null) {
            val ref = mStorageReference!!.child("images/" + UUID.randomUUID().toString())
          var uploadTask =   ref.putFile(imageUri!!)
                .addOnSuccessListener {



                }
                .addOnFailureListener {
                    toast("Uploading Failed")
                }
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl

            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Log.e("upload",task.result.toString())
                    Log.e("upload",  ref.downloadUrl.toString())

                    val image = task.result.toString()


                    toast("Image Uploaded Successfully")
                    storeDatatoFirestore(image)
                    finish()


                } else {
                    // Handle failures
                    // ...
                }
            }

        }


    }

    private fun storeDatatoFirestore(image: String) {
        var storeName = et_store_name.text.toString()
        var storeDescription = et_store_description.text.toString()
        var storeContact = et_contact_no.text.toString()
        var storeAddress = et_store_address.text.toString()
        var storeOpenTime = tv_open_time.text.toString()
        var storeCloseTime = tv_close_time.text.toString()
        var storeLink = et_store_site.text.toString()

        var myStore = StoreModel(
            image,
            storeName,
            storeContact,
            storeDescription,
            storeLink,
            GeoPoint(myGeolocation.latitude, myGeolocation.longitude),
            storeAddress,
            globalSubcategoryStringList,
            storeOpenTime,
            storeCloseTime,
            LoginActivityBusinessman.userBusinessManUsername
        )
        doAsync {

            database.collection("UserBusinessman").document(LoginActivityBusinessman.userBusinessManUsername)
                .collection("Stores").document(storeName).set(myStore).addOnSuccessListener {
                Log.e(TAG, "Store is fucking satored")

            }

            database.collection("Stores").document(storeName).set(myStore).addOnSuccessListener {
                Log.e(TAG, "Store is fucking satored2")


            }
        }


    }

    fun getCategories() {
        var database = FirebaseFirestore.getInstance()
        doAsync {
            database.collection("Categories").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (DocumentSnapshot in task.result!!) {
                        var uploaded = DocumentSnapshot.toObject(CategoryParse::class.java)
                        Log.e(TAG, DocumentSnapshot.getId() + " => " + DocumentSnapshot.getData())
                        //   Log.e(TAG,DocumentSnapshot.getId())
                        //  Log.e(TAG,upload.categoryName+upload.SelectedAll)
                        database.collection("Categories").document(DocumentSnapshot.id)
                            .collection("Subcategories").get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                for (DocumentSnapshot in task.result!!) {
                                    var upload =
                                        DocumentSnapshot.toObject(SubcategoryParse::class.java)
                                    Log.e(TAG, " Subcategory - $upload")
                                    uploaded.Subcategories.add(upload)


                                }


                            } else
                                toast("error")
                        }
                        globalCategorylist.add(uploaded)
                    }

                } else
                    toast("error")
            }


        }

    }

    fun getLocation() {
        var callTimes = false
        Log.e(TAG, "getLocation")
        locationManager =
            this!!.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {

                Log.e(TAG, "Location change ${location.latitude}  +  ${location.longitude}")
                if (!callTimes) {

                    val string = "${location.latitude},${location.longitude}"
                    et_store_address.setText(string)
                    myGeolocation = GeoLocation(location.latitude, location.longitude)

                    et_store_address.setText(getAdress(location.latitude, location.longitude))
                    callTimes = true
                }
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

            }

            override fun onProviderEnabled(provider: String) {

            }

            override fun onProviderDisabled(provider: String) {

            }
        }
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(
                    this!!.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this!!.applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )
            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0f,
                locationListener
            )

            // locationManager.!!requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0f,);

        } else {
            if (ActivityCompat.checkSelfPermission(
                    this!!.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this!!.applicationContext, Manifest.permission
                        .ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this!!,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
                return
            } else {
                //       locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
                // locationManager!!.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
                try {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        locationListener
                    )

                } catch (e: Exception) {
                    Log.e(TAG, "LOL")
                }
                Log.e(TAG, "this part")
            }

        }
    }

    fun getAdress(latitude: Double, longitude: Double): String {

        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        val address =
            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        val knownName = addresses[0].featureName
        val subLocality = addresses[0].subLocality



        Log.e(TAG, "$city \n  $state\n  $country\n  $postalCode\n  $knownName\n  $subLocality")
        return address
    }

    fun getStores2() {
        var storeList = ArrayList<StoreModel>()
        doAsync {
            database.collection("UserBusinessman").document(LoginActivityBusinessman.userBusinessManUsername)
                .collection("Stores").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (DocumentSnapshot in task.result!!) {
                        val storeName = DocumentSnapshot.data.get("storeName").toString()
                        val storeDescription =
                            DocumentSnapshot.data.get("storeDescription").toString()
                        val storeAddress = DocumentSnapshot.data.get("storeAddress").toString()
                        val storeBy = DocumentSnapshot.data.get("storeBy").toString()
                        val storeContact = DocumentSnapshot.data.get("storeContact").toString()
                        val storeLink = DocumentSnapshot.data.get("storeLink").toString()
                        val storeOpenTime = DocumentSnapshot.data.get("storeOpenTime").toString()
                        val storeCloseTime = DocumentSnapshot.data.get("storeCloseTime").toString()
                        val storeCategories =
                            DocumentSnapshot.data.get("storeCategories") as ArrayList<String>
                        var storeLatLng = DocumentSnapshot.data.get("storeLatLng") as GeoPoint
                        val storeImage = DocumentSnapshot.data.get("storeImage").toString()

                        val myStore = StoreModel(
                            storeImage,
                            storeName,
                            storeContact,
                            storeDescription,
                            storeLink,
                            storeLatLng,
                            storeAddress,
                            storeCategories,
                            storeOpenTime,
                            storeCloseTime,
                            storeBy
                        )

                        storeList.add(myStore)

                        Log.e(TAG, myStore.toString())
//                        Log.e(TAG,myStore.toString())
                    }


                }
            }

        }
    }


}
