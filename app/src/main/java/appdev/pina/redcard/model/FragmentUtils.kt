package appdev.pina.redcard.model

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

/**
 * Created by Hugo on 13/11/2018
 */
object FragmentUtils {

    fun showFragment(ctx : Context?, fm : FragmentManager, clazz : Class<Fragment>, frameLayoutID : Int) {
        //locate fragment by tag if this already exists
        var fragment = fm.findFragmentByTag(clazz.canonicalName)

        // If fragment doesn't exist yet, create one
        if (fragment == null)
            fragment = Fragment.instantiate(ctx, clazz.name)

        fm.beginTransaction()
            .replace(frameLayoutID, fragment, clazz.canonicalName)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}