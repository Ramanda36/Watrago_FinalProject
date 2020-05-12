package com.example.firstpage.text_run

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView

class MarqueeTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null):TextView(context, attrs) {
    init{
        setSingleLine()
        ellipsize = TextUtils.TruncateAt.MARQUEE
        isFocusable = true
        marqueeRepeatLimit = -1
        isFocusableInTouchMode = true
        setHorizontallyScrolling(true)
    }

    //強制讓所有情況都有跑馬燈效果
    override fun isFocused(): Boolean {
        return true
    }
    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        if(focused) super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        if(hasWindowFocus) super.onWindowFocusChanged(hasWindowFocus)
    }
}