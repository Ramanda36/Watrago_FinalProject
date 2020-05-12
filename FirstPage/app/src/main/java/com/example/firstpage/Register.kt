package com.example.firstpage

//import android.support.v7.app.AppCompatActivity
//import com.google.firebase.firestore.FirebaseFirestore
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import kotlin.collections.ArrayList

class Register : AppCompatActivity() {
    companion object {
        val TAG = "RegisterActivity"
    }

    val db = FirebaseFirestore.getInstance()
    var Auth= FirebaseAuth.getInstance()
    val db_user_info=  db.collection("UserInfo")
    var currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser()

    // private var mAuth: FirebaseAuth? = null
    /*  val TAG = "MainActivity"
    private val name: EditText? = null
    private var email_id: EditText? = null
    private var passwordcheck: EditText? = null
    private var username: EditText? = null
    private var progressBar: ProgressBar? = null
    private var birth: EditText? = null
    //private var date:DatePicker ? = null*/
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        btn_signup.setOnClickListener {
            performRegister()
        }

        login_page.setOnClickListener {
//            Log.d(TAG, "Try to show login activity")

            // launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        selectphoto_button_register.setOnClickListener {
//            Log.d(TAG, "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
//            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

//      val bitmapDrawable = BitmapDrawable(bitmap)
//      selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister() {
        val email = input_email.text.toString()
        val password = input_password.text.toString()


        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
            return
        }

//        Log.d(TAG, "Attempting to create user with email: $email")

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // else if successful
//                Log.d(TAG, "Successfully created user")

                uploadImageToFirebaseStorage()
               // Toast.makeText(this, "You are the member now!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
//                Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage() {
//        if (selectedPhotoUri == null) return

       val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
            if(selectedPhotoUri==null){
                var temp=""
//                    AddDataToFirebase(temp)
                val newContact: HashMap<String, Any> = HashMap()
                var store_spot_pref=5
                var month_pref=true
                var nullcollection=ArrayList<String>()
                var transporttime = 0.5
                var transitcount = 2
                newContact.put("UserName",input_username!!.text.toString())
                newContact.put("Password",input_password!!.text.toString())
                newContact.put("Email",input_email!!.text.toString())
                newContact.put("profileImageUrl",temp)
                newContact.put("SpotPrefer",store_spot_pref)
                newContact.put("transitcount",transitcount)
                newContact.put("transporttime",transporttime)
                newContact.put("DurationPrefer",month_pref)
                newContact.put("Collection",nullcollection)




                db.collection("UserInfo").document(Auth.uid.toString())
                    .set(newContact)

                Toast.makeText(this, "Successfully created account :)", Toast.LENGTH_LONG).show()
                var intent=Intent(this,LoginActivity::class.java)

                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)//1080806

                startActivity(intent)
            }
        else{
                ref.putFile(selectedPhotoUri!!)
                    .addOnSuccessListener {
//                        Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                        ref.downloadUrl.addOnSuccessListener {
//                            Log.d(TAG, "File Location: $it")

                            // saveUserToFirebaseDatabase(it.toString())
                            AddDataToFirebase(it.toString())
                        }
            }

                }
//                .addOnFailureListener {
//                    Log.d(TAG, "Failed to upload image to storage: ${it.message}")
//                    var temp=""
////                    AddDataToFirebase(temp)
//                    val newContact: HashMap<String, Any> = HashMap()
//                    newContact.put("UserName",input_username!!.text.toString())
//                    newContact.put("Password",input_password!!.text.toString())
//                    newContact.put("Email",input_email!!.text.toString())
//                    newContact.put("profileImageUrl",temp)
//                    Toast.makeText(this, "Successfully created account :)", Toast.LENGTH_LONG).show()
//                    var intent=Intent(this,LoginActivity::class.java)
//
//                    intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)//1080806
//
//                    startActivity(intent)
//
//                }
                }


    private fun AddDataToFirebase(profileImageUrl: String) {
        val newContact: HashMap<String, Any> = HashMap()
       // val uid = FirebaseAuth.getInstance().uid ?: ""
        //val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
          newContact.put("UserName",input_username!!.text.toString())
          newContact.put("Password",input_password!!.text.toString())
          newContact.put("Email",input_email!!.text.toString())
        newContact.put("profileImageUrl",profileImageUrl)

        db.collection("UserInfo").document(Auth.uid.toString())
         .set(newContact)
        .addOnSuccessListener { void: Void? ->
            // store prefer in firebase by hsuen

            var store_spot_pref=5
            var month_pref=true
            var nullcollection=ArrayList<String>()
            var transporttime = 0.5
            var transitcount = 2
            
            db_user_info.document(Auth.uid.toString()).get().addOnSuccessListener {
                val data =hashMapOf(
                    "SpotPrefer" to store_spot_pref,
                    "DurationPrefer" to month_pref,
                    "Collection" to nullcollection,
                    "transporttime" to transporttime,
                    "transitcount" to transitcount

                )
                db_user_info.document(Auth.uid.toString()).update(data as Map<String, Any>)
            }
            Toast.makeText(this, "Successfully created account :)", Toast.LENGTH_LONG).show()
            var intent=Intent(this,LoginActivity::class.java)

            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)//1080806

            startActivity(intent)
        }.addOnFailureListener { exception: java.lang.Exception ->
            Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
        }


}}
    class User(val uid: String, val username: String, val profileImageUrl: String){
        constructor():this("","","")
    }
