package com.tsl.app.activity.show

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsl.app.widget.BottomShowPopup

class ShowActivityViewModel : ViewModel() {
    private var mBottomShowPopup: MutableLiveData<BottomShowPopup> = MutableLiveData<BottomShowPopup>()
    fun setBottomPopup(bottomShowPopup: BottomShowPopup) {
        mBottomShowPopup.value = bottomShowPopup
    }

    fun getBottomPopup(): BottomShowPopup? {
    return mBottomShowPopup.value
    }
}