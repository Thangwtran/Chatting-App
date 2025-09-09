package com.example.ui.chatpdf

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
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
        val api = RetrofitInstance.getApiService()
        val repository = ChatRepository(api)
        viewModel = ViewModelProvider(this, ChatViewModel(repository).Factory(repository))[ChatViewModel::class.java]
        viewModel.fakeData()
        viewModel.getSuggestQuestion(
            language = "Tiếng Việt",
            prompt = "Trích xuất 3 câu hỏi quan trọng nhất từ tài liệu.",
            urlFile = "https://cglhceoknxoptndpqevt.supabase.co/storage/v1/object/file_gemini/file_gemini_1757389528990.pdf"
        )
        Log.d("TAG", "onCreate: ${viewModel.token}")
        observeData()
    }

    private fun observeData() {
        viewModel.messages.observe(this) { messages ->
            chatAdapter.submitList(messages)
            binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
        }

        viewModel.answerRag.observe(this){
            val botMessage = Message(text = it, isUser = false)
            viewModel.addMessage(botMessage)
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
        // Question Suggest
        questionSuggestAdapter = QuestionSuggestAdapter()
        binding.rvQuestionSuggest.adapter = questionSuggestAdapter
//        val questionList = listOf(
//            QuestionSuggest("Theo khảo sát mới nhất của McKinsey Global Survey về AI, có bao nhiêu phần trăm các tổ chức cho biết họ sử dụng AI trong ít nhất một chức năng kinh doanh?"),
//            QuestionSuggest("Số liệu nào cho thấy các tổ chức đang tích cực quản lý các rủi ro liên quan đến AI tạo sinh, bao gồm các rủi ro về độ chính xác, an ninh mạng và vi phạm sở hữu trí tuệ?"),
//            QuestionSuggest("Tỷ lệ phần trăm các tổ chức cho biết họ sử dụng AI tạo sinh để tạo ra đầu ra văn bản là bao nhiêu?")
//        )
//        questionSuggestAdapter.submitList(questionList)

    }

    fun botAnswer(message: String) {
        viewModel.chatWithBot(
            language = "Tiếng Việt",
            prompt = message,
            urlFile = "https://cglhceoknxoptndpqevt.supabase.co/storage/v1/object/file_gemini/file_gemini_1757389528990.pdf"
        )
    }

    private fun sendMessage() {
        val messageText = binding.inputBar.editTextMessage.text.toString().trim()
        if (messageText.isNotEmpty()) {
            viewModel.sendMessage(messageText)
            viewModel.chatWithBot(
                language = "Tiếng Việt",
                prompt = messageText,
                urlFile = "https://cglhceoknxoptndpqevt.supabase.co/storage/v1/object/file_gemini/file_gemini_1757389528990.pdf"
            )

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