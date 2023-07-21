package com.example.biketech.models

data class User(val uid: String = "",
                val displayName: String? = "",
                val imageUrl: String = "",
                val email: String = "",
                val Documents: ArrayList<String> = ArrayList())