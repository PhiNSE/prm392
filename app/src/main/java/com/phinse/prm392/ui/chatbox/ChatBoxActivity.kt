package com.phinse.prm392.ui.chatbox

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.phinse.prm392.databinding.ActivityChatBoxBinding
import com.phinse.prm392.service.model.User
import com.phinse.prm392.ui.chat.ChatActivity

class ChatBoxActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityChatBoxBinding.inflate(layoutInflater)
    }
    private val firebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val firebaseUser by lazy {
        FirebaseAuth.getInstance().currentUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.llChat.setOnClickListener {
            firebaseFirestore.collection("users").document("jzgFAVUNvVaQb0KFBAU5ryxb39E3").get()
                .addOnSuccessListener { user ->
                    val f = User()
                    f.uid = user.getString("uid")
                    f.name = user.getString("name")
                    f.email = user.getString("email")
                    f.protoUrl = user.getString("protoUrl")
                    Intent(this, ChatActivity::class.java).apply {
                        putExtra("otherUser", Gson().toJson(f))
                        startActivity(this)
                    }
                }

        }
    }
}