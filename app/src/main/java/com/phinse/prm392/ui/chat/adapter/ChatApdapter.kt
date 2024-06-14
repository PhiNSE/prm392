package com.heartsteel.heartory.ui.chat.inside

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.phinse.prm392.databinding.CustomLeftChatBinding
import com.phinse.prm392.databinding.CustomRightChatBinding
import com.phinse.prm392.service.model.Chat
import com.phinse.prm392.ui.chat.adapter.BaseAdapter

class ChatInsideAdapter : BaseAdapter<Chat, ChatInsideAdapter.ChatInsideViewHolder>() {

    private val LEFT = 0
    private val RIGHT = 1

    private val firebaseUser by lazy { FirebaseAuth.getInstance().currentUser }
    private val firebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    inner class ChatInsideViewHolder(binding: ViewBinding) :
        BaseItemViewHolder<Chat, ViewBinding>(binding) {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun bind(item: Chat) {
            if (binding is CustomLeftChatBinding) {
                firebaseFirestore.collection("users").document(item.sender).get()
                    .addOnSuccessListener { user ->
                        binding.tvOtherUserName.text = user.getString("name")
                    }
                binding.tvOtherUserText.text = item.message
            } else if (binding is CustomRightChatBinding) {
                binding.tvUserText.text = item.message
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatInsideViewHolder {
        return when (viewType) {
            LEFT -> {
                ChatInsideViewHolder(
                    CustomLeftChatBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            RIGHT -> {
                ChatInsideViewHolder(
                    CustomRightChatBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                ChatInsideViewHolder(
                    CustomLeftChatBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun differCallBack(): DiffUtil.ItemCallback<Chat> {
        return object : DiffUtil.ItemCallback<Chat>() {
            override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
                return oldItem.message == newItem.message
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        val chat = differ.currentList[position]
        return if (firebaseUser?.uid == chat.sender) {
            RIGHT
        } else {
            LEFT
        }
    }


}

