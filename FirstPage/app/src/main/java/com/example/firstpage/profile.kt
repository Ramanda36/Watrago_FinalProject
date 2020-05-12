package com.example.firstpage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.firstpage.AttractionFragment.HotAttractions
import com.example.firstpage.AttractionFragment.MyAttractions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_first_page.*
import kotlinx.android.synthetic.main.activity_first_page.btn_att
import kotlinx.android.synthetic.main.activity_first_page.btn_home
import kotlinx.android.synthetic.main.activity_first_page.btn_travel
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile_perference.*
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import java.util.*

class profile : AppCompatActivity() {


    val db = FirebaseFirestore.getInstance()
    var Auth= FirebaseAuth.getInstance()
    var photo_url=""


    override fun onCreate(savedInstanceState: Bundle?) {
        verifyUserIsLogin()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        db.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {
            var userurl=it.getString("profileImageUrl")
            if(userurl!=""){
                Picasso.get().load(userurl).into(user_img)
            }else{
                user_img.setImageResource(R.drawable.gif_profile_photo)

            }
        }

        user_img.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)

        }

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

        txt_preference.setOnClickListener {
            val intent = Intent(this, profile_perference::class.java)
            startActivity(intent)
        }
        txt_info.setOnClickListener {
            val intent = Intent(this, profile_info::class.java)
            startActivity(intent)
        }

//        var name=
        db.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {

            txt_user.text=it.getString("UserName")
            photo_url= it.getString("profileImageUrl").toString()

        }

//        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photo_url)



        LoguotEvent()
    }
    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
//            Log.d(Register.TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            user_img.setImageBitmap(bitmap)

            user_img.alpha = 0f
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

//      val bitmapDrawable = BitmapDrawable(bitmap)
//      selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
//                    Log.d(Register.TAG, "Successfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
//                        Log.d(Register.TAG, "File Location: $it")
                        AddDataToFirebase(it.toString())

                        // saveUserToFirebaseDatabase(it.toString())

                    }
                }

        }

    }

    private fun AddDataToFirebase(profileImageUrl: String) {

        var tempdb=db.collection("UserInfo")
        tempdb.document(Auth.uid.toString()).get().addOnSuccessListener {
            val data =hashMapOf(
                "profileImageUrl" to profileImageUrl
            )
            tempdb.document(Auth.uid.toString()).update(data as Map<String, Any>)
            this.recreate()


        }
    }

    private fun LoguotEvent() {

        profile_logout.setOnClickListener {
            Auth.signOut()
            if(Auth.currentUser==null){
                Toast.makeText(this, "Successfully logout:)", Toast.LENGTH_LONG).show()
                var intent= Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun verifyUserIsLogin() {
        val uid=Auth.uid
        if(uid==null){
            val intent=Intent(this,LoginActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }


}
class User1(val profileImageUrl: String){
    constructor():this("")
}
