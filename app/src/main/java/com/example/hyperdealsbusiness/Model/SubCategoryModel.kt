package com.example.hyperdealsbusiness.Model

import java.util.*

class SubCategoryModel(var subCategoryName: String, var subsubCategoryList: ArrayList<SubsubCategoryModel>)

{
    class SubsubCategoryModel(var SubsubcategoryName:String,var isChecked:Boolean=false)
}