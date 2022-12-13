package com.mouritech.crashnotifier.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.mouritech.crashnotifier.R
import com.mouritech.crashnotifier.databinding.ActivitySplashScreenBinding
import com.mouritech.crashnotifier.databinding.ViewPagerBinding
import com.mouritech.crashnotifier.ui.adapters.ViewPagerAdapter

class ViewPagerActivity : AppCompatActivity() {
    lateinit var binding : ViewPagerBinding
    private lateinit var mViewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.view_pager)


        var textList = ArrayList<String>()
        textList.add(getString(R.string.swipe_next))
        textList.add(getString(R.string.plz_proceed))
        mViewPagerAdapter = ViewPagerAdapter(this, textList)
        binding.proceedBtn.isVisible = false


        binding.viewpager.pageMargin = 15
        binding.viewpager.setPadding(50, 0, 50, 0);
        binding.viewpager.clipToPadding = false
        binding.viewpager.pageMargin = 25
        binding.viewpager.adapter = mViewPagerAdapter
        binding.viewpager.addOnPageChangeListener(viewPagerPageChangeListener)

        binding.proceedBtn.setOnClickListener {
            carouselDisplayed()
            val i = Intent(this@ViewPagerActivity, Main2Activity::class.java)
            startActivity(i)
            finish()
        }

    }

    private fun carouselDisplayed() {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putInt("carousel_screens", 2)
        myEdit.commit()
    }

    var viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
                // your logic here
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                binding.proceedBtn.isVisible = position==1
            }

            override fun onPageSelected(position: Int) {
                binding.proceedBtn.isVisible = position==1
            }
        }
}