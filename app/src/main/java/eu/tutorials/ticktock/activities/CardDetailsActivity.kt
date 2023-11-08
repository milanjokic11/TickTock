package eu.tutorials.ticktock.activities

import android.os.Bundle
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.databinding.ActivityCardDetailsBinding
import eu.tutorials.ticktock.models.Board
import eu.tutorials.ticktock.utils.Constants

class CardDetailsActivity : BaseActivity() {
    // class vars
    private var binding: ActivityCardDetailsBinding? = null
    private var mTaskListPos = -1
    private var mCardPos = -1
    private lateinit var mBoardDetails: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        getIntentData()
        setUpActionBar()
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POS)) {
            mTaskListPos = intent.getIntExtra(Constants.TASK_LIST_ITEM_POS, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POS)) {
            mCardPos = intent.getIntExtra(Constants.CARD_LIST_ITEM_POS, -1)
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarCardDetailsActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_btn_24dp)
            actionBar.title = mBoardDetails.taskList[mTaskListPos].cards[mCardPos].name
            binding?.toolbarCardDetailsActivity?.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }
}