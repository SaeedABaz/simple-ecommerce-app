package com.example.ecommerce

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ecommerce.network.ApiService
import com.example.ecommerce.network.ProductRequest
import com.example.ecommerce.network.RetrofitClient
import com.example.ecommerce.utils.FileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddItemActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var uploadImageView: ImageView
    private lateinit var uploadButton: Button
    private lateinit var addButton: Button
    private var selectedImageUri: Uri? = null

    private val PICK_IMAGE_REQUEST = 1
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        nameEditText = findViewById(R.id.editTextName)
        descriptionEditText = findViewById(R.id.editTextDescription)
        priceEditText = findViewById(R.id.editTextPrice)
        uploadImageView = findViewById(R.id.imageViewUpload)
        uploadButton = findViewById(R.id.buttonUploadImage)
        addButton = findViewById(R.id.buttonAdd)

        uploadButton.setOnClickListener {
            openGallery()
        }

        addButton.setOnClickListener {
            uploadImageAndAddProduct()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            Glide.with(this).load(selectedImageUri).into(uploadImageView)
        }
    }

    private fun uploadImageAndAddProduct() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val file = File(FileUtils.getPath(this, selectedImageUri!!))
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        apiService.uploadImage(body).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.get("url")
                    if (imageUrl != null) {
                        Log.d("AddItemActivity", "Uploaded Image URL: $imageUrl")  // Add this line for debugging
                        addProduct(imageUrl)
                    } else {
                        Toast.makeText(this@AddItemActivity, "Failed to get image URL", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AddItemActivity, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(this@AddItemActivity, "Image upload error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addProduct(imageUrl: String) {
        val name = nameEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val price = priceEditText.text.toString().trim().toDoubleOrNull()

        if (name.isNotEmpty() && description.isNotEmpty() && price != null) {
            val productRequest = ProductRequest(name, description, price.toString(), imageUrl)

            apiService.addProduct(productRequest).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddItemActivity, "Product added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddItemActivity, "Failed to add product", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AddItemActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }
}
