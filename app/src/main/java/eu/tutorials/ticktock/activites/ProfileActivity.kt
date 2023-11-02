package eu.tutorials.ticktock.activites

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.databinding.ActivityProfileBinding
import eu.tutorials.ticktock.firebase.FireStoreClass
import eu.tutorials.ticktock.models.User
import eu.tutorials.ticktock.utils.Constants
import java.io.IOException

class ProfileActivity : BaseActivity() {
    // class variables
    private var binding: ActivityProfileBinding? = null
    private var mSelectedFileURI: Uri? = null
    private var mProfileURL: String = ""
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpActionBar()
        FireStoreClass().loadUserData(this)
        binding?.ivProfileUserImage?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                ActivityCompat.requestPermissions(this
                    , arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    , READ_STORAGE_PERMISSION_CODE)
            }
        }
        binding?.btnUpdate?.setOnClickListener {
            if (mSelectedFileURI != null) {
                uploadUserImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            }
        } else {
            Toast.makeText(this@ProfileActivity, "Oops, you just denied permission to storage. Please turn it on to use feature...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImageChooser() {
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarProfileActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_btn_24dp)
            actionBar.title = resources.getString(R.string.profile_title)
            binding?.toolbarProfileActivity?.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    fun setUserDataInUI(user: User) {
        mUserDetails = user
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.iv_profile_user_image))

        binding?.etName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
        if (user.mobile != 0L) {
            binding?.etMobile?.setText(user.mobile.toString())
        }
    }

    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mSelectedFileURI != null) {
            val sRef: StorageReference = FirebaseStorage.getInstance()
                .reference.child("USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(mSelectedFileURI))
            sRef.putFile(mSelectedFileURI!!).addOnSuccessListener {
                taskSnapshot ->
                    Log.i("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                        Log.i("Downloadable Image URL", uri.toString())
                        mProfileURL = uri.toString()
                }
            }.addOnFailureListener {
                exception ->
                    Toast.makeText(this@ProfileActivity,  exception.message, Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
            }
        }
    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }
}