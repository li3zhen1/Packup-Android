package org.engrave.packup.util

import android.graphics.drawable.Animatable
import android.net.Uri
import android.util.Log
//import com.facebook.drawee.backends.pipeline.Fresco
//import com.facebook.drawee.controller.BaseControllerListener
//import com.facebook.drawee.view.SimpleDraweeView
//import com.facebook.imagepipeline.image.ImageInfo
//
//fun SimpleDraweeView.setControllerListener(
//    uri: Uri
//) {
//    val lp = layoutParams
//    val controllerListener = object : BaseControllerListener<ImageInfo?>() {
//        override fun onFinalImageSet(
//            id: String?,
//            imageInfo: ImageInfo?,
//            anim: Animatable?
//        ) {
//            if (imageInfo == null) {
//                return
//            }
//            val height: Int = imageInfo.height
//            val width: Int = imageInfo.width
//            lp.width = lp.width
//            lp.height = ((lp.width * height).toFloat() / width.toFloat()).toInt()
//
//            Log.e("IMAGE_INFO", "$width -- $height -- ${lp.width} -- ${lp.height}")
//            layoutParams = lp
//            // redundant?
//        }
//
//        override fun onIntermediateImageSet(id: String, imageInfo: ImageInfo?) {
//            Log.d("TAG", "Intermediate image received")
//        }
//
//        override fun onFailure(id: String, throwable: Throwable) {
//            throwable.printStackTrace()
//        }
//    }
//    controller = Fresco.newDraweeControllerBuilder()
//        .setControllerListener(controllerListener)
//        .setUri(uri)
//        .build()
//}