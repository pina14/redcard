package appdev.pina.redcard.controller.fragments

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import appdev.pina.redcard.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.title = getString(R.string.app_name)

        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.favorites
        bottomNavigationView.setupWithNavController(Navigation.findNavController(activity as Activity, R.id.nav_host_home_fragment))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }
}
