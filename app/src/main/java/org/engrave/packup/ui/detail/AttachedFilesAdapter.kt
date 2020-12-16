package org.engrave.packup.ui.detail

import android.R.attr.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.engrave.packup.R
import org.engrave.packup.data.deadline.DeadlineAttachedFile
import org.engrave.packup.ui.attached.AttachedFileBottomSheetFragment
import org.engrave.packup.ui.attached.FILE_BLOB_DATA_SERIALIZED


class AttachedFilesAdapter(
    val context: AppCompatActivity,
    var files: List<DeadlineAttachedFile>
) : RecyclerView.Adapter<AttachedFilesAdapter.ViewHolder>(
) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileName: TextView = itemView.findViewById(R.id.item_file_name)
        val fileIcon: ImageView = itemView.findViewById(R.id.item_file_extension_icon)
        val fileDesc: TextView = itemView.findViewById(R.id.item_file_desc)
        val container: ConstraintLayout = itemView.findViewById(R.id.item_file_blob_container)
    }

    fun postFileList(fs: List<DeadlineAttachedFile>){
        files = fs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_file_blobs, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        holder.fileName.text = file.fileName
        holder.fileDesc.text = when (file.downloadStatus) {
            DeadlineAttachedFile.STATUS_NOT_DOWNLOADED -> "未下载"
            DeadlineAttachedFile.STATUS_DOWNLOADED -> "已下载"
            DeadlineAttachedFile.STATUS_DOWNLOADING -> "正在下载"
            else -> "错误"
        }

        holder.container.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(FILE_BLOB_DATA_SERIALIZED, file.asString())
            val fileFragment = AttachedFileBottomSheetFragment().apply {
                arguments = bundle
            }
            fileFragment.show(context.supportFragmentManager, "FILE_BLOB_BOTTOM_SHEET")
        }


        holder.fileIcon.setImageDrawable(
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
    }

    override fun getItemCount(): Int = files.size
}