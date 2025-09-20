package com.example.intentfilterdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.intentfilterdemo.R

/**
 * WebActivity - Ví dụ về Intent Filter cho ACTION_VIEW với URL
 *
 * Activity này được khai báo trong AndroidManifest.xml với Intent Filter
 * để xử lý các URL https:// được mở từ ứng dụng khác.
 *
 * Intent Filter:
 * - Action: android.intent.action.VIEW
 * - Category: android.intent.category.DEFAULT và android.intent.category.BROWSABLE
 * - Data: scheme="https"
 *
 * Bảo mật: android:exported="true" - Cho phép ứng dụng khác mở Activity này
 */
class WebActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tạo WebView programmatically
        webView = WebView(this).apply {
            webViewClient = WebViewClient() // Đảm bảo link mở trong app
            settings.javaScriptEnabled = true // Cho phép JavaScript (có thể tắt nếu không cần)
        }

        setContentView(webView)

        // Xử lý Intent nhận được
        handleIntent(intent)
    }

    /**
     * Xử lý Intent khi Activity được mở
     */
    private fun handleIntent(intent: Intent?) {
        intent?.let {
            val action = it.action
            val data = it.data

            if (Intent.ACTION_VIEW == action && data != null) {
                val url = data.toString()

                // Validation: Chỉ xử lý URL https
                if (url.startsWith("https://")) {
                    webView.loadUrl(url)
                    Toast.makeText(this, "Đang mở: $url", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Chỉ hỗ trợ URL HTTPS", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Toast.makeText(this, "Không thể xử lý Intent này", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Xử lý Intent mới (khi Activity đã chạy)
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
}