package com.example.firstpage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firstpage.AttractionFragment.MyAttractions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_first_page.*
import kotlinx.android.synthetic.main.activity_first_page.btn_att
import kotlinx.android.synthetic.main.activity_first_page.btn_home
import kotlinx.android.synthetic.main.activity_first_page.btn_travel
import kotlinx.android.synthetic.main.activity_profile_perference.*
import kotlinx.android.synthetic.main.activity_schedules.*
import android.R
import android.view.View
import android.widget.*
import androidx.viewpager.widget.ViewPager
import com.example.firstpage.AttractionFragment.HotAttractions
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_register.*
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import kotlinx.android.synthetic.main.activity_first_page.btn_profile as btn_profile1
import kotlinx.android.synthetic.main.activity_profile_perference.btn_profile as btn_profile1
import kotlinx.android.synthetic.main.activity_schedules.btn_profile as btn_profile1


class profile_perference : AppCompatActivity() {


    val db = FirebaseFirestore.getInstance()
    var Auth= FirebaseAuth.getInstance()
    var month_pref=true
        var ig_spot_pref =0


    @SuppressLint("WrongConstant")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.firstpage.R.layout.activity_profile_perference)

//        val seekBar = findViewById<SeekBar>(com.example.firstpage.R.id.seekBar)

        val db_user_info=  db.collection("UserInfo")
//        var temp_spot_pref=0
//        var ig_spot_pref =0

        var month_pref=true
        var month_char=""
        var trans_count=0
        var load_trans_time: Double
        var db_trans_time: Double


        txt_google_present.text="google maps 的比重"
        txt_ig_present.text="instagram 的比重"


        //fake navigation bar button

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

        //get database data

        db.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {

            ig_spot_pref = it.getDouble("SpotPrefer")?.toInt()!!
            seekBar.progress = ig_spot_pref

            if(it.getDouble("transitcount")==null){
                seekBar_count.progress = 2
            }else{
                trans_count = it.getDouble("transitcount")?.toInt()!!
                seekBar_count.progress = trans_count
            }

            db_trans_time = it.getDouble("transporttime")!!
            load_trans_time=db_trans_time*2
            seekBar_time.progress = load_trans_time.toInt()

            month_pref = it.getBoolean("DurationPrefer")!!

            if (month_pref) {
                month_char = "一個月"
                chk_minth.setChecked(true)
            } else {
                month_char = "一年"
                chk_year.setChecked(true)
            }


        }

        //information click listener

        treans_time_info.setOnClickListener {
            Toast.makeText(this,"當搭乘交通運輸工具時間大於乘車時間一定倍數時，出現貼心提醒\"開車可以省多少時間\"。",Toast.LENGTH_SHORT).show()
        }

        duration_info.setOnClickListener {
            Toast.makeText(this,"IG趨勢景點的時間範圍是否以1個月為基準。預設為全部景點最早的日期為基準。",Toast.LENGTH_SHORT).show()
        }

        persent_info.setOnClickListener {
            Toast.makeText(this,"在熱門景點中，你比較偏好instagram的景點，還是google maps的景點。",Toast.LENGTH_SHORT).show()
        }

        treans_count_info.setOnClickListener {
            Toast.makeText(this,"搭乘公正運輸工具時，能接受的轉乘次數。",Toast.LENGTH_SHORT).show()
        }

        var name=  db.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {

            txt_name_perf.text=it.getString("UserName")


        }

        //ig and google maps Seekbar

        seekBar.setOnProgressChangeListener(object:DiscreteSeekBar.OnProgressChangeListener{
            override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
                if (seekBar != null) {
                    ig_spot_pref=seekBar.progress
                    txt_google_present.text="google maps 的比重 : "+ (10-ig_spot_pref).toString()
                    txt_ig_present.text="instagram 的比重 : "+ ig_spot_pref.toString()
                }
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {
                if (seekBar != null) {
                    ig_spot_pref=seekBar.progress
                    txt_google_present.text="google maps 的比重 : "+(10-ig_spot_pref).toString()
                    txt_ig_present.text="instagram 的比重 : "+ ig_spot_pref.toString()
                }
            }

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
                if (seekBar != null) {
                    ig_spot_pref=seekBar.progress
                    txt_google_present.text="google maps 的比重 : "+ (10-ig_spot_pref).toString()
                    txt_ig_present.text="instagram 的比重 : "+ ig_spot_pref.toString()

                    InputPrefer(ig_spot_pref)
                    db.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {

                        ig_spot_pref= it.getDouble("SpotPrefer")?.toInt()!!

                    }
                    seekBar.progress=ig_spot_pref

                }
            }
        })

        //transition count Seekbar

        seekBar_count.setOnProgressChangeListener(object:DiscreteSeekBar.OnProgressChangeListener{
            override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
                if (seekBar != null) {
                    trans_count = seekBar_count.progress
                }
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {
                if (seekBar != null) {
                    trans_count = seekBar_count.progress
                }
            }

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
//                if (seekBar != null) {
//                    trans_count = seekBar_count.progress
//
//                    InputCount(trans_count)
//                    db.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {
//
//                        trans_count= it.getDouble("transitcount")?.toInt()!!
//
//                    }
//                    seekBar_count.progress=trans_count
//
//                }
            }
        })

        //transition time Seekbar
