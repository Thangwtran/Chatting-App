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

class ChatAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_BOT = 1
    }

    private val messages = mutableListOf<Message>()
    private var isThinking = false

    @SuppressLint("NotifyDataSetChanged")
    fun updateThinking(isThinking: Boolean) {
        this.isThinking = isThinking
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<Message>) {
        messages.clear()
        messages.addAll(newList)
        notifyDataSetChanged()
    }

    private fun getLastBotPosition(): Int {
        for (i in messages.size - 1 downTo 0) {
            if (!messages[i].isUser) return i
        }
        return -1
    }


    // Hàm tạo message thinking khi bot đang typing
    fun startThinking() {
        val thinkingMessage = Message(
            id = System.currentTimeMillis(),
            text = "",         // chưa có text
            isUser = false,
            isTyping = true    // dùng để hiển thị "..." và Lottie animation
        )
        messages.add(thinkingMessage)
        notifyItemInserted(messages.size - 1)
    }

    // Hàm update text cho message thinking cuối cùng
    fun stopThinking(finalText: String) {
        val lastBotPos = messages.indexOfLast { !it.isUser && it.isTyping }
        if (lastBotPos != -1) {
            messages[lastBotPos].text = finalText
            messages[lastBotPos].isTyping = false
            notifyItemChanged(lastBotPos)
        }
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
                val isLastBot = position == messages.indexOfLast { !it.isUser }
                holder.bind(msg, isLastBot, msg.isTyping)
            }
        }
    }

    override fun getItemCount() = messages.size

    class UserVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txt: TextView = itemView.findViewById(R.id.textMessage)
        fun bind(m: Message) {
            txt.text = if (m.isTyping) "..." else m.text
        }
    }

    class BotVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txt: TextView = itemView.findViewById(R.id.textMessage)
        fun bind(m: Message, isLastBot: Boolean, isThinking: Boolean) {
            txt.text = if (m.isTyping) "..." else m.text

            val lottieView = itemView.findViewById<LottieAnimationView>(R.id.ai_loading)
            val textView = itemView.findViewById<TextView>(R.id.text_ai_assist)

            if (isLastBot && isThinking) {
                lottieView.visibility = View.VISIBLE
                textView.visibility = View.GONE
            } else {
                lottieView.visibility = View.GONE
                textView.visibility = View.VISIBLE
            }
        }



    }
}
