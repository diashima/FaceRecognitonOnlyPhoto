package kz.tengrilab.facerecognitononlyphoto.results.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import kz.tengrilab.facerecognitononlyphoto.ApiClient
import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.Variables.loadCredentials
import kz.tengrilab.facerecognitononlyphoto.data.Man
import kz.tengrilab.facerecognitononlyphoto.databinding.FragmentDetailsBinding
import kz.tengrilab.facerecognitononlyphoto.results.GetResultsInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: DetailsFragmentArgs by navArgs()
    private val top = 10

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val originalLink = args.photoOriginal.replace("10.77.1.62", "10.77.6.62")
        //val cropLink = args.photoPath.replace("10.77.1.62", "10.77.6.62")
        val originalLink = Variables.url + Variables.port + args.photoOriginal
        val cropLink = Variables.url + Variables.port + args.photoPath
        Picasso.get().load(cropLink).into(binding.imageViewCrop)
        Picasso.get().load(originalLink).into(binding.imageViewOriginal)
        getDetails()
    }

    private fun getDetails() {
        val retrofit = ApiClient.getRetrofitClient(requireContext())
        val detailsInterface = retrofit.create(GetResultsInterface::class.java)
        val token = loadCredentials(requireActivity())
        val header = Variables.headers2
        Log.d("Test", "get details started")
        val call = detailsInterface.getDetails(header + token, args.resultPath, args.uniqueId, args.faceId, top.toString())
        call.enqueue(object : Callback<List<Man>> {
            override fun onResponse(call: Call<List<Man>>, response: Response<List<Man>>) {
                Log.d("Test", "TEST: ${response.body()} ")
                val details = response.body()!!
                binding.recyclerRecognition.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(context)
                    adapter = DetailsAdapter(details)
                }
            }

            override fun onFailure(call: Call<List<Man>>, t: Throwable) {
                Log.d("Test", "fail")
                Toast.makeText(requireContext(), "Соединение разорвано", Toast.LENGTH_LONG).show()
            }
        })
    }
}