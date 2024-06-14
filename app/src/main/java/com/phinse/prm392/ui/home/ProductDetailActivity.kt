package com.phinse.prm392.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.phinse.prm392.databinding.ActivityProductDetailBinding
import com.phinse.prm392.service.model.Product
import com.phinse.prm392.service.model.User
import com.phinse.prm392.ui.chat.ChatActivity

class ProductDetailActivity : AppCompatActivity() {

    private val TAG: String = ProductDetailActivity::class.java.simpleName
    private val binding: ActivityProductDetailBinding by lazy {
        ActivityProductDetailBinding.inflate(layoutInflater)
    }

    private val firebaseFirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        Gson().fromJson(intent.getStringExtra("product"), Product::class.java).let {
            Log.d(TAG, it.toString())
            binding.tvProductName.text = it.name
            Glide.with(this).load(it.image).into(binding.ivProduct)
            binding.tvProductPrice.text = it.price.toString()
            binding.tvProductDescription.text = it.description

            firebaseFirestore.collection("users").document(it.shopId).get()
                .addOnSuccessListener { user ->
                    binding.tvProductSeller.text = user.getString("name")
                    val mUser = User();
                    mUser.uid = user.getString("uid")
                    mUser.name = user.getString("name")
                    mUser.email = user.getString("email")
                    mUser.protoUrl = user.getString("protoUrl")

                    binding.ivChat.setOnClickListener {
                        Intent(this, ChatActivity::class.java).apply {
                            putExtra("otherUser", Gson().toJson(mUser))
                            startActivity(this)
                        }
                    }
                }


        }


    }
}