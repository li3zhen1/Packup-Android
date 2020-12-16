package org.engrave.packup.ui.attached

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.R
import org.engrave.packup.data.deadline.DeadlineAttachedFile
import org.engrave.packup.util.view.setFileExtIcon
import org.w3c.dom.Text


const val FILE_BLOB_DATA_SERIALIZED = "FILE_BLOB_DATA"


@AndroidEntryPoint
class AttachedFileBottomSheetFragment() : BottomSheetDialogFragment() {

    private lateinit var fileNameTextView: TextView
    private lateinit var fileDescTextView: TextView
    private lateinit var imagePreview: ImageView
    private lateinit var openExternalButton: Button
    private lateinit var shareButton: ImageButton
    private lateinit var extIcon: ImageView
    private lateinit var unsupportedTextView: TextView
    private lateinit var sorryImageView: ImageView

    private val fileViewModel: AttachedFileBottomSheetViewModel by viewModels()

    override fun onStart() {
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_file_blob, container, false).apply {
        fileNameTextView = findViewById(R.id.file_blob_fragment_file_name)
        fileDescTextView = findViewById(R.id.file_blob_fragment_file_desc)
        imagePreview = findViewById(R.id.file_blob_fragment_file_preview)
        openExternalButton = findViewById(R.id.file_blob_fragment_open_external_button)
        shareButton = findViewById(R.id.file_blob_fragment_share_button)
        extIcon = findViewById(R.id.file_blob_fragment_file_ext_icon)
        unsupportedTextView = findViewById(R.id.file_blob_unable_to_provide_preview)
        sorryImageView = findViewById(R.id.file_blob_fragment_file_preview_sorry_emoji)

        openExternalButton.setOnClickListener {
            activity?.let { it1 -> fileViewModel.fileBlob.value?.startExternalOpenIntent(it1) }
        }
        shareButton.setOnClickListener {
            activity?.let { it1 -> fileViewModel.fileBlob.value?.startShareIntent(it1) }
        }

        fileViewModel.fileBlob.observe(viewLifecycleOwner) {
            fileNameTextView.text = it.fileName
            fileDescTextView.text = it.url
            extIcon.setFileExtIcon(it, context)
            if (it.fileExt in NATIVE_IMAGE_VIEWABLE_EXTENSIONS) {
                imagePreview.setImageURI(Uri.parse(it.localUri))
                unsupportedTextView.visibility = View.GONE
                sorryImageView.visibility = View.GONE
                imagePreview.visibility = View.VISIBLE
            }
            else{
                imagePreview.visibility = View.GONE
                unsupportedTextView.visibility = View.VISIBLE
                sorryImageView.visibility = View.VISIBLE
            }
        }

        arguments?.let {
            fileViewModel.fileBlob.value = it.getString(FILE_BLOB_DATA_SERIALIZED)?.let { it1 ->
                DeadlineAttachedFile.fromString(
                    it1
                )
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        val FRESCO_IMAGE_VIEWABLE_EXTENSIONS = listOf(
            "jpg",
            "jpeg",
            "jiff",
            "webp",
            "png",
            "bmp",
            "gif"
        )
        val NATIVE_IMAGE_VIEWABLE_EXTENSIONS = listOf(
            "jpg",
            "jpeg",
            "png"
        )
    }


}