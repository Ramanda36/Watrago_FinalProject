package com.example.firstpage

//import com.google.firebase.firestore.FirebaseFirestore
import android.content.ContentValues
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

//0808
import kotlinx.android.synthetic.main.activity_test.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.HttpURLConnection
import java.net.URL
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity


class test : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    val TAG = "MainActivity"

    //widgets


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        //val dialog=findViewById<Button>(R.id.showdialog)
        showdialog.setOnClickListener {
            val demoDialogFragment = DemoDialogFragment()
            demoDialogFragment.show(supportFragmentManager, "")
            d(TAG,"HI")

        }

        val results = FloatArray(1)
        Location.distanceBetween(121.387443,25.034770,
           121.767541, 25.160539,results)
        results[0]= (results[0]*0.001).toFloat()
        d("MainActivity", results[0].toString())


//        doAsync{
//            sendGet("http://120.126.18.144:4000/echo?msg=aaa")
//            uiThread{
//
//            }
//        }
//
//        back_btn.setOnClickListener {
//
//           //GetDataFromFirebase()
//           //finish()
//        }
    }

    fun sendGet( url :String) {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            val data = connection.inputStream.bufferedReader().readText()
            d(ContentValues.TAG,data)
        }finally {
            connection.disconnect()
        }
    }

    private fun AddDataToFirebase() {

        val newContact: HashMap<String, Any> = HashMap()
        newContact.put("Poster", "mook_travel_plus")
        newContact.put(
            "PhotoUrl",
            "https://scontent-tpe1-1.cdninstagram.com/vp/af191830e42872d116bb034166e8e31c/5DA5A28A/t51.2885-15/e35/s1080x1080/65159864_2251333511844984_483847965992584461_n.jpg?_nc_ht=scontent-tpe1-1.cdninstagram.com"
        )

        db.collection("Attraction").document("test2")
            .set(newContact)
            .addOnSuccessListener { void: Void? ->
                Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()
            }.addOnFailureListener { exception: java.lang.Exception ->
                Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
            }
    }

    private fun GetDataFromFirebase() {
            db.collection("Attraction")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            d(TAG, document.id + " => " + document.data)
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }
        /* val docRef = db.collection("Attraction").document("test")
         docRef.get()
             .addOnSuccessListener { document ->
                 if (document != null) {
                     Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                 } else {
                     Log.d(TAG, "No such document")
                 }
             }
             .addOnFailureListener { exception ->
                 Log.d(TAG, "get failed with ", exception)
             }*/
    }


    }
 class DemoDialogFragment  : androidx.fragment.app.DialogFragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        val dialog = super.onCreateDialog(savedInstanceState)
        val window = dialog.window
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        //setStyle(DialogFragment.STYLE_NORMAL, android.R.style.TextAppearance_Theme_Dialog)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_test, container)
        return v
    }
}

