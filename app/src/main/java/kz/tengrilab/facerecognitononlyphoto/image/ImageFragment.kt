package kz.tengrilab.facerecognitononlyphoto.image

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.muddzdev.styleabletoast.StyleableToast
import kz.tengrilab.facerecognitononlyphoto.ApiClient
import kz.tengrilab.facerecognitononlyphoto.BuildConfig
import kz.tengrilab.facerecognitononlyphoto.R
import kz.tengrilab.facerecognitononlyphoto.Variables
import kz.tengrilab.facerecognitononlyphoto.databinding.FragmentImageBinding
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
import java.text.SimpleDateFormat
import java.util.*

class ImageFragment : Fragment() {

    private var mImageFileLocation = ""
    private val CAMERA_PIC_REQUEST = 1111
    private lateinit var postPath: String
    private lateinit var fileUri: Uri

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        captureImage()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_PIC_REQUEST) {
            if (Build.VERSION.SDK_INT > 25) {
                val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.getDefault()).format(
                    Date()
                )
                postPath = mImageFileLocation
                val file = File(postPath)
                if (file.exists()) {
                    detectFaces(postPath, requireContext())
                    captureImage()
                    Log.d("Test", "captureImage")
                } else {
                    ImageFragmentDirections.actionConnect().apply {
                        findNavController().navigate(this)
                    }
                }
            } else {
                postPath = fileUri.path!!
                val file = File(postPath)
                if (file.exists()) {
                    detectFaces(postPath, requireContext())
                    captureImage()
                    Log.d("Test", "captureImage2")
                } else {
                    ImageFragmentDirections.actionConnect().apply {
                        findNavController().navigate(this)
                    }
                }
            }
        }
    }

    private fun sendOriginal(path: String, uuid: UUID) {
        Log.d("Test", "sendImage started")
        //val file = File(path)
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
                val answer = response.code()
                Log.d("Test", answer.toString())
                if (response.body() != null) {
                    Log.d("Test", response.body().toString())
                    if (response.code() == 200) {
                        StyleableToast.makeText(requireContext(), "Отправлено", Toast.LENGTH_LONG, R.style.mytoast).show()
                        Log.d("Test", "response 200")
                    }
                }
                if (response.code() == 401) {
                    ImageFragmentDirections.actionConnect().apply {
                        findNavController().navigate(this)
                    }
                    Toast.makeText(requireContext(), "Зайдите в аккаунт", Toast.LENGTH_SHORT).show()
                    Log.d("Test", "response 401")
                    TODO("startActivity(new Intent(getApplicationContext(), AuthRetryActivity.class));")
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Test", "not sent")
                StyleableToast.makeText(requireContext(), "Не отправлено", Toast.LENGTH_LONG, R.style.mytoast).show()
            }
        })
    }

    private fun sendImage(path: String, uuid: UUID) {
        Log.d("Test", "sendImage started")
        //val file = File(path)
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
                val answer = response.code()
                Log.d("Test", answer.toString())
                if (response.body() != null) {
                    Log.d("Test", response.body().toString())
                    if (response.code() == 200) {
                        StyleableToast.makeText(requireContext(), "Отправлено", Toast.LENGTH_LONG, R.style.mytoast).show()
                        Log.d("Test", "crop response 200")
                    }
                }
                if (response.code() == 401) {
                    ImageFragmentDirections.actionConnect().apply {
                        findNavController().navigate(this)
                    }
                    Toast.makeText(requireContext(), "Зайдите в аккаунт", Toast.LENGTH_SHORT).show()
                    Log.d("Test", "crop response 401")
                    TODO("startActivity(new Intent(getApplicationContext(), AuthRetryActivity.class));")
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Test", "crop not sent")
                StyleableToast.makeText(requireContext(), "Не отправлено", Toast.LENGTH_LONG, R.style.mytoast).show()
            }
        })
    }

    private fun captureImage() {
        val callCameraIntent = Intent()
        callCameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE

        val photoFile: File = createImageFile()

        Log.d("Test", photoFile.toString())

        val outputUri = FileProvider.getUriForFile(
            requireContext(),
            BuildConfig.APPLICATION_ID + ".provider",
            photoFile
        )

        callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
        callCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(callCameraIntent, CAMERA_PIC_REQUEST)
    }

    private fun createImageFile() : File {
        val timestamp = SimpleDateFormat("yyyy_MM_dd_HHmmSS", Locale.getDefault()).format(Date())
        val imageFileName = "IMAGE_$timestamp"

        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + "final")

        //val storageDirectory = getAppSpecificAlbumStorageDir(requireContext(), "${R.string.app_name}")

        if (!storageDirectory.exists()){
            storageDirectory.mkdir()
            Log.d("Test", "Creating directory")
        } else {
            Log.d("Test", "Directory exists!!!")
        }

        val image = File(storageDirectory, "$imageFileName.jpg")
        mImageFileLocation = image.absolutePath

        return image
    }

    private fun detectFaces(stringPath: String, context: Context) {
        var uri = ""
        var sum = 1
        val path = Uri.fromFile(File(stringPath))
        val bitmap = getCapturedImage(path, context)
        val image = InputImage.fromFilePath(context, path)


        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.15f)
            .build()

        val detector = FaceDetection.getClient(options)
        var count = 0

        detector.process(image)
            .addOnSuccessListener { faces ->
                Log.d("Test", "size: $faces.size")
                val uuid = UUID.randomUUID()
                sendOriginal(stringPath, uuid)
                for (face in faces) {
                    count += 1
                    //val bounds = face.boundingBox
                    //val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
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
                    sendImage(uri, uuid)
                }
            }
            .addOnFailureListener { e ->
                Log.d("Test", "crop is NOT !!!!!!! created")
            }
    }

    private fun saveImage(bitmap: Bitmap, uuid: UUID, count: Int) : String {
        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + "final")

        val file = File(storageDirectory, "$uuid-$count.jpg")
        Log.d("Test", file.toString())
        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()
        }
        return file.absolutePath
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
}