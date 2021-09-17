package kz.tengrilab.facerecognitononlyphoto.results.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.data.Man
import kz.tengrilab.facerecognitononlyphoto.databinding.ItemDetailBinding
import kz.tengrilab.facerecognitononlyphoto.results.ResultsAdapter

class DetailsAdapter(private val items: List<Man>, private val listener: OnItemClickListener) : RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder>() {

    inner class DetailsViewHolder(val binding: ItemDetailBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.btnShowCars.setOnClickListener(this)
        }
        fun bind(man: Man){
            with(itemView){
                val name = man.surname + " " + man.firstName + " " + man.secondName
                binding.textName.text = name
                binding.textDistance.text = man.distance.toString()
                binding.textIin.text = man.iin
                binding.textUdNumber.text = man.udNumber

                if (man.isHaveCar) {
                    binding.textNoCar.visibility = View.GONE
                    binding.btnShowCars.visibility = View.VISIBLE
                }

                if (!man.isHaveCar) {
                    binding.textNoCar.visibility = View.VISIBLE
                    binding.btnShowCars.visibility = View.GONE
                }

                //val url = Variables.imageUrl + Variables.port + "/files/udgrphotos/" + man.udNumber + ".ldr"
                val url = Variables.url + Variables.port + "/files/udgrphotos/" + man.udNumber.toLong().toString() + ".ldr"
                Picasso.get().load(url).into(binding.cropImageView)
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemCLick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        val binding = ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    interface  OnItemClickListener {
        fun onItemCLick(position: Int)
    }
}