package org.engrave.packup.ui.login

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.engrave.packup.data.account.AccountRepository

class PackupLoginViewModel @ViewModelInject constructor(
    private val accountRepository: AccountRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _userEmail = ""
    private var _password = ""
    private var _verificationCode = ""

    val isUserEmailInvalid = MutableLiveData(false)
    val isPasswordInvalid = MutableLiveData(false)
    val isUserEmailOccupied = MutableLiveData(false)
    val needRegister = MutableLiveData(false)

    var userEmail
        get() = _userEmail
        set(value) {
            if(_userEmail.matches(emailRegex)) {
                _userEmail = value
                isUserEmailInvalid.value = true
            }
            else
                isUserEmailInvalid.value = false
        }

    var password
        get() = _password
        set(value) {
            if(_password.matches(passwordRegex)){
                _password = value
                isPasswordInvalid.value = true
            }
            else
                isPasswordInvalid.value = false
        }


    suspend fun validatePackupAccount(){
        TODO()
    }

    fun onPackupAccountValidated(){
        TODO()
    }

    companion object{
        val emailRegex = """^[.a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+${'$'}""".toRegex()
        val passwordRegex: Regex = TODO()

    }
}