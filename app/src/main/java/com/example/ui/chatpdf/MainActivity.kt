package com.example.ui.chatpdf

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.chatpdf.databinding.ActivityMainBinding
import com.example.data.ChatRepository
import com.example.ui.viewmodel.ChatViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var viewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Chỉnh bottom padding để layout co lại khi bàn phím bật
            view.setPadding(0, 0, 0, imeInsets.bottom.coerceAtLeast(navBarInsets.bottom))
            insets
        }
        setupViews()
        val repository = ChatRepository()
        viewModel = ViewModelProvider(this, ChatViewModel(repository).Factory(repository))[ChatViewModel::class.java]
        viewModel.fakeData()
        observeData()
    }

    private fun observeData() {
        viewModel.messages.observe(this) { messages ->
            chatAdapter.submitList(messages)
            binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
        }
    }

    private fun setupViews() {
        chatAdapter = ChatAdapter()
        binding.recyclerViewMessages.adapter = chatAdapter
        // 1. Nhấn Enter (Done/Send) trên bàn phím
        binding.inputBar.editTextMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
                sendMessage()
                true
            } else {
                false
            }
        }

        // 2. Nhấn nút Send
        binding.inputBar.buttonSend.setOnClickListener {
            sendMessage()
        }

    }

    private fun sendMessage() {
        val messageText = binding.inputBar.editTextMessage.text.toString().trim()
        if (messageText.isNotEmpty()) {
            viewModel.sendMessage(messageText)

            // Clear EditText
            binding.inputBar.editTextMessage.text?.clear()

            // Ẩn bàn phím
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.inputBar.editTextMessage.windowToken, 0)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}