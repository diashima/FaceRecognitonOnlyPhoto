package kz.tengrilab.facerecognitononlyphoto.results.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.data.Man
import kz.tengrilab.facerecognitononlyphoto.databinding.ItemDetailBinding

class DetailsAdapter(private val items: List<Man>) : RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder>() {

    inner class DetailsViewHolder(val binding: ItemDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(man: Man){
            with(itemView){
                binding.textViewSurname.text = man.surname
                binding.textViewFirstName.text = man.firstName
                binding.textViewSecondName.text = man.secondName
                binding.textViewDistance.text = man.distance.toString()
                binding.textViewIIN.text = man.iin
                binding.textViewUdNumber.text = man.udNumber

                val url = Variables.url + Variables.port + "/files/udgrphotos/" + man.udNumber + ".jpg"
                Picasso.get().load(url).into(binding.cropImageView)
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
}