package com.example.hyperdealsbusiness.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.hyperdealsbusiness.Model.CategoryParse
import com.example.hyperdealsbusiness.R
import kotlinx.android.synthetic.main.model_head_category.view.*
import java.util.*

class CategoryAdapterBusiness (var context: Context, var categoryList : ArrayList<CategoryParse>) : RecyclerView.Adapter<CategoryAdapterBusiness.ViewHolder>(){
    var myList = categoryList
    var mySubcatAdapter:SubcategoryAdapterBusiness?=null
    companion object {

        lateinit  var globalCategoryList:ArrayList<CategoryParse>
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.model_head_category,parent,false))




    override fun getItemCount(): Int = categoryList.size


    override fun onBindViewHolder(holder: CategoryAdapterBusiness.ViewHolder, position: Int) {
        globalCategoryList = categoryList
        var alternateCounte = 1

        val myCategory = categoryList[position]

        mySubcatAdapter = SubcategoryAdapterBusiness(context,myCategory.Subcategories,position)


        var myStagger = StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL)
        holder.recyclerViewHeadd.layoutManager = myStagger
        holder.recyclerViewHeadd.adapter =  mySubcatAdapter


        holder.head_category.text = myCategory.categoryName


        holder.recyclerViewHeadd
        var subcategorySelected = false

        for (i in 0 until myCategory.Subcategories.size){
            if( myCategory.Subcategories[i].Selected){
                subcategorySelected = true

            }

        }
        if(subcategorySelected){
            holder.recyclerViewHeadd.visibility = View.VISIBLE

            holder.iv_minimize_maximize.setImageResource(R.mipmap.ic_arrow_down)
        }
        holder.iv_minimize_maximize.setOnClickListener {
            alternateCounte += 1

            if(alternateCounte%2==0) {
                holder.recyclerViewHeadd.visibility = View.VISIBLE

                holder.iv_minimize_maximize.setImageResource(R.mipmap.ic_arrow_down)
            }
            else
            {
                holder.recyclerViewHeadd.visibility = View.GONE

                holder.iv_minimize_maximize.setImageResource(R.mipmap.ic_arrow_right)
            }

        }


    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val head_category = view.tv_head_category!!
        val iv_minimize_maximize = view.minimize_maximize!!
        val recyclerViewHeadd = view.recyclearViewHead!!
    }


}