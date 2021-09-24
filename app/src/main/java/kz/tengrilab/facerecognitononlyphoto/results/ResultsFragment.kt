package kz.tengrilab.facerecognitononlyphoto.results

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.muddz.styleabletoast.StyleableToast
import kz.tengrilab.facerecognitononlyphoto.ApiClient
import kz.tengrilab.facerecognitononlyphoto.App
import kz.tengrilab.facerecognitononlyphoto.R
import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.Variables.loadCredentials
import kz.tengrilab.facerecognitononlyphoto.data.Face
import kz.tengrilab.facerecognitononlyphoto.databinding.FragmentResultsBinding
import kz.tengrilab.facerecognitononlyphoto.image.ImageFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class   ResultsFragment : Fragment(), ResultsAdapter.OnItemClickListener {

    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!
    private lateinit var key: List<Face.Result>
    //private val resultList = App.resultList

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Log.d("Test", "result list cache: $resultList.toString()")
        /*if (resultList != null) {
            binding.recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = ResultsAdapter(resultList,this@ResultsFragment)
            }
        }*/
        getResults()
    }

    private fun getResults() {
        val retrofit = ApiClient.getRetrofitClient()
        val resultInterface = retrofit.create(GetResultsInterface::class.java)
        val token = loadCredentials(requireActivity())
        Log.d("Test", token!!)
        val header = Variables.headers2

        val call = resultInterface.getResults(header + token, "1" )
        call.enqueue(object : Callback<Face> {
            override fun onResponse(call: Call<Face>, response: Response<Face>) {
                Log.d("Test", response.body().toString())
                if (response.code() == 200) {
                    if (response.body() != null) {
                        binding.progressBar.visibility = View.GONE
                        key = response.body()!!.results
                        Log.d("Test", key.toString())
                        binding.recyclerView.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(context)
                            adapter = ResultsAdapter(key, this@ResultsFragment)
                        }
                    }
                } else if (response.code() == 401) {
                    ImageFragmentDirections.actionConnect().apply {
                        findNavController().navigate(this)
                    }
                    Toast.makeText(requireContext(), "Зайдите в аккаунт", Toast.LENGTH_SHORT).show()
                } else if (response.code() == 404) {
                    binding.progressBar.visibility = View.GONE
                    StyleableToast.makeText(requireContext(), "Пусто", Toast.LENGTH_LONG, R.style.mytoast2).show()
                }
            }

            override fun onFailure(call: Call<Face>, t: Throwable) {
                StyleableToast.makeText(requireContext(), "Проблемы с сетью", Toast.LENGTH_LONG, R.style.mytoast2).show()
            }
        })
    }

    override fun onItemCLick(position: Int) {
        val resultPath = key[position].resultsPath
        val uniqueId = key[position].uniqueId
        val faceId = key[position].faceId
        //val imageUrl = key[position].imageLink
        val photoPath = key[position].photoPath
        val photoOriginal = key[position].photoOriginal

        ResultsFragmentDirections.actionConnectDetailsFR(resultPath, uniqueId, faceId, photoPath, photoOriginal).apply {
            findNavController().navigate(this)
        }
    }
}