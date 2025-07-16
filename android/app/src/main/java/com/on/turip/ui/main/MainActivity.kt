package com.on.turip.ui.main

import androidx.activity.viewModels
import com.on.turip.databinding.ActivityMainBinding
import com.on.turip.ui.common.base.BaseActivity

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override val viewModel: MainViewModel by viewModels()

    override val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
}