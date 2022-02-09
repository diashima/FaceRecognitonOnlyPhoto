package kz.tengrilab.fr_cppr.carnumber

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kz.tengrilab.fr_cppr.data.CarDetailByIin
import kz.tengrilab.fr_cppr.databinding.ItemCarBinding

class CarAdapter(private val items: List<CarDetailByIin.Data.CarInfo>) : RecyclerView.Adapter<CarAdapter.CarViewHolder>(){

    inner class CarViewHolder(private val binding: ItemCarBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(car: CarDetailByIin.Data.CarInfo) {
            with (itemView){
                binding.textNumber.text = car.car_number
                binding.textCarModel.text = car.car_model
                binding.textCarYear.text = car.car_year
                binding.textVin.text = "VIN-код: " + car.vin
                binding.textTechPass.text = "Тех. паспорт: " + car.teh_passport + ", от ${car.teh_passport_date}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(items.get(position))
    }

    override fun getItemCount(): Int = items.size
}