package com.example.intentfilterdemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.intentfilterdemo.R

/**
 * SecondActivity - Demo nhận dữ liệu từ MainActivity và Intent Filter
 *
 * Chức năng:
 * 1. Nhận dữ liệu từ MainActivity qua Intent (Explicit Intent)
 * 2. Trả về dữ liệu cho MainActivity sử dụng setResult()
 * 3. Xử lý Intent chia sẻ từ ứng dụng khác (Implicit Intent qua Intent Filter)
 *
 * Intent Filter khai báo trong AndroidManifest.xml:
 * - Action: android.intent.action.SEND
 * - Category: android.intent.category.DEFAULT
 * - Data: mimeType="text/plain"
 *
 * Bảo mật: android:exported="true" - Cho phép ứng dụng khác mở Activity này
 */
class SecondActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etData: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        setupUI()
        handleReceivedIntent()
    }

    /**
     * Thiết lập giao diện
     */
    private fun setupUI() {
        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etData = findViewById(R.id.etData)

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveAndReturnData()
        }

        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    /**
     * Xử lý Intent nhận được từ MainActivity (Explicit Intent)
     *
     * Khi MainActivity mở SecondActivity với Intent có extra data,
     * dữ liệu sẽ được nhận ở đây thông qua intent property.
     */
    private fun handleReceivedIntent() {
        val initialMessage = intent.getStringExtra("initial_message")
        if (!initialMessage.isNullOrEmpty()) {
            etData.setText(initialMessage)
            Toast.makeText(this, "Nhận được tin nhắn: $initialMessage", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Lưu dữ liệu và trả về MainActivity sử dụng setResult()
     *
     * Hai chiều giao tiếp giữa Activities:
     * 1. MainActivity gửi dữ liệu qua Intent (putExtra)
     * 2. SecondActivity trả về dữ liệu qua setResult() với Intent chứa dữ liệu
     *
     * setResult():
     * - RESULT_OK: Thành công, có dữ liệu trả về
     * - RESULT_CANCELED: Hủy bỏ, không có dữ liệu
     * - Intent: Chứa dữ liệu trả về (có thể null)
     */
    private fun saveAndReturnData() {
        val name = etName.text.toString().trim()
        val ageText = etAge.text.toString().trim()
        val data = etData.text.toString().trim()

        // Validation - Bảo mật: Luôn validate dữ liệu trước khi xử lý
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show()
            return
        }

        if (ageText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tuổi", Toast.LENGTH_SHORT).show()
            return
        }

        val age = ageText.toIntOrNull()
        if (age == null || age < 0 || age > 150) {
            Toast.makeText(this, "Tuổi không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        // Tạo Intent để trả về dữ liệu
        // Bảo mật: Không gửi thông tin nhạy cảm qua Intent
        val resultIntent = Intent().apply {
            putExtra("user_name", name)
            putExtra("user_age", age)
            putExtra("user_input", data)
        }

        // Đặt result và kết thúc Activity
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    /**
     * Xử lý Intent mới khi Activity đã chạy (Intent Filter)
     *
     * Khi ứng dụng khác chia sẻ text và chọn app này,
     * nếu SecondActivity đã mở thì onNewIntent() sẽ được gọi
     * thay vì onCreate() mới.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleShareIntent(intent)
    }

    /**
     * Xử lý Intent chia sẻ từ ứng dụng khác
     *
     * Intent Filter trong AndroidManifest.xml:
     * - Action: android.intent.action.SEND
     * - Data: mimeType="text/plain"
     *
     * Khi user chọn "Chia sẻ" trong app khác và chọn app này,
     * hệ thống sẽ mở SecondActivity với Intent chứa EXTRA_TEXT.
     */
    private fun handleShareIntent(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (!sharedText.isNullOrEmpty()) {
            etData.setText(sharedText)
            Toast.makeText(this, "Nhận chia sẻ: $sharedText", Toast.LENGTH_LONG).show()
        }
    }
}