package eu.tutorials.ticktock.activities

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.databinding.ActivityCardDetailsBinding
import eu.tutorials.ticktock.dialogs.LabelColorListDialog
import eu.tutorials.ticktock.firebase.FireStoreClass
import eu.tutorials.ticktock.models.Board
import eu.tutorials.ticktock.models.Card
import eu.tutorials.ticktock.models.Task
import eu.tutorials.ticktock.utils.Constants

class CardDetailsActivity : BaseActivity() {
    // class vars
    private var binding: ActivityCardDetailsBinding? = null
    private var mTaskListPos = -1
    private var mCardPos = -1
    private var mSelectedColor: String = ""
    private lateinit var mBoardDetails: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        getIntentData()
        setUpActionBar()
        binding?.etNameCardDetails?.setText(mBoardDetails.taskList[mTaskListPos].cards[mCardPos].name)
        binding?.etNameCardDetails?.setSelection(binding?.etNameCardDetails?.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPos].cards[mCardPos].labelColor
        if (mSelectedColor.isNotEmpty()) {
            setColor()
        }

        binding?.tvSelectLabelColor?.setOnClickListener {
            labelColorsListDialog()
        }

        binding?.btnUpdateCardDetails?.setOnClickListener {
            if (binding?.etNameCardDetails?.text.toString().isNotEmpty()) {
                updateCardDetails()
            } else {
                Toast.makeText(this@CardDetailsActivity, "Enter a card name...", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun colorsList(): ArrayList<String> {
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43c86f")
        colorsList.add("#0c90f1")
        colorsList.add("#f72400")
        colorsList.add("#7a8089")
        colorsList.add("#ff69b4")
        colorsList.add("#d57c1d")
        colorsList.add("#770000")
        colorsList.add("#0022f8")
        return colorsList
    }

    private fun setColor() {
        binding?.tvSelectLabelColor?.text = ""
        binding?.tvSelectLabelColor?.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun labelColorsListDialog() {
        val colorsList: ArrayList<String> = colorsList()
        val listDialog = object : LabelColorListDialog(
            this@CardDetailsActivity,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor) {
                override fun onItemSelected(color: String) {
                    mSelectedColor = color
                    setColor()
                }
            }
        listDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_card -> {
                alertForDeleteCard(mBoardDetails.taskList[mTaskListPos].cards[mCardPos].name)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun addUpdateTaskSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateCardDetails() {
        val card = Card(
            binding?.etNameCardDetails?.text.toString(),
            mBoardDetails.taskList[mTaskListPos].cards[mCardPos].createdBy,
            mBoardDetails.taskList[mTaskListPos].cards[mCardPos].assignedTo,
            mSelectedColor
        )
        mBoardDetails.taskList[mTaskListPos].cards[mCardPos] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun deleteCard() {
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPos].cards
        cardsList.removeAt(mCardPos)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[mTaskListPos].cards = cardsList

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun alertForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert))
        builder.setMessage(resources.getString(R.string.confirm_message_to_delete_card, cardName))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, which ->
            dialogInterface.dismiss()
            deleteCard()
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

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