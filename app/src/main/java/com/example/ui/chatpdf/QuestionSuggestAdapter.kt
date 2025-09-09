package com.example.ui.chatpdf

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatpdf.databinding.ItemTextSuggestBinding

class QuestionSuggestAdapter: RecyclerView.Adapter<QuestionSuggestAdapter.ViewHolder>() {

    private val questionSuggests = mutableListOf<QuestionSuggest>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<QuestionSuggest>) {
        questionSuggests.clear()
        questionSuggests.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemTextSuggestBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(questionSuggests[position])
    }

    override fun getItemCount(): Int {
        return questionSuggests.size
    }

    class ViewHolder(
        private val binding: ItemTextSuggestBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(questionSuggest: QuestionSuggest){
            binding.textQuestionSuggest.text = questionSuggest.text
        }
    }
}

data class QuestionSuggest(
    val text: String
)