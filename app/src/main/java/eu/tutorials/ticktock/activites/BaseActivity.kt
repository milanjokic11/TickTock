package eu.tutorials.ticktock.activites

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.databinding.ActivityBaseBinding

open class BaseActivity : AppCompatActivity() {
    private var binding: ActivityBaseBinding? = null
    private var doubleBackExitPressed = false
    private lateinit var mProgressDialog: Dialog
    private lateinit var tvProgressText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        tvProgressText = mProgressDialog.findViewById(R.id.tv_progress_text)
        tvProgressText.text = text
        mProgressDialog.show()

    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit() {
        if (doubleBackExitPressed) {
            super.onBackPressed()
            return
        }
        this.doubleBackExitPressed = true
        Toast.makeText(this, resources.getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({doubleBackExitPressed = false}, 2000)
    }

    fun showErrorSnackBar(message: String) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_error))
        snackBar.show()
    }
}