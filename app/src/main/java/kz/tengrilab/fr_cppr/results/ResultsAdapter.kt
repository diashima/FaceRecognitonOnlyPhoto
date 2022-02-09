package kz.tengrilab.fr_cppr.results

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kz.tengrilab.fr_cppr.Variables
import kz.tengrilab.fr_cppr.data.Face
import kz.tengrilab.fr_cppr.databinding.ItemResultBinding

class ResultsAdapter(private val items: List<Face.Result>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder>() {

    inner class ResultsViewHolder(val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemCLick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        with(holder){
            with(items[position]) {
                binding.textDateTime.text = this.timestamp
                //Picasso.get().load(this.photoPath.replace("10.77.1.62", "10.77.6.62")).into(binding.imageViewCard)
                Picasso.get().load(Variables.url + Variables.port + this.photoPath).into(binding.imageViewCard)
            }
        }
    }

    override fun getItemCount() = items.size

    interface  OnItemClickListener {
        fun onItemCLick(position: Int)
    }
}