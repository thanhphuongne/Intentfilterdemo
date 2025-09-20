package com.example.intentdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    // Khai báo ActivityResultLauncher cho việc nhận kết quả
    private lateinit var inputDataLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
    private lateinit var shareTextLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActivityResultLaunchers()
        setupClickListeners()
    }

    /**
     * Đăng ký ActivityResultLauncher
     */
    private fun setupActivityResultLaunchers() {
        // Launcher để nhận dữ liệu từ SecondActivity
        inputDataLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data?.getStringExtra("user_input")
                val name = result.data?.getStringExtra("user_name")
                val age = result.data?.getIntExtra("user_age", 0)
                
                // Hiển thị kết quả
                Toast.makeText(
                    this,
                    "Nhận được: Tên: $name, Tuổi: $age, Dữ liệu: $data",
                    Toast.LENGTH_LONG
                ).show()
                
                // Cập nhật UI
                findViewById<Button>(R.id.btnShowResult)?.text = "Dữ liệu: $data"
            }
        }

        // Launcher để chia sẻ text
        shareTextLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this, "Đã chia sẻ thành công!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Thiết lập sự kiện click cho các button
     */
    private fun setupClickListeners() {
        // Button mở SecondActivity để nhập dữ liệu
        findViewById<Button>(R.id.btnOpenSecondActivity).setOnClickListener {
            openSecondActivityForInput()
        }

        // Button chia sẻ text
        findViewById<Button>(R.id.btnShareText).setOnClickListener {
            shareText()
        }

        // Button mở website
        findViewById<Button>(R.id.btnOpenWebsite).setOnClickListener {
            openWebsite()
        }

        // Button gọi điện (Implicit Intent)
        findViewById<Button>(R.id.btnCallPhone).setOnClickListener {
            callPhone()
        }
    }

    /**
     * Mở SecondActivity để nhập dữ liệu sử dụng ActivityResultLauncher
     */
    private fun openSecondActivityForInput() {
        val intent = Intent(this, SecondActivity::class.java).apply {
            // Có thể gửi dữ liệu ban đầu
            putExtra("initial_message", "Xin chào từ MainActivity!")
        }
        inputDataLauncher.launch(intent)
    }

    /**
     * Chia sẻ text sử dụng Implicit Intent
     */
    private fun shareText() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Chia sẻ từ ứng dụng Intent Demo!")
        }
        
        // Đảm bảo có app có thể xử lý intent này
        val chooserIntent = Intent.createChooser(shareIntent, "Chia sẻ qua...")
        shareTextLauncher.launch(chooserIntent)
    }

    /**
     * Mở website sử dụng ACTION_VIEW
     */
    private fun openWebsite() {
        val websiteIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com"))
        try {
            startActivity(websiteIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Không thể mở trình duyệt", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Gọi điện sử dụng Implicit Intent
     */
    private fun callPhone() {
        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:0123456789"))
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) 
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // Yêu cầu quyền trước khi gọi
            Toast.makeText(this, "Cần quyền CALL_PHONE", Toast.LENGTH_SHORT).show()
            return
        }
        startActivity(callIntent)
    }
}