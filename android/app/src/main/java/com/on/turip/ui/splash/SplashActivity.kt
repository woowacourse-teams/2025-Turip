package com.on.turip.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.on.turip.databinding.ActivitySplashBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDuration()
    }

    private fun setupDuration() {
        lifecycleScope.launch {
            delay(1500)
            startActivity(MainActivity.newIntent(this@SplashActivity))
            finish()
        }
    }
}
