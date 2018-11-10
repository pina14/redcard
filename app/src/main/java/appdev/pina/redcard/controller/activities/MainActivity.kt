package appdev.pina.redcard.controller.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.widget.ExpandableListAdapter
import android.widget.Toast
import appdev.pina.redcard.R
import appdev.pina.redcard.controller.fragments.FavsFragment
import appdev.pina.redcard.controller.fragments.PreviousFragment
import appdev.pina.redcard.controller.fragments.TodayFragment
import appdev.pina.redcard.model.DrawerExpandableListListener
import appdev.pina.redcard.model.MenuModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.drawer_content_layout.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var expandableListAdapter: ExpandableListAdapter? = null
    private var headerList: ArrayList<MenuModel> = ArrayList()
    private var childList: HashMap<MenuModel, List<MenuModel>> = HashMap()
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        firebaseAuth = FirebaseAuth.getInstance()

        prepareMenuData()
        populateExpandableList()

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.favorites
    }

    private var backPressedBefore = false
    override fun onBackPressed() {
        val drawer = drawer_layout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if(backPressedBefore) {
                finish()
                return
            }
            Toast.makeText(this, "Click again to exit", Toast.LENGTH_LONG).show()
            GlobalScope.launch {
                backPressedBefore = true
                delay(3500)  //duration of Toast.LENGTH_LONG
                backPressedBefore = false
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        @Suppress("UNCHECKED_CAST")
        when(item.itemId) {
            R.id.favorites -> showFragment(FavsFragment::class.java as Class<Fragment>)
            R.id.today -> showFragment(TodayFragment::class.java as Class<Fragment>)
            R.id.previous -> showFragment(PreviousFragment::class.java as Class<Fragment>)
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showFragment(clazz : Class<Fragment>) {
        //locate fragment by tag if this already exists
        var fragment = supportFragmentManager.findFragmentByTag(clazz.canonicalName)

        // If fragment doesn't exist yet, create one
        if (fragment == null)
            fragment = Fragment.instantiate(this, clazz.name)

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content_frame, fragment, clazz.canonicalName)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    private fun prepareMenuData() {
        //prepare headers
        val session = MenuModel("Session", R.drawable.common_google_signin_btn_icon_dark, true)
        val menuAbout = MenuModel("Home", R.drawable.ic_menu_send, false, Runnable {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })

        headerList.apply {
            add(menuAbout)
            add(session)
        }

        //prepare childs
        val menuSessionLogin = MenuModel("Login", R.drawable.log_in_out, false, Runnable {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        })
        val menuSessionSignup = MenuModel("Sign up",android.R.drawable.ic_input_add, false, Runnable {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        })

        val menuSessionLogout = MenuModel("Log out",R.drawable.log_in_out, false, Runnable {
            firebaseAuth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })
        val menuSessionList = ArrayList<MenuModel>()
        menuSessionList.apply {
            if(firebaseAuth.currentUser == null) {
                add(menuSessionLogin)
                add(menuSessionSignup)
            } else
                add(menuSessionLogout)
        }

        childList.let { list ->
            list[session] = menuSessionList
        }
    }

    private fun populateExpandableList() {
        expandableListAdapter = DrawerExpandableListListener(this, headerList, childList)
        expandableListView.setAdapter(expandableListAdapter)

        expandableListView.setOnGroupClickListener { parent, v, groupPosition, id ->
            val menuItem = headerList[groupPosition]
            if (!menuItem.isGroup) {
                menuItem.command?.run()
            }
            false
        }

        expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val child = childList[headerList[groupPosition]]
            if (child != null) {
                val item = child[childPosition]
                item.command?.run()
            }

            false
        }
    }
}
