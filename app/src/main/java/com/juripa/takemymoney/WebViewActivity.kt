package com.juripa.takemymoney

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.juripa.takemymoney.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {

    private var _binding: ActivityWebViewBinding? = null
    private val binding: ActivityWebViewBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)
        initWebView()

        val url = intent.extras?.getString("WEB_URL")

        if (url.isNullOrEmpty()) {
            finish()
        } else {
            binding.webview.loadUrl(url)
        }
    }

    private fun initWebView() = with(binding.webview){
        webViewClient = WebViewClient()

        settings.apply {
            javaScriptEnabled = true
            setSupportMultipleWindows(true)
            setSupportZoom(true)
            cacheMode = WebSettings.LOAD_NO_CACHE
            domStorageEnabled = true
        }
    }
}