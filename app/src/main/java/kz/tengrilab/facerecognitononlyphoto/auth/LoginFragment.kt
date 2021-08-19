package kz.tengrilab.facerecognitononlyphoto.auth

import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kz.tengrilab.facerecognitononlyphoto.ApiClient
import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.databinding.FragmentLoginBinding
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked)
                binding.inputPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            else
                binding.inputPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }

        binding.btnAuth.setOnClickListener {
            val uName = binding.inputUserName.text.toString()
            val uPass = binding.inputPassword.text.toString()
            if (uName == "" || uPass == "")
                Toast.makeText(requireContext(), "Empty", Toast.LENGTH_LONG).show()
            else
                signIn(uName, uPass)
        }
    }

    private fun signIn(username: String, password: String) {
        val retrofit = ApiClient.getRetrofitClient(requireContext())
        val authInterface = retrofit.create(AuthInterface::class.java)

        val deviceId = Variables.getDeviceId(requireContext())
        Log.d("Test", deviceId)
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("username", username)
            .addFormDataPart("password", password)
            .addFormDataPart("code", deviceId)
            .build()
        val call = authInterface.getUser(body)
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("Test", response.toString())
                if (response.code() == 200) {
                    val body = response.body()
                    try {
                        val obj = JSONObject(body!!.string())
                        val token = obj.getString("token")
                        val userId = obj.getInt("user_id")
                        saveCredentials(username, token, userId)
                        //val expiresIn = obj.getInt("expires_in")
                        //val expiresAt = obj.getString("expires_at")
                        Snackbar.make(view!!, "Вход выполнен", Snackbar.LENGTH_SHORT).show()

                        LoginFragmentDirections.actionConnect().apply {
                            findNavController().navigate(this)
                        }
                    }
                    catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                else if (response.code() == 404) {
                    binding.textViewUser.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
                Log.d("Test", t.message!!)
            }

        })
    }

    private fun saveCredentials(login: String, token: String, userId: Int) {
        val sharedPref = activity?.getSharedPreferences(Variables.sharedPrefLogin, Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(Variables.sharedPrefLogin, login)
            putString(Variables.sharedPrefToken, token)
            putInt(Variables.sharedPredId, userId)
            apply()
        }
    }
}