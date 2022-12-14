package com.tsl.app.widget

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jowney.common.util.logger.L
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.tsl.app.R
import com.tsl.app.fragment.preview.BasePreviewFragment
import com.tsl.app.fragment.preview.PPTPreviewFragment
import com.tsl.app.fragment.preview.PicturePreviewFragment
import com.tsl.app.fragment.preview.VideoPreviewFragment
import kotlinx.android.synthetic.main.popview_bottom_show.view.*


class BottomShowPopup(mContext: Context) : BottomPopupView(mContext) {
    private lateinit var mTabLayout: TabLayout
    private lateinit var mViewPager2: ViewPager2
    private val activeSize = 14
    private val normalSize = 14
    private val mTabs = arrayOf("视频", "PPT", "图片")
    private val mFragments = arrayOf<BasePreviewFragment>(
        VideoPreviewFragment.newInstance("3", "4"),
        PPTPreviewFragment.newInstance("1", "2"),
        PicturePreviewFragment.newInstance("5", "6"))
    private var mMediator: TabLayoutMediator? = null

    //true->焦点在TableView中，false->焦点在RecycleView中
    private var mFocusInTableView: Boolean = true
    override fun getImplLayoutId(): Int {
        return R.layout.popview_bottom_show
    }

    override fun onCreate() {
        super.onCreate()
        mTabLayout = widget_bsp_view_table_layout
        mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var trim = tab?.text.toString().trim()
                var spStr: SpannableString = SpannableString(trim)
                var styleSpan: StyleSpan = StyleSpan(Typeface.BOLD)
                spStr.setSpan(styleSpan, 0, trim.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                tab?.text = spStr
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                var trim = tab?.text.toString().trim()
                //spannableString其实和string一样，
                //textview可以直接通过设置spannablestring来显示文本，只是spannablestring可以显示更多的样式风格。
                //spannableString和SpannableStringBuilder   一个是一次性的，一个可多次修改。
                var spStr: SpannableString = SpannableString(trim)
                var styleSpan: StyleSpan = StyleSpan(Typeface.NORMAL)
                spStr.setSpan(styleSpan, 0, trim.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                tab?.text = spStr
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        mViewPager2 = widget_bsp_view_pager2
        mViewPager2.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        mViewPager2.adapter = object : FragmentStateAdapter(context as FragmentActivity) {
            override fun getItemCount(): Int {
                return mTabs.size;
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> mFragments[0]
                    1 -> mFragments[1]
                    2 -> mFragments[2]
                    else -> mFragments[3]
                }
            }
        }
        //viewPage左右滑动时tablayout也滑动
        mViewPager2.isUserInputEnabled = true

        mViewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                //可以来设置选中时tab的大小
                /*val tabCount: Int = mTabLayout.tabCount
                for (i in 0 until tabCount) {
                    val tab: TabLayout.Tab? = mTabLayout.getTabAt(i)
                    val tabView = tab?.view
                    if (tab?.position == position) {
                        tabView!!.textSize = activeSize.toFloat()
                        tabView!!.typeface = Typeface.DEFAULT_BOLD
                    } else {
                        tabView!!.textSize = normalSize.toFloat()
                        tabView!!.typeface = Typeface.DEFAULT
                    }
                }*/
            }
        })
        mMediator = TabLayoutMediator(
            mTabLayout, mViewPager2
        ) { tab, position ->
            //这里可以自定义TabView
            /*  val tabView = TextView(context)
              tabView.text = mTabs[position]*/
            tab.text = mTabs[position]
            tab.view.tab?.text
        }
        //要执行这一句才是真正将两者绑定起来
        mMediator!!.attach()
    }

    override fun onShow() {
        super.onShow()
        L.d("底部弹窗展示")
    }

    override fun onDismiss() {
        super.onDismiss()
        L.d("底部弹窗消失")
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context) * 0.3f).toInt()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                return mFragments[mViewPager2.currentItem].onDirectionalPadEnter()
            }
            KeyEvent.KEYCODE_BACK -> {
                L.d("back--->")
                return true //这里由于break会退出，所以我们自己要处理掉 不返回上一层
            }

            KeyEvent.KEYCODE_DPAD_DOWN ->
                /*    实际开发中有时候会触发两次，所以要判断一下按下时触发 ，松开按键时不触发
                 *    exp:KeyEvent.ACTION_UP
                 */
                if (event!!.action === KeyEvent.ACTION_DOWN) {
                    mFocusInTableView = false
                    L.d("tab down--->")
                }
            KeyEvent.KEYCODE_DPAD_UP -> {
                mFocusInTableView = true
                L.d("tab up--->")
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (mFocusInTableView)
                    mTabLayout.selectTab(mTabLayout.getTabAt(if ((mTabLayout.selectedTabPosition - 1) <= 0) 0 else (mTabLayout.selectedTabPosition - 1)))
                else
                    mFragments[mViewPager2.currentItem].onDirectionalPadLeft()
                L.d("tab left--->")
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (mFocusInTableView) {
                    mTabLayout.selectTab(mTabLayout.getTabAt(if ((mTabLayout.selectedTabPosition + 1) >= 2) 2 else (mTabLayout.selectedTabPosition + 1)))

                } else mFragments[mViewPager2.currentItem].onDirectionalPadRight()
                L.d("tab right--->")
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }


}