//        seekBar_time.max = 8

        seekBar_time.setOnProgressChangeListener(object:DiscreteSeekBar.OnProgressChangeListener{
            override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
                if (seekBar != null) {
                    load_trans_time = seekBar_time.progress.toDouble()
                    txt_time.setText("大眾運輸為搭車 "+getCountValue(value)+" 倍時顯示提醒")

                }
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {
                if (seekBar != null) {

                }
            }

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
                if (seekBar != null) {
                    load_trans_time = seekBar_time.progress.toDouble()
                    db_trans_time = (load_trans_time/2)
//                    InputTime(load_trans_time.toDouble())
                    InputTime(db_trans_time)
                    db.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {
//                        load_trans_time= it.getDouble("transporttime")?.toInt()!!
                        db_trans_time= it.getDouble("transporttime")!!

                    }
                    seekBar_time.progress = load_trans_time.toInt()

                }
            }
        })

        radio_group.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener(){ group, checkedId ->
            val radio: RadioButton = findViewById(checkedId)

            if(radio.text=="一個月"){
                month_pref=true
                db_user_info.document(Auth.uid.toString()).get().addOnSuccessListener {
                    val data =hashMapOf(
                        "DurationPrefer" to month_pref
                    )
                    db_user_info.document(Auth.uid.toString()).update(data as Map<String, Any>)
                }

            }else{
                month_pref=false
                db_user_info.document(Auth.uid.toString()).get().addOnSuccessListener {
                    val data =hashMapOf(
                        "DurationPrefer" to month_pref
                    )
                    db_user_info.document(Auth.uid.toString()).update(data as Map<String, Any>)
                }


            }

        })

        LoguotEvent()

    }
    private fun InputPrefer(ig_spot_pref:  Int) {

        val db_user_info=  db.collection("UserInfo")
        var store_spot_pref=ig_spot_pref



        db_user_info.document(Auth.uid.toString()).get().addOnSuccessListener {
            val data =hashMapOf(
                "SpotPrefer" to store_spot_pref
            )
            db_user_info.document(Auth.uid.toString()).update(data as Map<String, Any>)



        }
    }

    private fun InputCount(trans_count:  Int) {

        val db_user_info=  db.collection("UserInfo")
        var store_trans_count=trans_count

        db_user_info.document(Auth.uid.toString()).get().addOnSuccessListener {
            val data =hashMapOf(
                "transitcount" to store_trans_count
            )
            db_user_info.document(Auth.uid.toString()).update(data as Map<String, Any>)
        }
    }

    private fun InputTime(db_trans_time:  Double) {

        val db_user_info=  db.collection("UserInfo")
        var store_trans_time:Double = db_trans_time

        db_user_info.document(Auth.uid.toString()).get().addOnSuccessListener {
            val data =hashMapOf(
                "transporttime" to store_trans_time
            )
            db_user_info.document(Auth.uid.toString()).update(data as Map<String, Any>)
        }
    }

    private fun LoguotEvent() {

        pref_logout.setOnClickListener {
            Auth.signOut()
            if(Auth.currentUser==null){
                Toast.makeText(this, "Successfully logout:)", Toast.LENGTH_LONG).show()
                var intent= Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun getCountValue(intVal: Int): Float {
        var floatVal = 0.0f
        floatVal = .5f * intVal
        return floatVal
    }
}

