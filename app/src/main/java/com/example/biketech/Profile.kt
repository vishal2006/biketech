package com.example.biketech
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.biketech.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.content.ContentResolver

import android.graphics.BitmapFactory

import android.graphics.Bitmap




class Profile : AppCompatActivity() {

    lateinit var firebaseUser : FirebaseUser
    lateinit var auth : FirebaseAuth
    lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!

        binding.namevalue.text = firebaseUser.displayName
        binding.emailvalue.text = firebaseUser.email

        binding.bikenumbervalue.setText(binding.bikenumbervalue.text)

    }


}

