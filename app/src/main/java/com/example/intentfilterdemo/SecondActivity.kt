package com.example.intentdemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

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
     * Xử lý Intent nhận được từ MainActivity
     */
    private fun handleReceivedIntent() {
        val initialMessage = intent.getStringExtra("initial_message")
        if (!initialMessage.isNullOrEmpty()) {
            etData.setText(initialMessage)
            Toast.makeText(this, "Nhận được tin nhắn: $initialMessage", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Lưu dữ liệu và trả về MainActivity
     */
    private fun saveAndReturnData() {
        val name = etName.text.toString().trim()
        val ageText = etAge.text.toString().trim()
        val data = etData.text.toString().trim()

        // Validation
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
     * Xử lý Intent chia sẻ từ ứng dụng khác (Intent Filter)
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            handleShareIntent(it)
        }
    }

    private fun handleShareIntent(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (!sharedText.isNullOrEmpty()) {
            etData.setText(sharedText)
            Toast.makeText(this, "Nhận chia sẻ: $sharedText", Toast.LENGTH_LONG).show()
        }
    }
}