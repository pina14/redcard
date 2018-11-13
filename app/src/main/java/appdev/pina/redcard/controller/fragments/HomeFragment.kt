package appdev.pina.redcard.controller.fragments

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import appdev.pina.redcard.R
import appdev.pina.redcard.model.FragmentUtils
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.favorites
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        @Suppress("UNCHECKED_CAST")
        when(item.itemId) {
            R.id.favorites -> FragmentUtils.showFragment(context, childFragmentManager, FavsFragment::class.java as Class<Fragment>, R.id.home_content_frame)
            R.id.today -> FragmentUtils.showFragment(context, childFragmentManager, TodayFragment::class.java as Class<Fragment>, R.id.home_content_frame)
            R.id.previous -> FragmentUtils.showFragment(context, childFragmentManager, PreviousFragment::class.java as Class<Fragment>, R.id.home_content_frame)
        }
        return true
    }
}
