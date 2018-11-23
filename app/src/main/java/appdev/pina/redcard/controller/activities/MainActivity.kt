package appdev.pina.redcard.controller.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import appdev.pina.redcard.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setupWithNavController(findNavController(R.id.nav_host_fragment))

        hideSystemUI()
    }

    fun hideSystemUI() {
        appbar.visibility = View.GONE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun showSystemUI() {
        appbar.visibility = View.VISIBLE
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
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

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawer = drawer_layout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
