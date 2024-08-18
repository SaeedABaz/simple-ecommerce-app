// ProductAdapter.kt
package com.example.ecommerce

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.network.ProductResponse

class ProductAdapter(
    private var products: List<ProductResponse>,
    private val onProductClick: (ProductResponse) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productImage: ImageView = itemView.findViewById(R.id.product_image)

        fun bind(product: ProductResponse) {
            productName.text = product.name
            Glide.with(itemView.context)
                .load(product.image_url)  // Ensure this URL is correct and accessible
                .into(productImage)
            itemView.setOnClickListener { onProductClick(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<ProductResponse>) {
        (products as MutableList).clear()
        (products as MutableList<ProductResponse>).addAll(newProducts)
        notifyDataSetChanged()
    }
}
