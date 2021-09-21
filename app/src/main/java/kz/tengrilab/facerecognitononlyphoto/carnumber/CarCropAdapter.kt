package kz.tengrilab.facerecognitononlyphoto.carnumber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.data.CarCrop
import kz.tengrilab.facerecognitononlyphoto.databinding.ItemCarnumberBinding
import kz.tengrilab.facerecognitononlyphoto.databinding.ItemGalleryBinding
import kz.tengrilab.facerecognitononlyphoto.results.ResultsAdapter


class CarCropAdapter(private val items: ArrayList<CarCrop>, private val listener: CarCropAdapter.OnItemClickListener) : RecyclerView.Adapter<CarCropAdapter.CarCropViewHolder>(){
    inner class CarCropViewHolder(private val binding: ItemCarnumberBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemCLick(position)
            }
        }
        fun bind(carCrop: CarCrop) {
            with (itemView){
                Picasso.get().load(Variables.url + Variables.port + carCrop.url).into(binding.imageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarCropViewHolder {
        val binding = ItemCarnumberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarCropViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarCropViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    interface  OnItemClickListener {
        fun onItemCLick(position: Int)
    }
}