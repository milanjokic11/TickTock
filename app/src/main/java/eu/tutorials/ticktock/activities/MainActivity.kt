
package eu.tutorials.ticktock.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import eu.tutorials.ticktock.R
import eu.tutorials.ticktock.adapters.BoardItemsAdapter
import eu.tutorials.ticktock.databinding.ActivityMainBinding
import eu.tutorials.ticktock.firebase.FireStoreClass
import eu.tutorials.ticktock.models.Board
import eu.tutorials.ticktock.models.User
import eu.tutorials.ticktock.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    // class variables
    private var binding: ActivityMainBinding? = null
    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpActionBar()
        findViewById<NavigationView>(R.id.nav_view).setNavigationItemSelectedListener(this)
        // set create board functionality
        findViewById<FloatingActionButton>(R.id.fab_create_board).setOnClickListener{
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivity(intent)
        }

        FireStoreClass().loadUserData(this, true)
    }

    fun setBoardsListToUI(boardsList: ArrayList<Board>) {
        hideProgressDialog()
        val rvBoardsList = findViewById<RecyclerView>(R.id.rv_boards_list)
        val tvNoBoards = findViewById<TextView>(R.id.tv_no_boards_available)
        if (boardsList.size > 0) {
            rvBoardsList.visibility = View.VISIBLE
            tvNoBoards.visibility = View.GONE

            rvBoardsList.layoutManager = LinearLayoutManager(this)
            rvBoardsList.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this, boardsList)
            rvBoardsList.adapter = adapter
        } else {
            rvBoardsList.visibility = View.GONE
            tvNoBoards.visibility = View.VISIBLE
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar_main))
        findViewById<Toolbar>(R.id.toolbar_main).setNavigationIcon(R.drawable.ic_action_navigation_menu)
        findViewById<Toolbar>(R.id.toolbar_main).setNavigationOnClickListener {
            // toggle drawer
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if (findViewById<DrawerLayout>(R.id.drawer_layout).isDrawerOpen(GravityCompat.START)) {
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
        } else {
            findViewById<DrawerLayout>(R.id.drawer_layout).openDrawer(GravityCompat.START)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivityForResult(intent, PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean) {
        // set global user name
        mUserName = user.name
        val headerView = findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById<CircleImageView>(R.id.cv_nav_user_image))

        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)
        navUsername.text = user.name

        if (readBoardsList) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardsList(this)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PROFILE_REQUEST_CODE) {
            FireStoreClass().loadUserData(this@MainActivity)
        } else {
            Log.e("Main.onActivityResult()", "Cancelled...")
        }
    }

    companion object {
        const val PROFILE_REQUEST_CODE: Int = 11
    }
}