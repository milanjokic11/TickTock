package eu.tutorials.ticktock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import eu.tutorials.ticktock.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    // activity variables
    private var binding: ActivitySignInBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        // set flags
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // set action bar
        setUpActionBar()
    }


    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarSignInActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_back_btn_24dp)
        }
        binding?.toolbarSignInActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}