package org.engrave.packup.util

import android.content.Context
import java.io.InputStream

fun InputStream.downloadBlob(context: Context, fileName: String){
    context.openFileOutput(
        fileName, Context.MODE_PRIVATE
    )
}