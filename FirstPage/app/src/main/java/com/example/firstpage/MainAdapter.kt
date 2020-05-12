//package com.example.firstpage
//
//
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.android.synthetic.main.list_item.view.*
//
//class MainAdapter : RecyclerView.Adapter<CustomViewHolder>() {
//
//    val db = FirebaseFirestore.getInstance()
//    val TAG = "testAttractions"
//    var Auth = FirebaseAuth.getInstance()
//    var spotlist: MutableList<String> = mutableListOf()
//    var spotnamelist: MutableList<String> = mutableListOf()
//    var attractionnamelist: MutableList<String> = mutableListOf()
//
//    //val spotnamelist = listOf("First title", "Second", "3rd", "MOOOOORE TITLE")
//
//
//    override fun getItemCount(): Int {
//
//        val info = db.collection("Attraction").get().addOnSuccessListener{
//            documents->
//
//            for (document in documents ) {
//                Log.d(TAG, document.data.get("PhotoUrl").toString())
//                Log.d(TAG, document.data.get("AttractionName").toString())
//                spotlist.add(document.data.get("PhotoUrl").toString())
//                spotnamelist.add(document.data.get("AttractionName").toString())
//            }
//             attractionnamelist = spotnamelist
//            Log.d(TAG, attractionnamelist[1])
//
//        }
//
//
//    return attractionnamelist.size
//
//}
//
//    //var intent= Intent.getIntentOld("list1")
////        if (getIntent().getExtra()!=null){
////            attractionnamelist= getIntent().getStringExtra("list1")
////
////        }
//
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
//        // how do we even create a view
//        val layoutInflater = LayoutInflater.from(parent?.context)
//        val cellForRow = layoutInflater.inflate(R.layout.list_item, parent, false)
//        return CustomViewHolder(cellForRow)
//    }
//
//    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        val spotnameList = attractionnamelist.get(position)
//        holder?.view?.spotname?.text=spotnameList
//
//    }
//
//}
//
//class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {
//
//}
