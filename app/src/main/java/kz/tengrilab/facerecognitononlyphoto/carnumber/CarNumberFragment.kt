package kz.tengrilab.facerecognitononlyphoto.carnumber

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
    }

    private fun searchCarNumber() {
        val retrofit = ApiClient.getRetrofitClient(requireContext())
        val clientInterface = retrofit.create(GetCarDetailsInterface::class.java)

        val call = clientInterface.uploadImage(Variables.headers2 + loadToken(), binding.editCarInfo.text.toString())
        call.enqueue(object : Callback<CarDetail> {
            override fun onResponse(call: Call<CarDetail>, response: Response<CarDetail>) {
                Log.d("Test", response.body().toString())
            }

            override fun onFailure(call: Call<CarDetail>, t: Throwable) {
                Log.d("Test", "Error")
            }

        })
    }

    private fun loadToken() : String? {
        val sharedPreferences = activity?.getSharedPreferences(Variables.sharedPrefLogin, Context.MODE_PRIVATE)!!
        return sharedPreferences.getString(Variables.sharedPrefToken, null)
    }
}