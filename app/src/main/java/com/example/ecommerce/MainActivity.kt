package com.example.ecommerce

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.network.ApiService
import com.example.ecommerce.network.ProductResponse
import com.example.ecommerce.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var navBar: BottomNavigationView
    private lateinit var adapter: ProductAdapter
    private var products: MutableList<ProductResponse> = mutableListOf()
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.products_recycler_view)
        searchView = findViewById(R.id.search_view)
        navBar = findViewById(R.id.nav_bar)

        adapter = ProductAdapter(products) { product ->
            openProductDetail(product)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadProducts()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterProducts(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText)
                return true
            }
        })

        navBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                R.id.nav_add_item -> {
                    startActivity(Intent(this, AddItemActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadProducts() {
        apiService.getProducts().enqueue(object : Callback<List<ProductResponse>> {
            override fun onResponse(call: Call<List<ProductResponse>>, response: Response<List<ProductResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    products.clear()
                    response.body()?.let { products.addAll(it) }
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("MainActivity", "Response was not successful")
                }
            }

            override fun onFailure(call: Call<List<ProductResponse>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to load products", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "Failed to load products", t)
            }
        })
    }

    private fun filterProducts(query: String?) {
        val filteredProducts = if (query.isNullOrEmpty()) {
            products
        } else {
            products.filter { it.name.contains(query, ignoreCase = true) }
        }
        adapter.updateProducts(filteredProducts)
    }

    private fun openProductDetail(product: ProductResponse) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }
}

private fun Parcelable.putExtra(s: String, product: ProductResponse) {
    TODO("Not yet implemented")
}
