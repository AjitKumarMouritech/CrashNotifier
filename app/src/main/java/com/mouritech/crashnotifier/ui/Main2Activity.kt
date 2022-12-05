package com.mouritech.crashnotifier.ui

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.databinding.ActivityMain2Binding
import com.mouritech.crashnotifier.utils.Utils

class Main2Activity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain2.toolbar)
        /*val navigationView = findViewById<View>(com.mouritech.crashnotifier.R.id.nav_view) as NavigationView

        val headerView: View = navigationView.getHeaderView(0)
        headerView.findViewById(com.mouritech.crashnotifier.R.id.userName).text = "User"
        headerView.findViewById(com.mouritech.crashnotifier.R.id.mobileNumber).text = "user@gmail.com"*/


        val navigationView = findViewById<View>(com.mouritech.crashnotifier.R.id.nav_view) as NavigationView
        var headerView= navigationView.getHeaderView(0)
        var username = headerView.findViewById<TextView>(R.id.userName)
        var mobileNumber = headerView.findViewById<TextView>(R.id.mobileNumber)

        val preferences = getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)

        username.text = Utils.getUserName(preferences)
        mobileNumber.text = Utils.mobileNumber(preferences)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_menu_nearest_emergency_contacts,
                R.id.nav_menu_hospitals_list,
                R.id.nav_menu_update_emergency_contacts,
                R.id.nav_update_health_details
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.main2, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}