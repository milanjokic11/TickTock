package eu.tutorials.ticktock.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.databinding.ActivityCreateBoardBinding
import eu.tutorials.ticktock.firebase.FireStoreClass
import eu.tutorials.ticktock.models.Board
import eu.tutorials.ticktock.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    // class variables
    private var binding: ActivityCreateBoardBinding? = null
    private var mSelectedImageURI: Uri? = null
    private var mBoardImageURL: String = ""
    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpActionBar()
        if (intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }
        // set up photo picker
        binding?.ivBoardImage?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        // set up create board btn
        binding?.btnCreate?.setOnClickListener {
            if (mSelectedImageURI != null) {
                uploadBoardImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    fun boardCreated() {
        hideProgressDialog()
        finish()
    }

    private fun createBoard() {
        val assignedUsersList: ArrayList<String> = ArrayList()
        assignedUsersList.add(getCurrentUserID())
        var board = Board(
            binding?.etBoardName?.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersList
        )
        FireStoreClass().createBoard(this, board)
    }

    private fun uploadBoardImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val sRef: StorageReference = FirebaseStorage.getInstance()
            .reference.child("BOARD_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(this, mSelectedImageURI))
        sRef.putFile(mSelectedImageURI!!).addOnSuccessListener { taskSnapshot ->
            Log.i("Board Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.i("Downloadable Image URL", uri.toString())
                mBoardImageURL = uri.toString()
                createBoard()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this@CreateBoardActivity,  exception.message, Toast.LENGTH_SHORT).show()
            hideProgressDialog()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            }
        } else {
            Toast.makeText(this, "Oops, you just denied permission to storage. Please turn it on to use feature...", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            mSelectedImageURI = data.data
            // set new image
            try {
                Glide
                    .with(this)
                    .load(mSelectedImageURI)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(findViewById(R.id.iv_board_image))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Oops, something went wrong inside CreateBoard.onActivityResult() ...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarCreateBoard)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_btn_24dp)
            actionBar.title = resources.getString(R.string.profile_title)
            binding?.toolbarCreateBoard?.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }
}