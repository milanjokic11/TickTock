package eu.tutorials.ticktock.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.adapters.TaskListItemsAdapter
import eu.tutorials.ticktock.databinding.ActivityTaskListBinding
import eu.tutorials.ticktock.firebase.FireStoreClass
import eu.tutorials.ticktock.models.Board
import eu.tutorials.ticktock.models.Card
import eu.tutorials.ticktock.models.Task
import eu.tutorials.ticktock.utils.Constants

class TaskListActivity : BaseActivity() {
    // class vars
    private var binding: ActivityTaskListBinding? = null
    private lateinit var mBoardDetails: Board
    private lateinit var mBoardDocID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(Constants.DOC_ID)) {
            mBoardDocID = intent.getStringExtra(Constants.DOC_ID).toString()
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this, mBoardDocID)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE ) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardDetails(this, mBoardDocID)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_members -> {
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun cardDetails(taskListPos: Int, cardPos: Int) {
        val intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POS, taskListPos)
        intent.putExtra(Constants.CARD_LIST_ITEM_POS, cardPos)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    fun boardDetails(board: Board) {
        mBoardDetails = board
        hideProgressDialog()
        setUpActionBar()

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        binding?.rvTaskList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvTaskList?.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this, board.taskList)
        binding?.rvTaskList?.adapter = adapter
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarTaskListActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_btn_24dp)
            actionBar.title = mBoardDetails.name
            binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this, mBoardDetails.docID)
    }

    fun createTaskList(taskListName: String) {
        val task = Task(taskListName, FireStoreClass().getCurrentUserID())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun updateTaskList(pos: Int, listName: String, model: Task) {
        val task = Task(listName, model.createdBy)
        mBoardDetails.taskList[pos] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun deleteTaskList(pos: Int) {
        mBoardDetails.taskList.removeAt(pos)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun addCardToTask(pos: Int, cardName: String) {
        val currUserID = FireStoreClass().getCurrentUserID()
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(currUserID)

        val card = Card(cardName, currUserID, cardAssignedUsersList)
        val cardList = mBoardDetails.taskList[pos].cards
        cardList.add(card)
        
        val task = Task(mBoardDetails.taskList[pos].title, mBoardDetails.taskList[pos].createdBy, cardList)

        mBoardDetails.taskList[pos] = task

        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    companion object {
        const val MEMBERS_REQUEST_CODE: Int = 13
        const val CARD_DETAILS_REQUEST_CODE: Int = 14
    }

}