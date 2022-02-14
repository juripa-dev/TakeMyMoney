package com.juripa.takemymoney

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.juripa.takemymoney.databinding.ActivityLaunchBinding
import com.juripa.takemymoney.databinding.ActivityMainBinding

class LaunchActivity : AppCompatActivity() {

    private var _binding: ActivityLaunchBinding? = null
    private val binding: ActivityLaunchBinding get() = _binding!!

    // fixme : develop launchViewModel
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_launch)

        viewModel.onInit()

        binding.startButton.setOnClickListener {
            viewModel.onClickStart()
            startMain()
        }

        val isAlreadyStarted = Prefs.getString(this, "started")?.isNotEmpty() == true

        if (isAlreadyStarted) {
            startMain()
        }
    }

    private fun startMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}