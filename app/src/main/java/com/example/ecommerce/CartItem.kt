package com.example.ecommerce

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val name: String,
    val price: String,
    val imageUri: String
) : Parcelable
