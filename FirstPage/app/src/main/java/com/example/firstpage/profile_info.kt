package com.example.firstpage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.firstpage.AttractionFragment.HotAttractions
import com.example.firstpage.AttractionFragment.MyAttractions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_first_page.*
import kotlinx.android.synthetic.main.activity_first_page.btn_att
import kotlinx.android.synthetic.main.activity_first_page.btn_home
import kotlinx.android.synthetic.main.activity_first_page.btn_travel
import kotlinx.android.synthetic.main.activity_profile_info.*
import kotlinx.android.synthetic.main.activity_schedules.*
import kotlinx.android.synthetic.main.activity_profile_info.btn_profile as btn_profile1
import kotlinx.android.synthetic.main.activity_schedules.btn_profile as btn_profile1


class profile_info : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    var Auth= FirebaseAuth.getInstance()
    var Uname=""
    var Umail=""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_info)


        //navigation bar button
        btn_home.setOnClickListener {
            val intent = Intent(this, FirstPageActivity::class.java)
            startActivity(intent)
        }
        btn_travel.setOnClickListener {
            val intent = Intent(this, TravelActivity::class.java)
            startActivity(intent)
        }
        btn_att.setOnClickListener {
            val intent = Intent(this, HotAttractions::class.java)
            startActivity(intent)
        }
        btn_profile.setOnClickListener {
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }

        //get data from firebase
        var name=  db.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {

            txt_name_info.text=it.getString("UserName")
            txt_prof_name.setText(it.getString("UserName"))
            txt_prof_mail.setText(it.getString("Email"))


        }

        //set edittext cannot use
        txt_prof_mail.isEnabled=false
        txt_prof_name.isEnabled=false

        btn_edit.setOnClickListener {

            showHide(btn_edit_save)
            showHide(btn_edit)

            txt_prof_mail.isEnabled=true
            txt_prof_name.isEnabled=true

        }

        btn_edit_save.setOnClickListener {

            if (txt_prof_name.text.isNullOrEmpty() && txt_prof_mail.text.isNullOrEmpty()){

                Toast.makeText(this,"name & email is empty",Toast.LENGTH_SHORT).show()

            }else if (txt_prof_name.text.isNullOrEmpty() && !(txt_prof_mail.text.isNullOrEmpty())){

                Toast.makeText(this,"name is empty",Toast.LENGTH_SHORT).show()

            }else if(!(txt_prof_name.text.isNullOrEmpty()) && txt_prof_mail.text.isNullOrEmpty()){

                Toast.makeText(this,"email is empty",Toast.LENGTH_SHORT).show()

            }else{
                Uname=txt_prof_name.text.toString()
                InputName(Uname)
                Umail=txt_prof_mail.text.toString()
                InputEmail(Umail)
                showHide(btn_edit_save)
                showHide(btn_edit)

                Toast.makeText(this,"save",Toast.LENGTH_SHORT).show()

                txt_prof_mail.isEnabled=false
                txt_prof_name.isEnabled=false
                txt_name_info.text = Uname.toString()
            }

        }

        LoguotEvent()

    }

    //when pressed logout
    private fun LoguotEvent() {

        info_logout.setOnClickListener {
            Auth.signOut()
            if(Auth.currentUser==null){
                Toast.makeText(this, "Successfully logout:)", Toast.LENGTH_LONG).show()
                var intent= Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

    }

    //get var name upload firebase
    private fun InputName(UName:  String) {

        val db_user_info=  db.collection("UserInfo")
        var store_name=UName



        db_user_info.document(Auth.uid.toString()).get().addOnSuccessListener {
            val data =hashMapOf(
                "UserName" to store_name
            )
            db_user_info.document(Auth.uid.toString()).update(data as Map<String, Any>)

        }
    }

    //get var email upload firebase
    private fun InputEmail(Umail:  String) {

        val db_user_info=  db.collection("UserInfo")
        var store_email=Umail



        db_user_info.document(Auth.uid.toString()).get().addOnSuccessListener {
            val data =hashMapOf(
                "Email" to store_email
            )
            db_user_info.document(Auth.uid.toString()).update(data as Map<String, Any>)

        }
    }

    //function of show and hide button
    fun showHide(view:View) {
        view.visibility = if (view.visibility == View.VISIBLE){
            View.INVISIBLE
        } else{
            View.VISIBLE
        }
    }
}
