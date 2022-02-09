package kz.tengrilab.fr_cppr.results.car

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.navArgs
import com.squareup.picasso.Picasso
import io.github.muddz.styleabletoast.StyleableToast
import kz.tengrilab.fr_cppr.ApiClient
import kz.tengrilab.fr_cppr.R
import kz.tengrilab.fr_cppr.Variables
import kz.tengrilab.fr_cppr.carnumber.GetCarDetailsInterface
import kz.tengrilab.fr_cppr.data.CarDetailById
import kz.tengrilab.fr_cppr.databinding.ActivityDetailsFromResultBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsFromResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsFromResultBinding
    private val args: DetailsFromResultActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsFromResultBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Log.d("Test", "here in onCreate")
        searchCarNumber()

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun searchCarNumber() {
        Log.d("Test", "here in activity")
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
                    if (obj.getString("car_number_type").lowercase() == "iur") {
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
                            val url = Variables.url + Variables.port + "/files/udgrphotos/" + fiz.getString("unique_id").toLong().toString() + ".ldr"
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
                    StyleableToast.makeText(applicationContext, "Автомобиль отсутствует", Toast.LENGTH_LONG, R.style.mytoast2).show()
                }
            }

            override fun onFailure(call: Call<CarDetailById>, t: Throwable) {
                Log.d("Test", "Error")
                StyleableToast.makeText(applicationContext, "Проблемы с сетью", Toast.LENGTH_LONG, R.style.mytoast2).show()
            }

        })
    }

    private fun loadToken() : String? {
        val sharedPreferences = getSharedPreferences(Variables.sharedPrefLogin, Context.MODE_PRIVATE)!!
        return sharedPreferences.getString(Variables.sharedPrefToken, null)
    }
}