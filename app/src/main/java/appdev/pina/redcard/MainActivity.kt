package appdev.pina.redcard

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.widget.ExpandableListAdapter
import android.widget.Toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var expandableListAdapter: ExpandableListAdapter? = null
    var headerList: ArrayList<MenuModel> = ArrayList()
    var childList: HashMap<MenuModel, List<MenuModel>> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        prepareMenuData()
        populateExpandableList()

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
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
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun prepareMenuData() {
        //prepare headers
        val menuLeagues = MenuModel("Leagues", R.drawable.ic_menu_camera, true)
        val menuNationalTeams = MenuModel("National Teams", R.drawable.ic_menu_gallery, true)
        val menuAbout = MenuModel("About", R.drawable.ic_menu_send, false)

        headerList.apply {
            add(menuLeagues)
            add(menuNationalTeams)
            add(menuAbout)
        }

        //prepare childs
        val menuLeaguesChild1 = MenuModel("Liga 1", R.drawable.ic_menu_camera, false, LeagueActivity::class.java)
        val menuLeaguesChild2 = MenuModel("Liga 2", R.drawable.ic_menu_gallery, false, LeagueActivity::class.java)
        val menuLeaguesChild3 = MenuModel("Liga 3", R.drawable.ic_menu_camera, false, LeagueActivity::class.java)
        val menuLeaguesList = ArrayList<MenuModel>()
        menuLeaguesList.apply {
            add(menuLeaguesChild1)
            add(menuLeaguesChild2)
            add(menuLeaguesChild3)
        }

        val menuNationalTeamsChild1 = MenuModel("National Team 1", R.drawable.ic_menu_gallery, false, NationalTeamActivity::class.java)
        val menuNationalTeamsChild2 = MenuModel("National Team 2", R.drawable.ic_menu_camera, false, NationalTeamActivity::class.java)
        val menuNationalTeamsList = ArrayList<MenuModel>()
        menuNationalTeamsList.apply {
            add(menuNationalTeamsChild1)
            add(menuNationalTeamsChild2)
        }

        childList.let { list ->
            list[menuLeagues] = menuLeaguesList
            list[menuNationalTeams] = menuNationalTeamsList
        }
    }

    private fun populateExpandableList() {
        expandableListAdapter = DrawerExpandableListListener(this, headerList, childList)
        expandableListView.setAdapter(expandableListAdapter)

        expandableListView.setOnGroupClickListener { parent, v, groupPosition, id ->
            val menuItem = headerList[groupPosition]
            if (!menuItem.isGroup) {
                val intent = Intent(this, menuItem.cls)
                startActivity(intent)
                finish()
            }
            false
        }

        expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val child = childList[headerList[groupPosition]]
            if (child != null) {
                val item = child[childPosition]
                val intent = Intent(this, item.cls)
                startActivity(intent)
                finish()
            }

            false
        }
    }
}
