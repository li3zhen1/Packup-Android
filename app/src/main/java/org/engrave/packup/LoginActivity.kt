package org.engrave.packup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.engrave.packup.data.account.AccountInfoRepository
import org.engrave.packup.databinding.ActivityLoginBinding
import org.engrave.packup.ui.login.LoginViewModel
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding
    @Inject
    lateinit var accountInfoRepository: AccountInfoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.color.windowBackgroundColor,
                theme
            )
        )
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.apply {
            loginActivityStudentId.doOnTextChanged { text, _, _, _ ->
                loginViewModel.setStudentId(text.toString())
            }
            loginActivityPassword.apply {
                doOnTextChanged { text, _, _, _ ->
                    loginViewModel.setPassword(text.toString())
                }
                setOnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE)
                        loginViewModel.validateLoginInfo()
                    false
                }
            }
            loginActivityValidateButton.setOnClickListener {
                loginViewModel.validateLoginInfo()
            }
            loginActivityHintPrivacyPolicy.setOnClickListener {
                //TODO: Pop up privacy policy fragment.
            }
            loginActivityHintTermsOfUse.setOnClickListener {
                //TODO: Pop up Terms of Use fragment.
            }
        }
        loginViewModel.apply {
            isValidationSucceed.observe(this@LoginActivity) {
                if (it) lifecycleScope.launch{
                    onLoginInfoValidated()
                }
            }
            isValidating.observe(this@LoginActivity) {
                binding.loginActivityValidateButton.isEnabled = !it
                binding.loginActivityShimmer.visibility =
                    if (it) View.VISIBLE else View.INVISIBLE
            }
            shouldDisplayStudentIdError.observe(this@LoginActivity) {
                binding.loginActivityStudentIdErrorHint.visibility =
                    if (it) View.VISIBLE
                    else View.GONE
                binding.loginActivityStudentIdErrorBorder.visibility =
                    if (it) View.VISIBLE
                    else View.INVISIBLE
            }
            shouldDisplayPasswordError.observe(this@LoginActivity) {
                binding.loginActivityPasswordErrorHint.visibility =
                    if (it) View.VISIBLE
                    else View.GONE
                binding.loginActivityPasswordErrorBorder.visibility =
                    if (it) View.VISIBLE
                    else View.INVISIBLE
            }
            errorMessage.observe(this@LoginActivity) {
                binding.loginActivityPasswordErrorHint.text =
                    when (it) {
                        "User ID or Password is NOT correct." -> getString(R.string.login_error_e01)
                        "Too many attempts. Please sign in after a half hour." -> getString(R.string.login_error_e21)
                        "Network Unavailable." -> getString(R.string.login_error_network_unavailable)
                        else -> getString(R.string.login_error_unknown) + it
                    }
            }
        }
    }

    private suspend fun onLoginInfoValidated() {
        accountInfoRepository.setAccountInfo(
            loginViewModel.getStudentId(),
            loginViewModel.getPassword(),
            "pku"
        )
        withContext(Dispatchers.Main) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }
}