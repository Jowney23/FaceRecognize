package com.tsl.app.fragment.set

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.jowney.common.util.FileTool
import com.jowney.common.util.logger.L
import com.open.face.core.ArcAlgorithmHelper
import com.open.face.core.CacheHelper
import com.open.face.model.EventTips
import com.open.face.model.TipMessageCode
import com.open.face.model.TipMessageCode.MESSAGE_LOAD_TEMPLATE_END
import com.tsl.app.R
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.concurrent.Executors


class ImageEnrollFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_enroll, container, false)
        view.findViewById<Button>(R.id.fie_enroll_bt).setOnClickListener {
            val startTime = System.currentTimeMillis()
            Executors.newSingleThreadExecutor().execute {
                val list: List<File>? = FileTool.listFilesInDir(CacheHelper.LIFE_IMAGE_DB_PATH_DIR)
                list?.forEach { file ->

                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    ArcAlgorithmHelper.getInstance().enrollFaceFeatureBitmap(bitmap)
                    L.v("建模耗费时间：${System.currentTimeMillis()}  $startTime}")
                }
            }
        }
        return view
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventBusReceive(event: EventTips<String>){

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}