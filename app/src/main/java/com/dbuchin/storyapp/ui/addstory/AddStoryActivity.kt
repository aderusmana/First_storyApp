package com.dbuchin.storyapp.ui.addstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.dbuchin.storyapp.R
import com.dbuchin.storyapp.databinding.ActivityAddStoryBinding
import com.dbuchin.storyapp.ui.main.MainActivity
import com.dbuchin.storyapp.util.createCustomTempFile
import com.dbuchin.storyapp.util.rotateImage
import com.dbuchin.storyapp.util.uriToFile
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream

class AddStoryActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AddStoryActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, AddStoryActivity::class.java)
            context.startActivity(starter)
        }
    }

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private val addStoryViewModel by viewModels<AddStoryViewModel>()
    private var getFile: File? = null

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                addStoryViewModel.setFile(myFile)
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                val bitmap = BitmapFactory.decodeFile(file.path)
                rotateImage(bitmap, currentPhotoPath).compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    FileOutputStream(file)
                )
                addStoryViewModel.setFile(file)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this@AddStoryActivity,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupAction()
        setupViewModel()
    }

    private fun setupViewModel() {
        addStoryViewModel.isPosted.observe(this) { isSuccess ->
            if (isSuccess) {
                val dialogBuilder = AlertDialog.Builder(this)
                    .setTitle("Success")
                    .setMessage("Story has been upload")
                    .setPositiveButton("Oke") { _,_ ->
                        MainActivity.start(this)
                        finish()
                    }
                    .setOnDismissListener {
                        MainActivity.start(this)
                        finish()
                    }
                val dialog = dialogBuilder.create()
                dialog.show()
            } else {
                Snackbar.make(binding.root, "Failed Upload", Snackbar.LENGTH_SHORT).show()
            }
        }

        addStoryViewModel.hasUploaded.observe(this) { file ->
            if (file != null) {
                Log.d(TAG, "true - with file")
                getFile = file
                binding.ivThumbnail.setImageBitmap(BitmapFactory.decodeFile(file.path))
                binding.ivThumbnail.setPadding(0)
            } else {
                binding.ivThumbnail.setImageResource(R.drawable.ic_thumbnail_image)
                binding.ivThumbnail.setPadding(32)
                Log.d(TAG, "true - without file")
            }
        }

        addStoryViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(value: Boolean) {
        binding.pbLoadingScreen.isVisible = value
        binding.btnUpload.isInvisible = value
        binding.btnGallery.isEnabled = !value
        binding.btnCamera.isEnabled = !value
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Not Have Permission",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupAction() {
        with(binding) {
            btnCamera.setOnClickListener { startTakePhoto() }
            btnGallery.setOnClickListener { openGallery() }
            btnUpload.setOnClickListener {
                edAddDescription.clearFocus()
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        when {
            getFile == null -> Snackbar.make(binding.root, getString(R.string.please_input_image), Snackbar.LENGTH_SHORT).show()
            binding.edAddDescription.text.isNullOrBlank() -> {
                binding.edAddDescription.error = getString(R.string.error_empty_description)
                binding.edAddDescription.requestFocus()
            }
            else -> {
                val file = getFile as File
                val description = binding.edAddDescription.text.toString()
                addStoryViewModel.postStory(file, description)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoUri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.dbuchin.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }
}