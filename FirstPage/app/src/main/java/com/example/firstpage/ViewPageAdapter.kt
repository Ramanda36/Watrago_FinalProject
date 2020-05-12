package com.example.firstpage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import java.security.AccessControlContext

class ViewPageAdapter (private val context: Context) : PagerAdapter(){
    private var layoutInflater:LayoutInflater?=null
    private  val images = arrayOf(R.drawable.ty_spot_1, R.drawable.ty_spot_2,R.drawable.ty_spot_3,R.drawable.ty_spot_4,R.drawable.ty_spot_5)

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = layoutInflater!!.inflate(R.layout.custom_layout,null)
        val image = v.findViewById<View>(R.id.image_view) as ImageView
        image.setImageResource(images[position])

        val vp = container as ViewPager
        vp.addView(v, 0)
        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val v= `object` as View
        vp.removeView(v)
    }
}