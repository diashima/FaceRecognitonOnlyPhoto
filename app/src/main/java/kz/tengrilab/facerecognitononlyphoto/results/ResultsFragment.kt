package kz.tengrilab.facerecognitononlyphoto.results

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kz.tengrilab.facerecognitononlyphoto.ApiClient
import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.Variables.loadCredentials
import kz.tengrilab.facerecognitononlyphoto.data.Face
import kz.tengrilab.facerecognitononlyphoto.databinding.FragmentResultsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultsFragment : Fragment(), ResultsAdapter.OnItemClickListener {

    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!
    private lateinit var key: List<Face.Result>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getResults()
    }

    private fun getResults() {
        val retrofit = ApiClient.getRetrofitClient()
        val resultInterface = retrofit.create(GetResultsInterface::class.java)
        val token = loadCredentials(requireActivity())
        val header = Variables.headers2

        val call = resultInterface.getResults(header + token, "1" )
        call.enqueue(object : Callback<Face> {
            override fun onResponse(call: Call<Face>, response: Response<Face>) {
                Log.d("Test", response.body().toString())
                if (response.body() != null) {
                    val results = response.body()!!.results
                    key = results
                    Log.d("Test", results.toString())
                    binding.recyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(context)
                        adapter = ResultsAdapter(results, this@ResultsFragment)
                    }
                }
            }

            override fun onFailure(call: Call<Face>, t: Throwable) {
                Log.d("Test", "Something went wrong 56")
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