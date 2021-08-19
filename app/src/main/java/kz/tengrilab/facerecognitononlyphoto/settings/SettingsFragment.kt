package kz.tengrilab.facerecognitononlyphoto.settings

import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kz.tengrilab.facerecognitononlyphoto.ApiClient
import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.databinding.FragmentSettingsBinding
import kz.tengrilab.facerecognitononlyphoto.showSnack
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadIp()
        binding.IPAddressText.setText(loadIp())
        binding.buttonEdit.setOnClickListener {
            binding.buttonEdit.isEnabled = false
            binding.buttonSave.isEnabled = true
        }

        binding.buttonSave.setOnClickListener {
            val ip = binding.IPAddressText.text.toString()
            saveIp(ip)
            binding.buttonSave.isEnabled = false
            binding.buttonEdit.isEnabled = true
        }

        binding.buttonChange.setOnClickListener {
            binding.ipLayout.visibility = View.GONE
            binding.layoutPassword.visibility = View.VISIBLE
            binding.buttonChange.isEnabled = false
        }

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                binding.textOldPass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.textNewPass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.textConfirmPass.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            else {
                binding.textOldPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.textNewPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.textConfirmPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
        }

        binding.buttonSavePass.setOnClickListener {
            val retrofit = ApiClient.getRetrofitClient(requireContext())
            val settingsInterface = retrofit.create(SettingsInterface::class.java)

            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("old_password", binding.textOldPass.text.toString())
                .addFormDataPart("password", binding.textNewPass.text.toString())
                .addFormDataPart("password2", binding.textConfirmPass.text.toString())
                .build()

            val call = settingsInterface.changePassword(Variables.headers2 + loadToken(), loadId(), body)
            call.enqueue(object : Callback<ResponseBody?>{
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    Log.d("Test", response.code().toString())
                    if (response.code() == 200) {
                        binding.layoutPassword.visibility = View.GONE
                        binding.ipLayout.visibility = View.VISIBLE
                        binding.buttonChange.isEnabled = true
                        binding.errorText.text = ""
                        showSnack("Пароль успешно изменён")
                    }
                    if (response.code() == 400) {
                        Log.d("Test", response.errorBody()!!.string())
                        binding.errorText.text = "Пароль должен содержать минимум 8 символов, состоять из букв и цифр, и не должен быть слишком распространенным."
                        binding.textOldPass.text = null
                        binding.textNewPass.text = null
                        binding.textConfirmPass.text = null
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                }
            })
        }
    }

    private fun saveIp(ip: String) {
        val sharedPref = activity?.getSharedPreferences(Variables.sharedPrefIp, Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(Variables.sharedPrefIp, ip)
            apply()
        }
    }

    private fun loadIp() : String? {
        val sharedPreferences = activity?.getSharedPreferences(Variables.sharedPrefIp, Context.MODE_PRIVATE)!!
        return sharedPreferences.getString(Variables.sharedPrefIp, Variables.url)
    }

    private fun loadId() : Int {
        val sharedPreferences = activity?.getSharedPreferences(Variables.sharedPrefLogin, Context.MODE_PRIVATE)!!
        return sharedPreferences.getInt(Variables.sharedPredId, 0)
    }

    private fun loadToken() : String? {
        val sharedPreferences = activity?.getSharedPreferences(Variables.sharedPrefLogin, Context.MODE_PRIVATE)!!
        return sharedPreferences.getString(Variables.sharedPrefToken, null)
    }
}