package kz.tengrilab.fr_cppr.results.car

import android.content.Context
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
import kz.tengrilab.fr_cppr.ApiClient
import kz.tengrilab.fr_cppr.R
import kz.tengrilab.fr_cppr.Variables
import kz.tengrilab.fr_cppr.data.ResultCar
import kz.tengrilab.fr_cppr.databinding.FragmentCarResultsBinding
import kz.tengrilab.fr_cppr.results.GetResultsInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CarResultsFragment : Fragment(), PageAdapter.OnItemClickListener, CarResultAdapter.OnItemClickListener {

    private var _binding: FragmentCarResultsBinding? = null
    private val binding get() = _binding!!
    private var listInt = mutableListOf(1)
    private var currentPage = 1
    private lateinit var listResults: List<ResultCar.ResultCarDetails>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        searchResults(1)
    }

    private fun searchResults(page: Int) {
        val retrofit = ApiClient.getRetrofitClient()
        val clientInterface = retrofit.create(GetResultsInterface::class.java)
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        val currentDate = sdf.format(Date())
        Log.d("Test", currentDate)

        val call = clientInterface.getCarResults(Variables.headers2 + loadToken(), 15, page)
        call.enqueue(object : Callback<ResultCar>{
            override fun onResponse(call: Call<ResultCar>, response: Response<ResultCar>) {
                Log.d("Test", response.code().toString())
                if (response.code() == 200) {
                    if (response.body() != null) {
                        listResults = response.body()!!.results
                        Log.d("Test", response.body().toString())
                        if (response.body()!!.count > 15) {
                            val pageCount = kotlin.math.ceil(response.body()!!.count / 15.0)

                            for (i in 2..pageCount.toInt()) {
                                if (!listInt.contains(i)) {
                                    listInt.add(i)
                                }
                            }
                        }

                        currentPage = page
                        val stringPage = "???????????????? $currentPage"
                        binding.textPage.text = stringPage
                        binding.recyclerAllNews.apply{
                            layoutManager = LinearLayoutManager(context)
                            adapter = CarResultAdapter(response.body()!!.results, this@CarResultsFragment)
                        }
                        binding.recyclerAllPages.apply {
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            adapter = PageAdapter(listInt, this@CarResultsFragment)
                        }
                    }
                } else {
                    StyleableToast.makeText(requireContext(), "???????????? ????????", Toast.LENGTH_LONG, R.style.mytoast2).show()
                }
            }

            override fun onFailure(call: Call<ResultCar>, t: Throwable) {
                StyleableToast.makeText(requireContext(), "???????????????? ?? ??????????", Toast.LENGTH_LONG, R.style.mytoast2).show()
            }

        })
    }

    private fun loadToken() : String? {
        val sharedPreferences = activity?.getSharedPreferences(Variables.sharedPrefLogin, Context.MODE_PRIVATE)!!
        return sharedPreferences.getString(Variables.sharedPrefToken, null)
    }

    override fun onItemClick(position: Int) {
        searchResults(position + 1)
    }

    override fun onResultCLick(position: Int) {
        /*CarResultsFragmentDirections.actionConnectDetailsFromResultsFR(listResults[position].id).apply {
            findNavController().navigate(this)
        }*/
        CarResultsFragmentDirections.actionConnectActivity(listResults[position].id).apply {
            findNavController().navigate(this)
        }
    }
}