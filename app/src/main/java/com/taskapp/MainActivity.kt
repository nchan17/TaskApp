package com.taskapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.FirebaseApp
import com.taskapp.databinding.ActivityMainBinding
import com.taskapp.presentation.RegisterUser

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.registerTextView.setOnClickListener(this)
        setContentView(binding.root)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.register_textView -> {
                    startActivity(Intent(this, RegisterUser::class.java))
                }
            }
        }
    }
}