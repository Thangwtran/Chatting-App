package com.example.ui.chatpdf

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.chatpdf.databinding.ActivityMainBinding
import com.example.data.ChatRepository
import com.example.data.api.RetrofitInstance
import com.example.data.model.Message
import com.example.ui.viewmodel.ChatViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var questionSuggestAdapter: QuestionSuggestAdapter
    private lateinit var viewModel: ChatViewModel

    private val language = "Tiếng Việt"
    private val urlFile =
        "https://cglhceoknxoptndpqevt.supabase.co/storage/v1/object/file_gemini/file_gemini_1757389528990.pdf"

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
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // hide gesture bar/ navigation bar
        WindowCompat
            .getInsetsController(window, window.decorView)
            .hide(WindowInsetsCompat.Type.navigationBars())

        val api = RetrofitInstance.getApiService()
        val repository = ChatRepository(api)
        viewModel = ViewModelProvider(
            this,
            ChatViewModel(repository).Factory(repository)
        )[ChatViewModel::class.java]
        viewModel.getSuggestQuestion(
            language = "Tiếng Việt",
            prompt = "Trích xuất 3 câu hỏi quan trọng nhất từ tài liệu.",
            urlFile = "https://cglhceoknxoptndpqevt.supabase.co/storage/v1/object/file_gemini/file_gemini_1757389528990.pdf"
        )
        Log.d("TAG", "onCreate: ${viewModel.token}")
        setupViews()
        observeData()

    }

    private fun observeData() {
        viewModel.messages.observe(this) { messages ->
            Log.d("TAG", "observeData: $messages")
            if (messages.isEmpty()) {
                binding.containerIntro.visibility = View.VISIBLE
                binding.pdfContainer.visibility = View.VISIBLE
                binding.rvQuestionSuggest.visibility = View.GONE
            } else {
                binding.containerIntro.visibility = View.GONE
                binding.pdfContainer.visibility = View.GONE
                binding.rvQuestionSuggest.visibility = View.VISIBLE
                chatAdapter.submitList(messages)
                binding.recyclerViewMessages.post {
                    binding.recyclerViewMessages.scrollToPosition(chatAdapter.itemCount - 1)
                }
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            chatAdapter.isThinking = isLoading
            Log.d("Main", "observeData: $isLoading")
            if (isLoading) {
                val thinkingMessage = Message(text = "Thinking...", isUser = false)
                chatAdapter.addThinkingMessage(thinkingMessage)
            }
        }

        // Khi bot có response
        viewModel.botResponse.observe(this) { botMsg ->
            if (botMsg != null) {
                chatAdapter.updateLastMessage(botMsg)
                chatAdapter.isThinking = false
            }
        }

        viewModel.suggestedQuestion.observe(this) { questions ->
            val questionList = listOf(
                QuestionSuggest(questions.quest1),
                QuestionSuggest(questions.quest2),
                QuestionSuggest(questions.quest3)
            )
            questionSuggestAdapter.submitList(questionList)
        }

        viewModel.isError.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.inputBar.buttonSend.isEnabled = !isLoading
        }

    }

    private fun setupViews() {
        chatAdapter = ChatAdapter()
        binding.recyclerViewMessages.adapter = chatAdapter
        // 1. Nhấn Enter (Done/Send) trên bàn phím
        binding.inputBar.editTextMessage.setOnEditorActionListener { _, actionId, _ ->
            val messageText = binding.inputBar.editTextMessage.text.toString().trim()
            if ((actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE)
                && viewModel.isLoading.value == false // kiểm tra loading
            ) {
                sendMessage(messageText)
                true
            } else {
                false
            }
        }

        // 2. Nhấn nút Send
        binding.inputBar.buttonSend.setOnClickListener {
            if (viewModel.isLoading.value == false) {
                val messageText = binding.inputBar.editTextMessage.text.toString().trim()
                sendMessage(messageText)
            }
        }
        // Question Suggest adapter
        questionSuggestAdapter = QuestionSuggestAdapter(object : OnSuggestTextSelected {
            override fun onSuggestTextSelected(text: String) {
                if (viewModel.isLoading.value == false) {
                    sendMessage(text)
                }
            }

        })
        binding.rvQuestionSuggest.adapter = questionSuggestAdapter

    }

    private fun sendMessage(messageText: String) {
        if (messageText.isNotEmpty()) {
            viewModel.sendPromptToBot(
                language = language,
                prompt = messageText,
                urlFile = urlFile
            )
        }
        // Clear EditText
        binding.inputBar.editTextMessage.text?.clear()

        // Ẩn bàn phím
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputBar.editTextMessage.windowToken, 0)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}