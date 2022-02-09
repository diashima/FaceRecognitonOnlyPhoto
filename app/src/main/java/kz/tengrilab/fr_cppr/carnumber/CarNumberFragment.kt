package kz.tengrilab.fr_cppr.carnumber

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
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.squareup.picasso.Picasso
import io.github.muddz.styleabletoast.StyleableToast
import kz.tengrilab.fr_cppr.*
import kz.tengrilab.fr_cppr.data.CarCrop
import kz.tengrilab.fr_cppr.data.CarDetail
import kz.tengrilab.fr_cppr.databinding.FragmentCarnumberBinding
import kz.tengrilab.fr_cppr.gallery.GalleryFragment
import kz.tengrilab.fr_cppr.image.GetProperImageFile
import kz.tengrilab.fr_cppr.image.SendImageInterface
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

class CarNumberFragment : Fragment(), CarCropAdapter.OnItemClickListener {

    companion object {
        const val PICK_IMAGE_MULTIPLE = 1
        const val CAMERA_PIC_REQUEST = 1111
    }

    private var mImageFileLocation = ""

    private lateinit var postPath: String
    private val list = ArrayList<CarCrop>()
    private var cropCount = 0
    private var answerCount = 0
    private var foundCrops = 0
    private var currentIdCrop = 0
    lateinit var imagePath: String
    private val pathList = ArrayList<String>()

    private var _binding: FragmentCarnumberBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarnumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.buttonSearch.setOnClickListener {
            if (binding.editCarInfo.text.toString() == "") {
                StyleableToast.makeText(requireContext(), "Введите номер", Toast.LENGTH_LONG, R.style.mytoast2).show()
            } else {
                searchCarNumber()
            }
        }

        binding.rightButton.setOnClickListener {
            if (list.size > 1) {
                currentIdCrop += 1
                binding.editCarInfo.setText(list[currentIdCrop].number)
                Picasso.get()
                    .load(Variables.url + Variables.port + list[currentIdCrop].url)
                    .into(binding.imageView)
                if (currentIdCrop + 1 > 1) {
                    binding.leftButton.isEnabled = true
                    binding.leftButton.visibility = View.VISIBLE
                }
                if (currentIdCrop + 1 == list.size) {
                    binding.rightButton.isEnabled = false
                    binding.rightButton.visibility = View.INVISIBLE
                }
            }
        }

        binding.leftButton.setOnClickListener {
            if (currentIdCrop > 0) {
                currentIdCrop -= 1
                binding.editCarInfo.setText(list[currentIdCrop].number)
                Picasso.get()
                    .load(Variables.url + Variables.port + list[currentIdCrop].url)
                    .into(binding.imageView)
                if (currentIdCrop == 0) {
                    binding.leftButton.isEnabled = false
                    binding.leftButton.visibility = View.INVISIBLE
                }
                if (currentIdCrop + 1 < list.size) {
                    binding.rightButton.isEnabled = true
                    binding.rightButton.visibility = View.VISIBLE
                }
            }
        }

        binding.buttonMakePhoto.setOnClickListener {
            disableButtons()
            captureImage()
        }

        binding.buttonGallery.setOnClickListener {
            disableButtons()
            pickImage()
        }

