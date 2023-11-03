package eu.tutorials.ticktock.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.databinding.ActivityTaskListBinding
import eu.tutorials.ticktock.firebase.FireStoreClass
import eu.tutorials.ticktock.models.Board
import eu.tutorials.ticktock.utils.Constants

class TaskListActivity : BaseActivity() {
    // class vars
    private var binding: ActivityTaskListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        var boardDocID = ""
        if (intent.hasExtra(Constants.DOC_ID)) {
            boardDocID = intent.getStringExtra(Constants.DOC_ID).toString()
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this, boardDocID)
    }

    fun boardDetails(board: Board) {
        hideProgressDialog()
        setUpActionBar(board.name)
    }

    private fun setUpActionBar(title: String) {
        setSupportActionBar(binding?.toolbarTaskListActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_btn_24dp)
            actionBar.title = title
            binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (findViewById<DrawerLayout>(R.id.drawer_layout).isDrawerOpen(GravityCompat.START)) {
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
            super.onBackPressed()
        } else {
            doubleBackToExit()
            super.onBackPressed()
        }
    }
}