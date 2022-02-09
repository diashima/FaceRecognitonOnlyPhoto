package kz.tengrilab.fr_cppr.results.details

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kz.tengrilab.fr_cppr.Variables
import kz.tengrilab.fr_cppr.data.Man
import kz.tengrilab.fr_cppr.databinding.ItemDetailBinding
import java.lang.Exception

class DetailsAdapter(private val items: List<Man>, private val listener: OnItemClickListener) : RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder>() {

    inner class DetailsViewHolder(val binding: ItemDetailBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.layoutCard.setOnClickListener(this)
        }
        @SuppressLint("SetTextI18n")
        fun bind(man: Man){
            with(itemView){
                val name = man.surname + " " + man.firstName + " " + man.secondName
                binding.textName.text = name
                binding.textDistance.text = man.distance.toString() + "%"
                binding.textIin.text = "ИИН: ${man.iin}"
                //binding.textUdNumber.text = man.udNumber

                if (man.isHaveCar) {
                    binding.imageCar.visibility = View.VISIBLE
                    binding.layoutCard.visibility = View.VISIBLE
                }

                if (!man.isHaveCar) {
                    binding.imageCar.visibility = View.GONE
                    binding.layoutCard.visibility = View.GONE
                }


                try {
                    val url = Variables.url + Variables.port + "/files/udgrphotos/" + man.udNumber.toLong() + ".ldr"
                    //val url = Variables.url + Variables.port + "/files/udgrphotos/" + man.uniqueId.toLong() + ".ldr"
                    Picasso.get().load(url).into(binding.cropImageView)
                }
                catch (e: Exception) {
                    Log.d("Test", e.message!!)
                }
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