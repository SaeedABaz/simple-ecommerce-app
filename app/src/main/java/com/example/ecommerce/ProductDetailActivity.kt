package com.example.ecommerce

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ecommerce.network.ProductResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var productDescription: TextView
    private lateinit var productImage: ImageView
    private lateinit var addToCartButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Initialize views
        productName = findViewById(R.id.product_name)
        productPrice = findViewById(R.id.product_price)
        productDescription = findViewById(R.id.product_description)
        productImage = findViewById(R.id.product_image)
        addToCartButton = findViewById(R.id.add_to_cart_button)

        // Retrieve the product from the intent
        val product = intent.getParcelableExtra<ProductResponse>("product")

        // Populate the views with the product data
        product?.let {
            productName.text = it.name
            productPrice.text = it.price.toString()
            productDescription.text = it.description
            Glide.with(this).load(it.image_url).into(productImage)

            addToCartButton.setOnClickListener {
                addToCart(product)
            }
        } ?: run {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if the product is not available
        }
    }

    private fun addToCart(product: ProductResponse) {
        // Get shared preferences
        val sharedPreferences = getSharedPreferences("cart", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Get existing cart items
        val gson = Gson()
        val cartItemsJson = sharedPreferences.getString("cartItems", null)
        val cartItemsType = object : TypeToken<MutableList<CartItem>>() {}.type
        val cartItems: MutableList<CartItem> = gson.fromJson(cartItemsJson, cartItemsType) ?: mutableListOf()

        // Add new item to the cart
        val cartItem = CartItem(product.name, product.price.toString(), product.image_url)
        cartItems.add(cartItem)

        // Save updated cart items
        val updatedCartItemsJson = gson.toJson(cartItems)
        editor.putString("cartItems", updatedCartItemsJson)
        editor.apply()

        Toast.makeText(this, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
    }
}
