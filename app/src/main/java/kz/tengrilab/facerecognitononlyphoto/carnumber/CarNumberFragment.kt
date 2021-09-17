package kz.tengrilab.facerecognitononlyphoto.carnumber

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import kz.tengrilab.facerecognitononlyphoto.ApiClient
import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.data.CarDetail
import kz.tengrilab.facerecognitononlyphoto.databinding.FragmentCarnumberBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CarNumberFragment : Fragment() {
    private var _binding: FragmentCarnumberBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarnumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSearch.setOnClickListener {
            searchCarNumber()
        }

        binding.buttonClear.setOnClickListener {
            binding.editCarInfo.text.clear()
            binding.carInputLayout.visibility = View.VISIBLE
            binding.tableCar.visibility = View.GONE
            binding.tableCitizen.visibility = View.GONE
            binding.buttonClear.visibility = View.GONE
            binding.imagePerson.visibility = View.GONE
        }
    }

    private fun searchCarNumber() {
        val retrofit = ApiClient.getRetrofitClient()
        val clientInterface = retrofit.create(GetCarDetailsInterface::class.java)
        binding.carInputLayout.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        val call = clientInterface.sendCarNumber(Variables.headers2 + loadToken(), binding.editCarInfo.text.toString())
        call.enqueue(object : Callback<CarDetail> {
            override fun onResponse(call: Call<CarDetail>, response: Response<CarDetail>) {
                Log.d("Test", response.body().toString())
                if (response.code() == 200) {
                    val result = response.body()!!.data
                    binding.progressBar.visibility = View.GONE
                    binding.tableCar.visibility = View.VISIBLE
                    binding.buttonClear.visibility = View.VISIBLE
                    if (result.fiz != null) {
                        binding.tableCitizen.visibility = View.VISIBLE
                        binding.imagePerson.visibility = View.VISIBLE
                        binding.textSurname.text = result.fiz.lastname
                        binding.textFirstName.text = result.fiz.firstname
                        binding.textSecondName.text = result.fiz.secondname
                        binding.textIn.text = result.fiz.gr_code
                        binding.textUd.text = result.fiz.ud_code
                        binding.textLicense.text = result.vu_serial
                        binding.textDateLicense.text = result.vu_end
                        //val url = Variables.imageUrl + Variables.port + "/files/udgrphotos/" + result.fiz.ud_code + ".ldr"
                        val url = Variables.url + Variables.port + "/files/udgrphotos/" + result.fiz.ud_code.toLong().toString() + ".ldr"
                        Picasso.get().load(url).into(binding.imagePerson)
                        //binding.textLicense.text
                        //binding.textDateLicense.text
                    }
                    binding.textGrnz.text = result.car_number
                    binding.textCarModel.text = result.car_model
                    binding.textCarYear.text = result.car_year
                    binding.textVin.text = result.vin
                    binding.textTechPass.text = result.teh_passport
                    binding.textDateTechPass.text = result.teh_passport_date
                }
            }

            override fun onFailure(call: Call<CarDetail>, t: Throwable) {
                Log.d("Test", "Error")
                binding.carInputLayout.visibility = View.VISIBLE
            }

        })
    }

    private fun loadToken() : String? {
        val sharedPreferences = activity?.getSharedPreferences(Variables.sharedPrefLogin, Context.MODE_PRIVATE)!!
        return sharedPreferences.getString(Variables.sharedPrefToken, null)
    }
}