        binding.buttonResults.setOnClickListener {
            CarNumberFragmentDirections.actionConnectResultCarFr().apply {
                findNavController().navigate(this)
            }
        }
        binding.buttonClear.setOnClickListener {
            binding.editCarInfo.text.clear()
            binding.carInputLayout.visibility = View.VISIBLE
            binding.tableCar.visibility = View.GONE
            binding.tableCitizen.visibility = View.GONE
            binding.buttonClear.visibility = View.GONE
            binding.imagePerson.visibility = View.GONE
            binding.tableIur.visibility = View.GONE
        }
    }

    private fun disableButtons(){
        list.clear()
        binding.cropLayout.visibility = View.GONE
        cropCount = 0
        answerCount = 0
        foundCrops = 0
        binding.editCarInfo.text.clear()
        binding.buttonSearch.isEnabled = false
        binding.buttonMakePhoto.isEnabled = false
        binding.buttonGallery.isEnabled = false
        binding.buttonResults.isEnabled = false
    }

    private fun enableButtons(){
        binding.buttonSearch.isEnabled = true
        binding.buttonMakePhoto.isEnabled = true
        binding.buttonGallery.isEnabled = true
        binding.buttonResults.isEnabled = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_PIC_REQUEST) {
            postPath = mImageFileLocation
            val file = File(postPath)
            if (file.exists()) {
                detectFaces(postPath, requireContext())
            } else {
                enableButtons()
            }
        } else if (requestCode == GalleryFragment.PICK_IMAGE_MULTIPLE) {
            if (resultCode == Activity.RESULT_OK && null != data) {
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
            }
            else {
                enableButtons()
            }
        }
    }

    private fun searchCarNumber() {
        val retrofit = ApiClient.getRetrofitClient()
        val clientInterface = retrofit.create(GetCarDetailsInterface::class.java)
        binding.carInputLayout.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        val call = clientInterface.sendCarNumber(Variables.headers2 + loadToken(), binding.editCarInfo.text.toString())
        call.enqueue(object : Callback<CarDetail> {
            override fun onResponse(call: Call<CarDetail>, response: Response<CarDetail>) {
                binding.progressBar.visibility = View.GONE
                Log.d("Test", response.body().toString())
                if (response.code() == 200) {
                    val result = response.body()!!.data
                    binding.cropLayout.visibility = View.GONE
                    binding.tableCar.visibility = View.VISIBLE
                    binding.buttonClear.visibility = View.VISIBLE
                    if (result.car_number_type.lowercase() == "iur") {
                        binding.tableIur.visibility = View.VISIBLE
                        binding.textIur.text = result.iur
                    }
                    if (result.fiz != null) {
                        binding.tableCitizen.visibility = View.VISIBLE
                        binding.imagePerson.visibility = View.VISIBLE
                        binding.textSurname.text = result.fiz.lastname
                        binding.textFirstName.text = result.fiz.firstname
                        binding.textSecondName.text = result.fiz.secondname
                        binding.textIn.text = result.fiz.gr_code
                        binding.textUd.text = result.fiz.ud_code
                        binding.textLicense.text = result.vu_serial
                        binding.textDateLicense.text = result.vu_end
                        //val url = Variables.imageUrl + Variables.port + "/files/udgrphotos/" + result.fiz.ud_code + ".ldr"
                        try {
                            val url = Variables.url + Variables.port + "/files/udgrphotos/" + result.fiz.ud_code.toLong().toString() + ".ldr"
                            Picasso.get().load(url).into(binding.imagePerson)
                        }
                        catch (e: java.lang.Exception) {
                            Log.d("Test", e.message!!)
                        }
                    }
                    binding.textGrnz.text = result.car_number
                    binding.textCarModel.text = result.car_model
                    binding.textCarYear.text = result.car_year
                    binding.textVin.text = result.vin
                    binding.textTechPass.text = result.teh_passport
                    binding.textDateTechPass.text = result.teh_passport_date
                } else if (response.code() == 404) {
                    StyleableToast.makeText(requireContext(), "Автомобиль отсутствует", Toast.LENGTH_LONG, R.style.mytoast2).show()
                    binding.carInputLayout.visibility = View.VISIBLE
                    list.clear()
                    binding.cropLayout.visibility = View.GONE
                    binding.editCarInfo.setText("")
                }
            }

            override fun onFailure(call: Call<CarDetail>, t: Throwable) {
                Log.d("Test", "Error")
                binding.carInputLayout.visibility = View.VISIBLE
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
        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + "koregen_car")
        if (!storageDirectory.exists()){
            storageDirectory.mkdir()
            Log.d("Test", "Creating directory")
        } else {
            Log.d("Test", "Directory exists!")
        }
        val image = File(storageDirectory, "$imageFileName.jpg")
        mImageFileLocation = image.absolutePath
        return image
    }

    private fun detectFaces(stringPath: String, context: Context) {
        var uri = ""
        val path = Uri.fromFile(File(stringPath))
        val bitmap = getCapturedImage(path, context)
        val image = InputImage.fromFilePath(context, path)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        var count = 0

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                if (visionText.textBlocks.size == 0) {
                    enableButtons()
                    StyleableToast.makeText(requireContext(), "Номера на фото не распознаны", Toast.LENGTH_SHORT, R.style.mytoast2).show()
                }
                val uuid = UUID.randomUUID()
                sendOriginal(stringPath, uuid)
                for (block in visionText.textBlocks) {
                    count += 1
                    cropCount += 1
                    val boundingBox = block.boundingBox!!
                    val textBitmap = Bitmap
                        .createBitmap(
                            bitmap,
                            boundingBox.left,
                            boundingBox.top,
                            boundingBox.width(),
                            boundingBox.height()
                        )
                    uri = saveImage(textBitmap, uuid, count)
                    Log.d("Test", "crop created: $count, uri: $uri")
                    //list.add(uri)
                    sendCrop(uri, uuid)
                }
            }
            .addOnFailureListener { _ ->
                enableButtons()
                StyleableToast.makeText(requireContext(), "Номена на фото не распознаны", Toast.LENGTH_LONG, R.style.mytoast2).show()
            }
    }

    private fun saveImage(bitmap: Bitmap, uuid: UUID, count: Int) : String {
        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + "koregen_car")
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

    private fun sendCrop(path: String, uuid: UUID) {
        binding.progressBar.visibility = View.VISIBLE
        val file = GetProperImageFile.getRotatedImageFile(File(path), requireContext())
        val retrofit = ApiClient.getRetrofitClient()
        val clientInterface = retrofit.create(SendImageInterface::class.java)
        val requestBody = file!!.asRequestBody("*/*".toMediaTypeOrNull())
        val code = uuid.toString()
        val partFile = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val partCode = MultipartBody.Part.createFormData("code", code)
        val call = clientInterface.uploadCarCrop(Variables.headers2 + loadToken(), partFile, partCode)
        call.enqueue(object : Callback<CarCrop> {
            override fun onResponse(call: Call<CarCrop>, response: Response<CarCrop>) {
                answerCount += 1
                Log.d("Test", "crop code: ${response.code()}")
                if (response.code() == 200) {
                    if (response.body() != null) {
                        foundCrops += 1
                        //StyleableToast.makeText(requireContext(), "Отправлено", Toast.LENGTH_SHORT, R.style.mytoast).show()
                        Log.d("Test", response.body().toString())
                        list.add(CarCrop(response.body()!!.number, response.body()!!.url))
                    }
                } else if (response.code() == 401) {
                    CarNumberFragmentDirections.actionConnectMainFR().apply {
                        findNavController().navigate(this)
                    }
                    showSnack("Войдите в аккаунт")
                } else if (response.code() == 404 ){
                    if (answerCount == cropCount) {
                        if (foundCrops == 0) {
                            enableButtons()
                            StyleableToast.makeText(requireContext(), "Не распознаны номера", Toast.LENGTH_SHORT, R.style.mytoast2).show()
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
                if (answerCount == cropCount) {
                    if (foundCrops > 0) {
                        //binding.chooseLabel.visibility = View.VISIBLE
                        StyleableToast.makeText(requireContext(), "Распознано $foundCrops номеров", Toast.LENGTH_SHORT, R.style.mytoast).show()
                        binding.progressBar.visibility = View.GONE
                        enableButtons()
                        binding.cropLayout.visibility = View.VISIBLE
                        binding.leftButton.isEnabled = false
                        binding.leftButton.visibility = View.INVISIBLE
                        binding.rightButton.isEnabled = false
                        binding.rightButton.visibility = View.INVISIBLE
                        binding.editCarInfo.setText(list[0].number)
                        Picasso.get()
                            .load(Variables.url + Variables.port + list[0].url)
                            .into(binding.imageView)
                        if (list.size > 1) {
                            binding.rightButton.isEnabled = true
                            binding.rightButton.visibility = View.VISIBLE
                        }
                    }
                }
            }
            override fun onFailure(call: Call<CarCrop>, t: Throwable) {
                StyleableToast.makeText(requireContext(), "Не отправлено", Toast.LENGTH_SHORT, R.style.mytoast2).show()
            }
        })
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
                if (response.code() == 200) {
                    if (response.body() != null) {
                        Log.d("Test", response.body().toString())
                    }
                } else if (response.code() == 401) {
                    CarNumberFragmentDirections.actionConnectMainFR().apply {
                        findNavController().navigate(this)
                    }
                    showSnack("Войдите в аккаунт")
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
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

    override fun onItemCLick(position: Int) {
        binding.editCarInfo.setText(list[position].number)
    }

    private fun pickImage(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, GalleryFragment.PICK_IMAGE_MULTIPLE)
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
                pathList.add(imagePath)
                detectFaces(imagePath, requireContext())
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("Test", e.message, e)
        }
    }


}