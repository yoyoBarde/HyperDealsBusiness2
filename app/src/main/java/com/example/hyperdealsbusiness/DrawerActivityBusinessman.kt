package com.example.hyperdealsbusiness

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.app_bar_drawer_activity_businessman.*


import android.view.MenuInflater

import androidx.annotation.NonNull

import androidx.drawerlayout.widget.DrawerLayout




class DrawerActivityBusinessman : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
   private var mSectionsPagerAdapter: SectionsPagerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.businessman_activity_drawer)
        setSupportActionBar(toolbar1)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        containerbm.adapter = mSectionsPagerAdapter


        containerbm.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs1))
        tabs1.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(containerbm))



        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar1, R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(com.example.hyperdealsbusiness.R.menu.drawer_activity_businessman, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

class SectionsPagerAdapter(manager:FragmentManager):FragmentPagerAdapter(manager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
                    when (position){
                0 -> {
                    return FragmentAddPromo()
                }

                1 -> {
                    return FragmentReach()

                }

                else -> return FragmentAddPromo()
            }
    }

    override fun getCount(): Int {
        return 2
    }
}

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {


            }
            R.id.nav_stores -> {
                val intent = Intent (this, Business_Stores::class.java)
                startActivity(intent)

            }

            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_logout -> {
                val intent = Intent (this, MainActivity::class.java)
                startActivity(intent)

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}


