package com.example.firstpage

//import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firstpage.AttractionFragment.MyAttractions
import com.example.firstpage.TransportFragment.Schedules
import com.example.firstpage.TransportFragment.Transport_Attraction
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.logintest.*

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logintest)
        login_button_login.setOnClickListener {
            performLogin()
        }
        back_to_register_textview.setOnClickListener{
            val intent = Intent(this, Register::class.java)
            startActivity(intent)

        }

    }

    private fun performLogin() {
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out email/pw.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
//                Log.d("Login", "Successfully logged in!")
                Toast.makeText(this, "Successfully login:)", Toast.LENGTH_LONG).show()

                val intent = Intent(this, FirstPageActivity::class.java)
//                val intent = Intent(this, MyAttractions::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)//1080806
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}