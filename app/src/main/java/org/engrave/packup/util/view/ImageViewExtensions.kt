package org.engrave.packup.util.view

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import org.engrave.packup.R
import org.engrave.packup.data.deadline.DeadlineAttachedFile

fun ImageView.setFileExtIcon(file: DeadlineAttachedFile, context: Context) = setImageDrawable(
    ContextCompat.getDrawable(
        context,
        when (file.fileType) {
            "genericfile" -> R.drawable.ic_genericfile
            "pdf" -> R.drawable.pdf
            "zip" -> R.drawable.zip
            "code" -> R.drawable.code
            "photo" -> R.drawable.photo
            "pptx" -> R.drawable.pptx
            "docx" -> R.drawable.docx
            "xlsx" -> R.drawable.xlsx

            "accdb" -> R.drawable.accdb
            "archive" -> R.drawable.archive
            "audio" -> R.drawable.audio
            "calendar" -> R.drawable.calendar
            "contact" -> R.drawable.contact
            "csv" -> R.drawable.csv
            "email" -> R.drawable.email
            "exe" -> R.drawable.exe
            "font" -> R.drawable.font
            "html" -> R.drawable.html
            "model" -> R.drawable.model
            "one" -> R.drawable.one
            "rtf" -> R.drawable.rtf
            "sysfile" -> R.drawable.sysfile
            "vector" -> R.drawable.vector
            "video" -> R.drawable.video
            "xml" -> R.drawable.xml
            else -> R.drawable.ic_genericfile
        }
    )
)