package com.example.ui.chatpdf

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.chatpdf.R
import com.example.data.model.Message
import io.noties.markwon.Markwon

class ChatAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_BOT = 1
    }

    private val messages = mutableListOf<Message>()
    var isThinking = false

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<Message>) {
        messages.clear()
        messages.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) =
        if (messages[position].isUser) TYPE_USER else TYPE_BOT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_USER) {
            val v = inflater.inflate(R.layout.item_message_user, parent, false)
            UserVH(v)
        } else {
            val v = inflater.inflate(R.layout.item_message_bot, parent, false)
            BotVH(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        when (holder) {
            is UserVH -> holder.bind(msg)
            is BotVH -> {
                holder.bind(msg, isThinking)
            }
        }
    }

    override fun getItemCount() = messages.size

    inner class UserVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txt: TextView = itemView.findViewById(R.id.textMessage)
        fun bind(m: Message) {
            txt.text = m.text
        }
    }

    inner class BotVH(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        private val textBotResponse: TextView = itemView.findViewById(R.id.text_bot_message)
        fun bind(m: Message, isThinking: Boolean) {
            val markwon = Markwon.create(itemView.context)
            val markdownText = m.text
            markwon.setMarkdown(textBotResponse, markdownText)

            val lottieView = itemView.findViewById<LottieAnimationView>(R.id.ai_loading)
            val textAiAssist = itemView.findViewById<TextView>(R.id.text_ai_assist)
            if (!m.isUser) {
                if (isThinking && adapterPosition == messages.size - 1) {
                    lottieView.visibility = View.VISIBLE
                    textAiAssist.visibility = View.GONE
                } else {
                    lottieView.visibility = View.GONE
                    textAiAssist.visibility = View.VISIBLE
                }
            }

        }
    }

    // Hàm cập nhật phần tử cuối cùng
    @SuppressLint("NotifyDataSetChanged")
    fun updateLastMessage(newMessage: Message) {
        if (messages.isNotEmpty()) {
            messages[messages.size - 1] = newMessage
            notifyItemChanged(messages.size - 1)
        }
    }

    // Thêm một tin nhắn mới
    fun addThinkingMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
