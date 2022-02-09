package kz.tengrilab.fr_cppr.results.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import io.github.muddz.styleabletoast.StyleableToast
import kz.tengrilab.fr_cppr.ApiClient
import kz.tengrilab.fr_cppr.R
import kz.tengrilab.fr_cppr.Variables
import kz.tengrilab.fr_cppr.Variables.loadCredentials
import kz.tengrilab.fr_cppr.data.Man
import kz.tengrilab.fr_cppr.databinding.FragmentDetailsBinding
import kz.tengrilab.fr_cppr.results.GetResultsInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsFragment : Fragment(), DetailsAdapter.OnItemClickListener {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: DetailsFragmentArgs by navArgs()
    private lateinit var details: List<Man>
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
        //Picasso.get().load(originalLink).into(binding.imageViewOriginal)
        binding.progressBar.visibility = View.VISIBLE
        getDetails()

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun getDetails() {
        val retrofit = ApiClient.getRetrofitClient()
        val detailsInterface = retrofit.create(GetResultsInterface::class.java)
        val token = loadCredentials(requireActivity())
        val header = Variables.headers2
        Log.d("Test", "get details started")
        val call = detailsInterface.getDetails(header + token, args.resultPath, args.uniqueId, args.faceId, top.toString())
        call.enqueue(object : Callback<List<Man>> {
            override fun onResponse(call: Call<List<Man>>, response: Response<List<Man>>) {
                Log.d("Test", "TEST: ${response.body()} with code ${response.code()} ")
                when {
                    response.code() == 200 -> {
                        binding.progressBar.visibility = View.GONE
                        details = response.body()!!
                        binding.recyclerRecognition.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(context)
                            adapter = DetailsAdapter(details, this@DetailsFragment)
                        }
                    }
                    response.code() == 404 -> {
                        binding.progressBar.visibility = View.GONE
                        StyleableToast.makeText(requireContext(), "Пусто", Toast.LENGTH_LONG, R.style.mytoast2).show()
                    }
                    else -> {
                        StyleableToast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT, R.style.mytoast2).show()
                        DetailsFragmentDirections.actionConnectMainFR().apply {
                            findNavController().navigate(this)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Man>>, t: Throwable) {
                StyleableToast.makeText(requireContext(), "Проблемы с сетью", Toast.LENGTH_LONG, R.style.mytoast2).show()
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    override fun onItemCLick(position: Int) {
        val udNumber = details[position].iin

        DetailsFragmentDirections.actionConnectCarDetailsFr(udNumber).apply {
            findNavController().navigate(this)
        }
    }
}