package com.example.ecommerce

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: String,
    val image_url: String
) : Parcelable
