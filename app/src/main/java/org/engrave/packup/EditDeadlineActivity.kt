package org.engrave.packup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.engrave.packup.databinding.ActivityEditDeadlineBinding
import org.engrave.packup.ui.editdeadline.EditDeadlineViewModel


@AndroidEntryPoint
class EditDeadlineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditDeadlineBinding

    private val editDeadlineViewModel: EditDeadlineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDeadlineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addDeadlineTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editDeadlineViewModel.editingDeadlineTitle.value = s.toString()
                editDeadlineViewModel.editingDeadlineDescription.value =
                    "你输入了 ${s.toString().length} 个字符"
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        binding.deadlineDetailNavButton.setOnClickListener {
            finish()
        }

        binding.editDeadlineFragmentConfirmButton.setOnClickListener {
            editDeadlineViewModel.commitDeadline()
            finish()
        }
    }
}