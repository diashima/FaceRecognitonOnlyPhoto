package kz.tengrilab.facerecognitononlyphoto.results.details

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import kz.tengrilab.facerecognitononlyphoto.ApiClient
import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.carnumber.CarAdapter
import kz.tengrilab.facerecognitononlyphoto.carnumber.GetCarDetailsInterface
import kz.tengrilab.facerecognitononlyphoto.data.CarDetailByIin
import kz.tengrilab.facerecognitononlyphoto.databinding.FragmentCardetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CarDetailsFragment : Fragment() {

    private var _binding: FragmentCardetailsBinding? = null
    private val binding get() = _binding!!
    private val args: CarDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.VISIBLE
        Log.d("Test", args.udNumber)
        findUd()
    }

    private fun findUd() {
        val retrofit = ApiClient.getRetrofitClient()
        val clientInterface = retrofit.create(GetCarDetailsInterface::class.java)

        val call = clientInterface.sendPersonIin(Variables.headers2 + loadToken(), args.udNumber)
        call.enqueue(object : Callback<CarDetailByIin> {
            override fun onResponse(call: Call<CarDetailByIin>, response: Response<CarDetailByIin>) {
                Log.d("Test", response.code().toString())
                if (response.code() == 200) {
                    binding.progressBar.visibility = View.GONE
                    if (response.body() != null) {
                        val carList = response.body()!!.data.car_info
                        Log.d("Test", carList.toString())
                        val result = response.body()!!
                        binding.tableCitizen.visibility = View.VISIBLE
                        binding.imagePerson.visibility = View.VISIBLE
                        binding.textSurname.text = result.data.lastname
                        binding.textFirstName.text = result.data.firstname
                        binding.textSecondName.text = result.data.secondname
                        binding.textIn.text = result.data.gr_code
                        binding.textUd.text = result.data.ud_code
                        binding.textLicense.text = result.data.car_info[0].vu_serial
                        binding.textDateLicense.text = result.data.car_info[0].vu_serial
                        //val url = Variables.imageUrl + Variables.port + "/files/udgrphotos/" + result.data.ud_code + ".ldr"
                        val url = Variables.url + Variables.port + "/files/udgrphotos/" + result.data.ud_code.toLong().toString() + ".ldr"
                        Picasso.get().load(url).into(binding.imagePerson)
                        binding.recycler.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = CarAdapter(carList)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CarDetailByIin>, t: Throwable) {
                Log.d("Test", "Error")
            }

        })
    }

    private fun loadToken() : String? {
        val sharedPreferences = activity?.getSharedPreferences(Variables.sharedPrefLogin, Context.MODE_PRIVATE)!!
        return sharedPreferences.getString(Variables.sharedPrefToken, null)
    }
}