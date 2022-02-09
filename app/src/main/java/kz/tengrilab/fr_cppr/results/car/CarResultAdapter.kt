package kz.tengrilab.fr_cppr.results.car

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.tengrilab.fr_cppr.data.ResultCar
import kz.tengrilab.fr_cppr.databinding.ItemResultCarBinding
import org.json.JSONObject

class CarResultAdapter(private val items: List<ResultCar.ResultCarDetails>, private val listener: OnItemClickListener) : RecyclerView.Adapter<CarResultAdapter.ResultCarViewHolder>(){

    inner class ResultCarViewHolder(private val binding: ItemResultCarBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onResultCLick(position)
            }
        }
        @SuppressLint("SetTextI18n")
        fun bind(car: ResultCar.ResultCarDetails) {
            with (itemView){
                val obj = JSONObject(car.response)
                binding.textRequestTime.text = car.timestamp
                binding.textNumber.text = car.car_number
                binding.textCarModel.text = obj.getString("car_model")
                binding.textCarYear.text = obj.getString("car_year")
                binding.textVin.text = obj.getString("vin")
                binding.textTechPass.text = "Тех. паспорт: " + obj.getString("teh_passport") + ", от ${obj.getString("teh_passport_date")}"
                //binding.textCarModel.text = car.response.car_model
                //binding.textCarYear.text = car.response.car_year
                //binding.textVin.text = "VIN-код: " + car.response.vin
                //binding.textTechPass.text = "Тех. паспорт: " + car.response.teh_passport + ", от ${car.response.teh_passport_date}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultCarViewHolder {
        val binding = ItemResultCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultCarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultCarViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    interface  OnItemClickListener {
        fun onResultCLick(position: Int)
    }
}