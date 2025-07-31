package com.on.turip.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.on.turip.R
import com.on.turip.databinding.ActivityMainBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.main.favorite.FavoriteFragment
import com.on.turip.ui.main.home.HomeFragment

class MainActivity : BaseActivity<ActivityMainBinding>() {
    val viewModel: MainViewModel by viewModels()

    override val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var activeTag: String? = null

    private var backPressedTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleDoubleBackPressToExit()
        initBottomNavigation()

        if (savedInstanceState == null) {
            binding.bottomNavMenu.selectedItemId = R.id.menu_fragment_home
        }
    }

    private fun initBottomNavigation() {
        binding.bottomNavMenu.itemIconTintList = null

        binding.bottomNavMenu.setOnItemSelectedListener { item ->
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

    private fun switchFragment(
        target: Class<out Fragment>,
        tag: String,
    ) {
        if (activeTag == tag) return

        val activeFragment = supportFragmentManager.findFragmentByTag(activeTag)
        val targetFragment = supportFragmentManager.findFragmentByTag(tag)

        supportFragmentManager.commit {
            activeFragment?.let { hide(it) }

            if (targetFragment == null) {
                add(R.id.main_fragment_container, target, null, tag)
            } else {
                show(targetFragment)
            }
        }
        activeTag = tag
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
