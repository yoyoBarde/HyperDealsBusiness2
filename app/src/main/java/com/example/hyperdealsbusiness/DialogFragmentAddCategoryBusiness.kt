package com.example.hyperdealsbusiness


import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.example.hyperdealsbusiness.Adapter.CategoryAdapterBusiness
import com.example.hyperdealsbusiness.AddPromo.Companion.globalCustomSubcategory
import com.example.hyperdealsbusiness.Model.myInterfaces
import kotlinx.android.synthetic.main.activity_dialog_fragment_add_category_business.*


class DialogFragmentAddCategoryBusiness : DialogFragment() {

    var myAdapter: CategoryAdapterBusiness?=null
    val TAG = "DialogFragment"
    fun newInstance(): DialogFragmentAddCategoryBusiness {
        return DialogFragmentAddCategoryBusiness()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {




        return inflater.inflate(R.layout.activity_dialog_fragment_add_category_business, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var myGlobalcategoryList = AddPromo.globalCategorylist
        try{

            for(i in 0 until myGlobalcategoryList.size){
                Log.e(TAG,"${myGlobalcategoryList[i].Subcategories.size} - ${myGlobalcategoryList[i].categoryName}")
                for(k in 0 until myGlobalcategoryList[i].Subcategories.size  ){
                    for(l in 0 until globalCustomSubcategory.size){
                        if(globalCustomSubcategory[l].matches(myGlobalcategoryList[i].Subcategories[k].SubcategoryName.toRegex())){
                            myGlobalcategoryList[i].Subcategories[k].Selected = true

                        }

                    }


                }

            }

        }catch (e:UninitializedPropertyAccessException){
            print(e)

        }




        myAdapter = CategoryAdapterBusiness(activity!!,myGlobalcategoryList)

        recyclerViewCategories.layoutManager = LinearLayoutManager(activity!!)
        recyclerViewCategories.adapter = myAdapter
        iv_close.setOnClickListener {

            dismiss()

        }
        btn_set_category_business.setOnClickListener {


            if(AddStore.Store){
                var myInterface =  AddStore() as (myInterfaces)
                myInterface.saveCategoriesBusiness(CategoryAdapterBusiness.globalCategoryList)

            }
            else {
                var myInterface =  AddPromo() as (myInterfaces)
                myInterface.saveCategoriesBusiness(CategoryAdapterBusiness.globalCategoryList)
            }
            dismiss()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Sample)
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            //   dialog.window!!.setGravity(Gravity.BOTTOM)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.white )
        }
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog.window!!
            .attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setBackgroundDrawableResource(R.color.black_alpha_80)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        //  dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        return dialog
    }





}



