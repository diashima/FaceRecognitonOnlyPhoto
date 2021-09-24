package kz.tengrilab.facerecognitononlyphoto.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import io.github.muddz.styleabletoast.StyleableToast
import kz.tengrilab.facerecognitononlyphoto.ApiClient
import kz.tengrilab.facerecognitononlyphoto.R
import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.databinding.FragmentGalleryBinding
import kz.tengrilab.facerecognitononlyphoto.image.GetProperImageFile
import kz.tengrilab.facerecognitononlyphoto.image.SendImageInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

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
            detectFaces(list, requireContext())
        }
    }

    private fun detectFaces(list: ArrayList<String>, context: Context){
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.15f)
            .build()

        val detector = FaceDetection.getClient(options)
        for (stringPath in list) {
            var uri = ""
            val path = Uri.fromFile(File(stringPath))
            val bitmap = getCapturedImage(path, context)
            val image = InputImage.fromFilePath(context, path)
            var count = 0

            detector.process(image)
                .addOnSuccessListener { faces ->
                    Log.d("Test", "size: $faces.size")
                    val uuid = UUID.randomUUID()
                    sendOriginal(stringPath, uuid)
                    for (face in faces) {
                        count += 1
                        val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees
                        val faceBitmap = Bitmap
                            .createBitmap(
                                bitmap,
                                face.boundingBox.left,
                                face.boundingBox.top,
                                face.boundingBox.width(),
                                face.boundingBox.height()
                            ).rotate(rotZ)

                        val newBitmap = Bitmap.createScaledBitmap(
                            faceBitmap,
                            112,
                            112,
                            true
                        )
                        uri = saveImage(newBitmap, uuid, count)
                        Log.d("Test", "crop created: $count, uri: $uri")
                        sendCrop(uri, uuid)
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("Test", "crop is NOT !!!!!!! created")
                }
        }
    }

    private fun saveImage(bitmap: Bitmap, uuid: UUID, count: Int) : String {
        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + "koregen_face")
        val file = File(storageDirectory, "$uuid-$count.jpg")
        Log.d("Test", file.toString())
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException){
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private fun sendOriginal(path: String, uuid: UUID) {
        val file = GetProperImageFile.getRotatedImageFile(File(path), requireContext())
        val retrofit = ApiClient.getRetrofitClient()
        val clientInterface = retrofit.create(SendImageInterface::class.java)
        val requestBody = file!!.asRequestBody("*/*".toMediaTypeOrNull())
        val code = uuid.toString()
        val partFile = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val partCode = MultipartBody.Part.createFormData("code", code)
        val call = clientInterface.uploadOrigin(Variables.headers2 + loadToken(), partFile, partCode)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("Test", "original photo response: ${response.code()}")
                if (response.code() == 200) {
                    if (response.body() != null) {
                        Log.d("Test", response.body().toString())
                        StyleableToast.makeText(requireContext(), "Отправлено", Toast.LENGTH_LONG, R.style.mytoast).show()
                    }
                } else if (response.code() == 401) {
                    GalleryFragmentDirections.actionConnect().apply {
                        findNavController().navigate(this)
                    }
                    Toast.makeText(requireContext(), "Зайдите в аккаунт", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Test", "not sent")
                StyleableToast.makeText(requireContext(), "Не отправлено", Toast.LENGTH_LONG, R.style.mytoast).show()
            }
        })
    }

    private fun sendCrop(path: String, uuid: UUID) {
        val file = GetProperImageFile.getRotatedImageFile(File(path), requireContext())
        val retrofit = ApiClient.getRetrofitClient()
        val clientInterface = retrofit.create(SendImageInterface::class.java)
        val requestBody = file!!.asRequestBody("*/*".toMediaTypeOrNull())
        val code = uuid.toString()
        val partFile = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val partCode = MultipartBody.Part.createFormData("code", code)
        val call = clientInterface.uploadImage(Variables.headers2 + loadToken(), partFile, partCode)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("Test", "crop response: ${response.code()}")
                if (response.code() == 200) {
                    if (response.body() != null) {
                        GalleryFragmentDirections.actionConnect().apply {
                            findNavController().navigate(this)
                        }
                        Log.d("Test", response.body().toString())
                        StyleableToast.makeText(requireContext(), "Отправлено", Toast.LENGTH_LONG, R.style.mytoast).show()
                    }
                } else if (response.code() == 401) {
                    GalleryFragmentDirections.actionConnect().apply {
                        findNavController().navigate(this)
                    }
                    Toast.makeText(requireContext(), "Зайдите в аккаунт", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Test", "crop not sent")
                StyleableToast.makeText(requireContext(), "Не отправлено", Toast.LENGTH_LONG, R.style.mytoast).show()
            }
        })
    }

    private fun pickImage(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
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
                Log.d("Test", "Clip data: ${data.clipData.toString()}")
                val count = data.clipData!!.itemCount
                Log.d("Test", count.toString())
                for (i in 0 until count) {
                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    getPathFromURI(imageUri)
                }
            } else if (data.data != null) {
                val imagePath = data.data!!
                getPathFromURI(imagePath)
                Log.d("Test", imagePath.toString());
            }
            binding.recyclerView.apply {
                Log.d("Test", list.toString())
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = GalleryAdapter(list)
            }
        } else {
            GalleryFragmentDirections.actionConnect().apply {
                findNavController().navigate(this)
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

    private fun getCapturedImage(selectedPhotoUri: Uri, context: Context): Bitmap {
        val source = ImageDecoder.createSource(context.contentResolver, selectedPhotoUri)
        return ImageDecoder.decodeBitmap(source)
    }

    private fun Bitmap.rotate(degrees: Float) : Bitmap {
        val matrix = Matrix().apply { postRotate(degrees)}
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun loadToken() : String? {
        val sharedPreferences = activity?.getSharedPreferences(Variables.sharedPrefLogin, Context.MODE_PRIVATE)!!
        return sharedPreferences.getString(Variables.sharedPrefToken, null)
    }
    
    companion object {
        const val PICK_IMAGE_MULTIPLE = 1
    }
}