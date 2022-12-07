package com.open.face.core

import android.graphics.Bitmap
import android.graphics.Rect
import com.jowney.common.util.logger.L2
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FaceBitmapUtil {
    fun saveBitmap(bitmap: Bitmap, savePath: String, fileName: String) {
        val filePic: File
        try {
            filePic = File("$savePath$fileName.jpg")
            if (!filePic.exists()) {
                filePic.parentFile.mkdirs()
                filePic.createNewFile()
            }
            val fos = FileOutputStream(filePic)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            L2.e("保存图片失败 :${e.message}")
            return
        }
    }

    /**
     * 将图像中需要截取的Rect向外扩张一倍，若扩张一倍会溢出，则扩张到边界，若Rect已溢出，则收缩到边界
     *
     * @param width   图像宽度
     * @param height  图像高度
     * @param srcRect 原Rect
     * @return 调整后的Rect
     */
    private fun getBestRect(width: Int, height: Int, srcRect: Rect?): Rect? {
        if (srcRect == null) {
            return null
        }
        val rect = Rect(srcRect)

        // 原rect边界已溢出宽高的情况
        val maxOverFlow = Math.max(
            -rect.left,
            Math.max(-rect.top, Math.max(rect.right - width, rect.bottom - height))
        )
        if (maxOverFlow >= 0) {
            rect.inset(maxOverFlow, maxOverFlow)
            return rect
        }

        // 原rect边界未溢出宽高的情况
        var padding = rect.height() / 2

        // 若以此padding扩张rect会溢出，取最大padding为四个边距的最小值
        if (!(rect.left - padding > 0 && rect.right + padding < width && rect.top - padding > 0 && rect.bottom + padding < height)) {
            padding = Math.min(
                Math.min(Math.min(rect.left, width - rect.right), height - rect.bottom),
                rect.top
            )
        }
        rect.inset(-padding, -padding)
        return rect
    }
}