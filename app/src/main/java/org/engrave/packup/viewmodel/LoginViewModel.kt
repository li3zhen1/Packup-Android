package org.engrave.packup.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.engrave.packup.network.pku.iaaa.fetchIaaaToken
import org.engrave.packup.util.device.isNetworkAvailable
import org.engrave.packup.util.exception.EXCEPTION_NETWORK_UNAVAILABLE

class LoginViewModel(application: Application): AndroidViewModel(application) {
    private val studentId = MutableLiveData<String>()
    private val password = MutableLiveData<String>()

    private val studentIdRegex = "[0-9]{10}$".toRegex()

    private val _shouldDisplayStudentIdError = MutableLiveData(false)
    val shouldDisplayStudentIdError: LiveData<Boolean> get() = _shouldDisplayStudentIdError

    private val _shouldDisplayPasswordError = MutableLiveData(false)
    val shouldDisplayPasswordError: LiveData<Boolean> get() = _shouldDisplayPasswordError

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isValidationSucceed = MutableLiveData(false)
    val isValidationSucceed: LiveData<Boolean> get() = _isValidationSucceed

    private val _isValidating = MutableLiveData(false)
    val isValidating: LiveData<Boolean> get() = _isValidating

    fun validateLoginInfo() =
        viewModelScope.launch {
            val currentStudentId = studentId.value
            val currentPassword = password.value
            if (currentStudentId.isNullOrEmpty() || !currentStudentId.matches(studentIdRegex)) {
                _shouldDisplayStudentIdError.value = true
                _shouldDisplayPasswordError.value = false
            } else if (currentPassword.isNullOrEmpty()) {
                _shouldDisplayPasswordError.value = true
            } else {
                _shouldDisplayStudentIdError.value = false
                _shouldDisplayPasswordError.value = false
                _isValidationSucceed.value =
                    attemptValidateViaIaaa(currentStudentId, currentPassword)
                if (!isValidationSucceed.value!!) _shouldDisplayPasswordError.value = true
            }
        }

    private suspend fun attemptValidateViaIaaa(studentId: String, password: String): Boolean {
        _isValidating.value = true
        var isSucceed: Boolean
        try {
            if (!isNetworkAvailable(getApplication()))
                throw Exception(EXCEPTION_NETWORK_UNAVAILABLE)
            withContext(Dispatchers.IO) {
                fetchIaaaToken(studentId = studentId, password = password)
            }
            isSucceed = true
        } catch (e: java.lang.Exception) {
            _errorMessage.value = e.message
            isSucceed = false
        }
        _isValidating.value = false
        return isSucceed
    }

    fun setStudentId(value: String) {
        studentId.value = value
        if (value.matches(studentIdRegex))
            _shouldDisplayStudentIdError.value = false
    }

    fun setPassword(value: String) {
        password.value = value
    }

    fun getStudentId() = studentId.value.orEmpty()
    fun getPassword() = password.value.orEmpty()

}