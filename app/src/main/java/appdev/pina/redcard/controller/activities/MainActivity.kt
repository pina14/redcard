package appdev.pina.redcard.controller.activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.widget.ExpandableListAdapter
import android.widget.Toast
import appdev.pina.redcard.R
import appdev.pina.redcard.controller.App
import appdev.pina.redcard.controller.fragments.HomeFragment
import appdev.pina.redcard.controller.fragments.ProfileFragment
import appdev.pina.redcard.model.DrawerExpandableListListener
import appdev.pina.redcard.model.FragmentUtils
import appdev.pina.redcard.model.MenuModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.drawer_content_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var expandableListAdapter: ExpandableListAdapter? = null
    private var headerList: ArrayList<MenuModel> = ArrayList()
    private var childList: HashMap<MenuModel, List<MenuModel>> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        title = getString(R.string.app_name)

        prepareMenuData()
        populateExpandableList()

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onResume() {
        super.onResume()

        @Suppress("UNCHECKED_CAST")
        FragmentUtils.showFragment(this, supportFragmentManager, HomeFragment::class.java as Class<Fragment>, R.id.main_content_frame)
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

    private fun prepareMenuData() {
        //prepare headers
        val session = MenuModel("Session", R.drawable.common_google_signin_btn_icon_dark, true)
        val menuAbout = MenuModel("Home", R.drawable.ic_menu_send, false, kotlinx.coroutines.Runnable {
            @Suppress("UNCHECKED_CAST")
            FragmentUtils.showFragment(this, supportFragmentManager, HomeFragment::class.java as Class<Fragment>, R.id.main_content_frame)

            drawer_layout.closeDrawer(GravityCompat.START)
        })

        headerList.apply {
            add(menuAbout)
            add(session)
        }

        //prepare childs
        val menuSessionList = ArrayList<MenuModel>()

        val menuSessionLogin = MenuModel("Login", R.drawable.log_in_out, false, kotlinx.coroutines.Runnable {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        })
        val menuSessionSignup = MenuModel("Sign up", android.R.drawable.ic_input_add, false, kotlinx.coroutines.Runnable {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        })

        val menuSessionProfile = MenuModel("Profile", R.drawable.ic_menu_gallery, false, kotlinx.coroutines.Runnable {
            @Suppress("UNCHECKED_CAST")
            FragmentUtils.showFragment(this, supportFragmentManager, ProfileFragment::class.java as Class<Fragment>, R.id.main_content_frame)
            drawer_layout.closeDrawer(GravityCompat.START)
        })
        val menuSessionLogout = MenuModel("Log out",R.drawable.log_in_out, false, kotlinx.coroutines.Runnable {
            App.firebaseOps.signOutUser()
            App.signedUser = null
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })

        menuSessionList.apply {
            if(App.firebaseOps.isUserLoggedIn()) {
                add(menuSessionProfile)
                add(menuSessionLogout)
            }
            else {
                add(menuSessionLogin)
                add(menuSessionSignup)
            }
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
