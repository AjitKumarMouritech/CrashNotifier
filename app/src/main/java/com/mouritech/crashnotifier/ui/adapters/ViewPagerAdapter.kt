package com.mouritech.crashnotifier.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.mouritech.crashnotifier.R

class ViewPagerAdapter (private val mContext: Context, private  val itemList: ArrayList<String>) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(mContext)
        val view = layoutInflater!!.inflate(R.layout.view_pager_data, container, false)
        var textview: TextView = view.findViewById(R.id.slide_screen_tv)
        var heading: TextView = view.findViewById(R.id.heading)
        var description: TextView = view.findViewById(R.id.description)

        textview.text = itemList[position]

        if (position==0){
            heading.text = mContext.getString(R.string.location)
            description.text = mContext.getString(R.string.location_description)
        }
        else{
            heading.text = mContext.getString(R.string.sms)
            description.text = mContext.getString(R.string.sms_description)
        }

        container.addView(view, position)
        return view
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }
}