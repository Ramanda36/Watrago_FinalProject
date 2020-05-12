package com.example.firstpage

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.firstpage.AttractionFragment.HotAttractions
import com.example.firstpage.AttractionFragment.MyAttractions
import org.jetbrains.anko.activityManager

class ButtonNavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_button_navigation)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

//        textMessage = findViewById(R.id.message)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        creatHomePage()
    }

    val manager = supportFragmentManager


    private lateinit var textMessage: TextView
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
             //   textMessage.setText(R.string.title_home)
                creatHomePage()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_att -> {
             //   textMessage.setText(R.string.title_att)
                creatAttractionPage()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_travel -> {
              //  textMessage.setText(R.string.title_travel)
                creatTravelPage()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_personal -> {
             //   textMessage.setText(R.string.title_personal)
                creatProfilePage()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    fun creatHomePage()
    {


        val transaction = manager.beginTransaction()
        val fragment = HomePage()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun creatAttractionPage()
    {


//        val transaction = manager.beginTransaction()
//        val fragment = MyAttractions()
//        transaction.replace(R.id.fragment_container,fragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
    }



    fun creatTravelPage()
    {

        val transaction = manager.beginTransaction()
        val fragment = FragmentTravel()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun creatProfilePage()
    {

//        val transaction = manager.beginTransaction()
//        val fragment = profile()
//        transaction.replace(R.id.fragment_container,fragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
    }


}
