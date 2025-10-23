package com.on.turip.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.on.turip.R
import com.on.turip.databinding.ActivityMainBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.main.favorite.FavoriteFragment
import com.on.turip.ui.main.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var backPressedTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleDoubleBackPressToExit()
        initBottomNavigation()
        setupBottomNavigation()

        if (savedInstanceState == null) {
            binding.bnvMain.selectedItemId = R.id.menu_fragment_home
        }
    }

    private fun initBottomNavigation() {
        binding.bnvMain.itemIconTintList = null

        binding.bnvMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_fragment_home -> {
                    HomeFragment::class.java.let { switchFragment(it, it.simpleName) }
                    return@setOnItemSelectedListener true
                }

                R.id.menu_fragment_favorite -> {
                    FavoriteFragment::class.java.let { switchFragment(it, it.simpleName) }
                    return@setOnItemSelectedListener true
                }

                else -> return@setOnItemSelectedListener false
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bnvMain.setOnApplyWindowInsetsListener(null)
    }

    private fun switchFragment(
        target: Class<out Fragment>,
        tag: String,
    ) {
        val fragments = supportFragmentManager.fragments
        val targetFragment = supportFragmentManager.findFragmentByTag(tag)

        if (targetFragment?.isVisible == true) return

        supportFragmentManager.commit {
            fragments.filter { it.isVisible }.forEach { hide(it) }

            if (targetFragment == null) {
                add(R.id.fcv_main, target, null, tag)
            } else {
                show(targetFragment)
            }
        }
    }

    private fun handleDoubleBackPressToExit() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (System.currentTimeMillis() - backPressedTime <= 2000) {
                        finish()
                    } else {
                        backPressedTime = System.currentTimeMillis()
                        Toast
                            .makeText(
                                this@MainActivity,
                                getString(R.string.main_double_back_pressed_to_exit),
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                }
            },
        )
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}
