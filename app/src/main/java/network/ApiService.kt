package com.example.ecommerce.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class ProductRequest(
    val name: String,
    val description: String,
    val price: String,
    val image_url: String
)

interface ApiService {

    @Multipart
    @POST("/api/upload")
    fun uploadImage(@Part file: MultipartBody.Part): Call<Map<String, String>>

    @POST("/api/products")
    fun addProduct(@Body product: ProductRequest): Call<Void>

    @GET("/api/products")
    fun getProducts(): Call<List<ProductResponse>>
}
