package org.engrave.packup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import org.engrave.packup.data.account.AccountInfoRepository
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var accountInfoRepository: AccountInfoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val accountInfo = runBlocking { accountInfoRepository.getAccountInfo() }
        if (accountInfo.studentId.isEmpty() || accountInfo.password.isEmpty())
            startActivity(Intent(this, LoginActivity::class.java))
        else
            startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}