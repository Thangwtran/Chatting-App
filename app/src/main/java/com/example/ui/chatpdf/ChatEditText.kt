package com.example.ui.chatpdf

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.R
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.core.view.ViewCompat

class ChatEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        // Lắng nghe content có MIME type là text/*
        ViewCompat.setOnReceiveContentListener(this, arrayOf("text/*")) { _, payload ->
            val (content, remaining) =
                payload.partition { it.text != null }

            if (content != null) {
                val clip = content.clip
                if (clip.itemCount > 0) {
                    // Lấy text từ clip
                    val pastedText = clip.getItemAt(0).text?.toString()
                    if (!pastedText.isNullOrEmpty()) {
                        // Chèn text vào EditText
                        this.append(pastedText)
                    }
                }
            }
            remaining
        }
    }
}
