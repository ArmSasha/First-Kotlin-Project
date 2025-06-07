package com.example.firstproject

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val webView = findViewById<WebView>(R.id.webView)
        val url = intent.getStringExtra("url")

        webView.webViewClient = WebViewClient()  // чтобы ссылки открывались внутри WebView
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url ?: "https://reddit.com")
    }
}
