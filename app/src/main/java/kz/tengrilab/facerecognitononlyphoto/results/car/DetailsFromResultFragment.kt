package kz.tengrilab.facerecognitononlyphoto.results.car

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import io.github.muddz.styleabletoast.StyleableToast
import kz.tengrilab.facerecognitononlyphoto.ApiClient
import kz.tengrilab.facerecognitononlyphoto.R
import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.carnumber.GetCarDetailsInterface
import kz.tengrilab.facerecognitononlyphoto.data.CarDetail
import kz.tengrilab.facerecognitononlyphoto.data.CarDetailById
import kz.tengrilab.facerecognitononlyphoto.databinding.FragmentDetailsFromResultBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsFromResultFragment : Fragment() {

    private var _binding: FragmentDetailsFromResultBinding? = null
    private val binding get() = _binding!!
    private val args: DetailsFromResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsFromResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchCarNumber()
    }

    private fun searchCarNumber() {
        val retrofit = ApiClient.getRetrofitClient()
        val clientInterface = retrofit.create(GetCarDetailsInterface::class.java)
        val call = clientInterface.sendCarId(Variables.headers2 + loadToken(), args.carId)
        call.enqueue(object : Callback<CarDetailById> {
            override fun onResponse(call: Call<CarDetailById>, response: Response<CarDetailById>) {
                binding.progressBar.visibility = View.GONE
                Log.d("Test", response.body().toString())
                if (response.code() == 200) {
                    val result = response.body()!!.data
                    val obj = JSONObject(result.response)
                    val jsonFiz = obj.getString("fiz")

                    binding.tableCar.visibility = View.VISIBLE
                    if (obj.getString("car_number_type") == "iur") {
                        binding.tableIur.visibility = View.VISIBLE
                        binding.textIur.text = obj.getString("iur")
                    }
                    if (obj.getString("car_number_type") == "FIZ") {
                        if (jsonFiz != "null") {
                            val fiz = JSONObject(jsonFiz)
                            binding.textSurname.text = fiz.getString("lastname")
                            binding.textFirstName.text = fiz.getString("firstname")
                            binding.textSecondName.text = fiz.getString("secondname")
                            binding.textIn.text = fiz.getString("gr_code")
                            binding.textUd.text = fiz.getString("ud_code")
                            val url = Variables.url + Variables.port + "/files/udgrphotos/" + fiz.getString("ud_code").toLong().toString() + ".ldr"
                            Picasso.get().load(url).into(binding.imagePerson)
                            binding.tableCitizen.visibility = View.VISIBLE
                            binding.imagePerson.visibility = View.VISIBLE
                            binding.textLicense.text = obj.getString("vu_serial")
                            binding.textDateLicense.text = obj.getString("vu_end")
                        }
                    }
                    binding.textGrnz.text = obj.getString("car_number")
                    binding.textCarModel.text = obj.getString("car_model")
                    binding.textCarYear.text = obj.getString("car_year")
                    binding.textVin.text = obj.getString("vin")
                    binding.textTechPass.text = obj.getString("teh_passport")
                    binding.textDateTechPass.text = obj.getString("teh_passport_date")
                } else {
                    StyleableToast.makeText(requireContext(), "Автомобиль отсутствует", Toast.LENGTH_LONG, R.style.mytoast2).show()
                }
            }

            override fun onFailure(call: Call<CarDetailById>, t: Throwable) {
                Log.d("Test", "Error")
                StyleableToast.makeText(requireContext(), "Проблемы с сетью", Toast.LENGTH_LONG, R.style.mytoast2).show()
            }

        })
    }

    private fun loadToken() : String? {
        val sharedPreferences = activity?.getSharedPreferences(Variables.sharedPrefLogin, Context.MODE_PRIVATE)!!
        return sharedPreferences.getString(Variables.sharedPrefToken, null)
    }
}