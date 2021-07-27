package kz.tengrilab.facerecognitononlyphoto.gallery

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.tengrilab.facerecognitononlyphoto.databinding.ItemGalleryBinding
import java.io.File

class GalleryAdapter(private val items: ArrayList<String>) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>(){

    inner class GalleryViewHolder(private val binding: ItemGalleryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: String) {
            with (itemView){
                Log.d("Test", photo)
                val file = File(photo)
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                binding.imageView.setImageBitmap(bitmap)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}