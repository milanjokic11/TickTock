package eu.tutorials.ticktock.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.adapters.CardMemberListItemsAdapter
import eu.tutorials.ticktock.databinding.ActivityCardDetailsBinding
import eu.tutorials.ticktock.dialogs.LabelColorListDialog
import eu.tutorials.ticktock.dialogs.MembersListDialog
import eu.tutorials.ticktock.firebase.FireStoreClass
import eu.tutorials.ticktock.models.Board
import eu.tutorials.ticktock.models.Card
import eu.tutorials.ticktock.models.SelectedMembers
import eu.tutorials.ticktock.models.Task
import eu.tutorials.ticktock.models.User
import eu.tutorials.ticktock.utils.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CardDetailsActivity : BaseActivity() {
    // class vars
    private var binding: ActivityCardDetailsBinding? = null
    private var mTaskListPos = -1
    private var mCardPos = -1
    private var mSelectedColor: String = ""
    private var mSelectedDueDateMilliseconds: Long = 0
    private lateinit var mBoardDetails: Board
    private lateinit var mMembersDetailList: ArrayList<User>

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

        setUpSelectedMembersList()

        binding?.tvSelectMembers?.setOnClickListener {
            membersListDialog()
        }

        mSelectedDueDateMilliseconds = mBoardDetails.taskList[mTaskListPos].cards[mCardPos].dueDate
        if (mSelectedDueDateMilliseconds > 0) {
            val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(mSelectedDueDateMilliseconds))
            findViewById<TextView>(R.id.tv_select_due_date).text = selectedDate
        }
        binding?.tvSelectDueDate?.setOnClickListener {
            showDatePicker()
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

    private fun membersListDialog() {
        var cardAssignedMembersList = mBoardDetails.taskList[mTaskListPos].cards[mCardPos].assignedTo

        if (cardAssignedMembersList.size > 0 ) {
            for (i in mMembersDetailList.indices) {
                for (j in cardAssignedMembersList) {
                    if (mMembersDetailList[i].id == j) {
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        } else {
            for (i in mMembersDetailList.indices) {
                mMembersDetailList[i].selected = false
            }
        }
        val listDialog = object: MembersListDialog(
            this,
            mMembersDetailList,
            resources.getString(R.string.str_select_member)
        ) {
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECT) {
                    if (mBoardDetails.taskList[mTaskListPos].cards[mCardPos].assignedTo.contains(user.id)) {
                        mBoardDetails.taskList[mTaskListPos].cards[mCardPos].assignedTo.add(user.id)
                    }
                } else {
                    mBoardDetails.taskList[mTaskListPos].cards[mCardPos].assignedTo.remove(user.id)

                    for (i in mMembersDetailList.indices) {
                        if (mMembersDetailList[i].id == user.id) {
                            mMembersDetailList[i].selected = false
                        }
                    }
                }
                setUpSelectedMembersList()
            }
        }
        listDialog.show()
    }

    private fun setUpSelectedMembersList() {
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskListPos].cards[mCardPos].assignedTo
        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for (i in mMembersDetailList.indices) {
            for (j in cardAssignedMembersList) {
                if (mMembersDetailList[i].id == j) {
                    val selectedMember = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if (selectedMembersList.size > 0) {
            val recyclerView = findViewById<RecyclerView>(R.id.rv_selected_members_list)
            selectedMembersList.add(SelectedMembers("", ""))

            findViewById<TextView>(R.id.tv_select_members).visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            recyclerView.layoutManager = GridLayoutManager(this, 6)
            val adapter = CardMemberListItemsAdapter(this, selectedMembersList, true)
            recyclerView.adapter = adapter
            adapter.setOnClickListener(object:
                CardMemberListItemsAdapter.OnClickListener {
                    override fun onClick() {
                        membersListDialog()
                    }
                }
            )
        } else {
            val recyclerView = findViewById<RecyclerView>(R.id.rv_selected_members_list)
            findViewById<TextView>(R.id.tv_select_members).visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
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
            mSelectedColor,
            mSelectedDueDateMilliseconds
        )
        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

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

        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
            dialogInterface.dismiss()
            deleteCard()
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
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
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
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

    private fun showDatePicker() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonthOfYear, selectedDayOfMonth ->
                val sDayOfMonth = if (selectedDayOfMonth < 10) "0$selectedDayOfMonth" else "$selectedDayOfMonth"
                val sMonthOfYear = if ((selectedMonthOfYear + 1) < 10) "0${selectedMonthOfYear + 1}" else "${selectedMonthOfYear + 1}"

                val selectedDate = "$sMonthOfYear/$sDayOfMonth/$selectedYear"
                findViewById<TextView>(R.id.tv_select_due_date).text = selectedDate

                val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)

                val theDate = sdf.parse(selectedDate)

                mSelectedDueDateMilliseconds = theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show()
    }

}