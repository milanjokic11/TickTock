package eu.tutorials.ticktock.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.adapters.MemberListItemsAdapter
import eu.tutorials.ticktock.databinding.ActivityMembersBinding
import eu.tutorials.ticktock.firebase.FireStoreClass
import eu.tutorials.ticktock.models.Board
import eu.tutorials.ticktock.models.User
import eu.tutorials.ticktock.utils.Constants

class MembersActivity : BaseActivity() {
    // class vars
    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>
    private var binding: ActivityMembersBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        setUpActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)
    }

    fun memberDetails(user: User) {
        mBoardDetails.assignedTo.add(user.id)
        FireStoreClass().assignMemberToBoard(this, mBoardDetails, user)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_member -> {
                dialogSearchMember()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()
            if (email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FireStoreClass().getMemberDetails(this, email)
            } else {
                Toast.makeText(this@MembersActivity, "Please enter member's email address...", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun memberAssignedSuccess(user: User) {
        hideProgressDialog()
        mAssignedMembersList.add(user)
        setUpMembersList(mAssignedMembersList)
    }

    // fun. to display assigned members list
    fun setUpMembersList(list: ArrayList<User>) {
        mAssignedMembersList = list

        hideProgressDialog()

        binding?.rvMembersList?.layoutManager = LinearLayoutManager(this)
        binding?.rvMembersList?.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this, list)
        binding?.rvMembersList?.adapter = adapter
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarMembersActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_back_btn_24dp)
            actionBar.title = resources.getString(R.string.members)
            binding?.toolbarMembersActivity?.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

}