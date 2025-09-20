package com.example.intentfilterdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.intentfilterdemo.R

/**
 * MainActivity - Demo Implicit Intent và ActivityResultLauncher
 *
 * PHẦN 3: IMPLICIT INTENT VÀ INTENT FILTER
 *
 * Implicit Intent: Không chỉ định component cụ thể, nhờ hệ thống chọn Activity phù hợp
 * Intent Resolution: Hệ thống tìm Activity có Intent Filter phù hợp với Intent
 * Intent Filter: Khai báo trong AndroidManifest.xml với Action, Category, Data
 *
 * Bảo mật trong Intent:
 * - android:exported="true": Cho phép ứng dụng khác mở Activity này
 * - Xử lý dữ liệu nhạy cảm: Không gửi thông tin nhạy cảm qua Intent
 * - Validation: Luôn validate dữ liệu nhận được
 *
 * ActivityResultLauncher: Thay thế cho deprecated startActivityForResult()
 * - Ưu điểm: Không cần override onActivityResult(), dễ quản lý callback
 * - Cách sử dụng: registerForActivityResult() với contract phù hợp
 */
class MainActivity : AppCompatActivity() {

    // ActivityResultLauncher - Thay thế cho startActivityForResult()
    // Khai báo ở class level để có thể sử dụng trong toàn bộ Activity
    private lateinit var inputDataLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
    private lateinit var shareTextLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActivityResultLaunchers()
        setupClickListeners()
    }

    /**
     * Đăng ký ActivityResultLauncher - Thay thế cho startActivityForResult()
     *
     * ActivityResultLauncher cung cấp:
     * - Callback rõ ràng và dễ quản lý
     * - Không cần override onActivityResult()
     * - Type-safe với ActivityResultContracts
     * - Tự động xử lý lifecycle
     */
    private fun setupActivityResultLaunchers() {
        // Launcher để nhận dữ liệu từ SecondActivity
        // Contract: StartActivityForResult() - mở Activity và nhận kết quả
        inputDataLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // Xử lý kết quả trả về
            if (result.resultCode == RESULT_OK) {
                // Bảo mật: Luôn validate dữ liệu nhận được
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

        // Launcher để chia sẻ text - sử dụng cho Intent.createChooser()
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
     *
     * Implicit Intent: Không chỉ định component cụ thể
     * - Action: ACTION_SEND - gửi dữ liệu
     * - Type: text/plain - loại dữ liệu
     * - Extra: EXTRA_TEXT - nội dung chia sẻ
     *
     * Intent.createChooser(): Hiển thị dialog chọn app để xử lý Intent
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
     * Mở website sử dụng Implicit Intent với ACTION_VIEW
     *
     * Implicit Intent cho URL:
     * - Action: ACTION_VIEW
     * - Data: Uri của website
     * - Hệ thống sẽ tìm app có thể xử lý URL (browser, WebActivity của app khác)
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
     *
     * Implicit Intent cho cuộc gọi:
     * - Action: ACTION_CALL
     * - Data: Uri với scheme "tel:"
     *
     * Bảo mật: Kiểm tra quyền CALL_PHONE trước khi thực hiện
     */
    private fun callPhone() {
        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:0123456789"))

        // Bảo mật: Kiểm tra quyền trước khi gọi
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // Yêu cầu quyền trước khi gọi
            Toast.makeText(this, "Cần quyền CALL_PHONE", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            startActivity(callIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Không thể thực hiện cuộc gọi", Toast.LENGTH_SHORT).show()
        }
    }
}