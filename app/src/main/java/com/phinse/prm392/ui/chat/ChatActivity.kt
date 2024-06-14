package com.phinse.prm392.ui.chat

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.heartsteel.heartory.ui.chat.inside.ChatInsideAdapter
import com.phinse.prm392.R
import com.phinse.prm392.databinding.ActivityChatBinding
import com.phinse.prm392.service.model.Chat
import com.phinse.prm392.service.model.User
import kotlin.math.roundToInt

class ChatActivity : AppCompatActivity() {

    private val TAG: String = ChatActivity::class.java.simpleName
    private val chatsLiveData = MutableLiveData<MutableList<Chat>>()
    private val firebaseUser by lazy {
        FirebaseAuth.getInstance().currentUser
    }
    private val firebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var otherUser: User

    private val binding: ActivityChatBinding by lazy {
        ActivityChatBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        otherUser = Gson().fromJson(intent.getStringExtra("otherUser"), User::class.java)

        binding.tvChatTitle.text = otherUser.name

        val rvChat = binding.rvChat
        val chatAdapter = ChatInsideAdapter()
        rvChat.adapter = chatAdapter
        rvChat.layoutManager = LinearLayoutManager(this)

        binding.ivSend.setOnClickListener {
            val message = binding.etInput.text.toString()
            if (message.isNotEmpty()) {
                val chat = Chat(
                    message,
                    firebaseUser!!.uid,
                    otherUser.uid,
                    System.currentTimeMillis(),
                )
                firebaseFirestore.collection("chats").add(chat)
                binding.etInput.text.clear()
            }
        }

        firebaseFirestore.collection("chats").addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            val chats = mutableListOf<Chat>()
            value?.documents?.forEach {
                val chat = it.toObject(Chat::class.java)
                if (chat != null) {
                    chats.add(chat)
                }
            }

            chats.sortBy { it.timestamp }

            chatsLiveData.value = chats
        }

        chatsLiveData.observe(this) {
            chatAdapter.submitList(it)
        }

        autoScrollToBottom()

    }


    private fun autoScrollToBottom() {
        // Scroll to bottom when keyboard is shown or hidden
        val activityRootView = findViewById<View>(R.id.rvChat)
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val heightDiff = activityRootView.rootView.height - activityRootView.height
                if (heightDiff > dpToPx(100) || heightDiff < -dpToPx(100)) {
                    if (chatsLiveData.value?.size != null && chatsLiveData.value!!.isNotEmpty())
                        binding.rvChat.smoothScrollToPosition(chatsLiveData.value!!.size - 1)
                }
            }

            private fun dpToPx(dp: Int): Int {
                val density = resources.displayMetrics.density
                return (dp * density).roundToInt()
            }
        })
    }

    // Hide keyboard when touch outside of EditText
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (binding.etInput.isFocused) {
                val outRect = Rect()
                binding.etInput.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    binding.etInput.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etInput.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}