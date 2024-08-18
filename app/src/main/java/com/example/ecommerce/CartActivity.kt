package com.example.ecommerce

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var buyNowButton: Button
    private lateinit var adapter: CartAdapter
    private var cartItems: MutableList<CartItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.cart_recycler_view)
        buyNowButton = findViewById(R.id.buy_now_button)

        adapter = CartAdapter(cartItems) { cartItem ->
            removeItemFromCart(cartItem)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buyNowButton.setOnClickListener {
            // Handle buy now action, e.g., navigate to success page
            startActivity(Intent(this, SuccessActivity::class.java))
        }

        loadCartItems()
    }

    private fun loadCartItems() {
        try {
            val sharedPreferences = getSharedPreferences("cart", MODE_PRIVATE)
            val gson = Gson()
            val cartItemsJson = sharedPreferences.getString("cartItems", null)
            Log.d("CartActivity", "Loaded Cart Items JSON: $cartItemsJson")

            val cartItemsType = object : TypeToken<MutableList<CartItem>>() {}.type
            cartItems.clear()
            if (cartItemsJson != null) {
                val items: MutableList<CartItem> = gson.fromJson(cartItemsJson, cartItemsType) ?: mutableListOf()
                cartItems.addAll(items)
            } else {
                Log.d("CartActivity", "No Cart Items Found")
            }

            adapter.notifyDataSetChanged()
            Log.d("CartActivity", "Cart Items After Load: $cartItems")
        } catch (e: Exception) {
            Log.e("CartActivity", "Error loading cart items", e)
            Toast.makeText(this, "Failed to load cart items", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeItemFromCart(cartItem: CartItem) {
        try {
            cartItems.remove(cartItem)
            adapter.notifyDataSetChanged()

            val sharedPreferences = getSharedPreferences("cart", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val updatedCartItemsJson = gson.toJson(cartItems)
            editor.putString("cartItems", updatedCartItemsJson)
            editor.apply()

            Toast.makeText(this, "${cartItem.name} removed from cart", Toast.LENGTH_SHORT).show()
            Log.d("CartActivity", "Removed Cart Item: $cartItem")
        } catch (e: Exception) {
            Log.e("CartActivity", "Error removing item from cart", e)
            Toast.makeText(this, "Failed to remove item from cart", Toast.LENGTH_SHORT).show()
        }
    }
}
