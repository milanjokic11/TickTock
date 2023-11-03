package eu.tutorials.ticktock.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.databinding.ActivityCreateBoardBinding

class CreateBoardActivity : AppCompatActivity() {
    // class variables
    private var binding: ActivityCreateBoardBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpActionBar()
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