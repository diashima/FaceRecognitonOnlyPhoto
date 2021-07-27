package kz.tengrilab.facerecognitononlyphoto.gallery

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.muddzdev.styleabletoast.StyleableToast
import kz.tengrilab.facerecognitononlyphoto.ApiClient
import kz.tengrilab.facerecognitononlyphoto.R
import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.databinding.FragmentGalleryBinding
import kz.tengrilab.facerecognitononlyphoto.image.SendImageInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val list = ArrayList<String>()
    lateinit var imagePath: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pickImage()
        binding.btnMultipleSend.setOnClickListener {
            sendImage(list)
        }
    }


    private fun sendImage(list: ArrayList<String>){
        Toast.makeText(requireContext(), "SEND", Toast.LENGTH_LONG).show()
        val token = Variables.loadCredentials(requireActivity())
        val retrofit = ApiClient.getRetrofitClient()
        val imagesInterface = retrofit.create(SendImageInterface::class.java)
        val multipartArray = arrayOfNulls<MultipartBody.Part>(list.size)
        for (i in list.indices) {
            val file = File(list[i])
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            multipartArray[i] = MultipartBody.Part.createFormData("files", file.name, requestBody)
        }
        val call = imagesInterface.uploadMultipleImage(Variables.headers2 + token, multipartArray)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                when(response.code()){
                    200 -> StyleableToast.makeText(requireContext(), "Отправлено", Toast.LENGTH_LONG, R.style.mytoast).show()
                    401 -> {
                        GalleryFragmentDirections.actionConnect().apply {
                            findNavController().navigate(this)
                        }
                        StyleableToast.makeText(requireContext(), "Войдите в аккаунт", Toast.LENGTH_LONG, R.style.mytoast2).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                StyleableToast.makeText(requireContext(), "Не отправлено", Toast.LENGTH_LONG, R.style.mytoast2).show()
            }

        })
    }

    private fun pickImage(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_MULTIPLE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK
            && null != data) {
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    getPathFromURI(imageUri)
                }
            } else if (data.data != null) {
                val imagePath = data.data!!
                Log.d("Test", imagePath.toString());
            }
            binding.recyclerView.apply {
                Log.d("Test", list.toString())
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = GalleryAdapter(list)
            }
        }
    }

    private fun getPathFromURI(uri: Uri) {
        var path: String = uri.path!! // uri = any content Uri

        val databaseUri: Uri
        val selection: String?
        val selectionArgs: Array<String>?
        if (path.contains("/document/image:")) { // files selected from "Documents"
            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = "_id=?"
            selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
        } else { // files selected from all other sources, especially on Samsung devices
            databaseUri = uri
            selection = null
            selectionArgs = null
        }
        try {
            val projection = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Media.DATE_TAKEN
            ) // some example data you can query
            val cursor = requireContext().contentResolver.query(
                databaseUri,
                projection, selection, selectionArgs, null
            )
            if (cursor!!.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(projection[0])
                imagePath = cursor.getString(columnIndex)
                // Log.e("path", imagePath);
                list.add(imagePath)
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("Test", e.message, e)
        }
    }

    companion object {
        const val PICK_IMAGE_MULTIPLE = 1
    }
}