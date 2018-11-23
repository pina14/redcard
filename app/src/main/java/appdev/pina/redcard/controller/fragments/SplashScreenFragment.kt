package appdev.pina.redcard.controller.fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import appdev.pina.redcard.R
import appdev.pina.redcard.controller.App
import appdev.pina.redcard.controller.activities.MainActivity
import appdev.pina.redcard.firebase.FirebaseOps

class SplashScreenFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let { act ->
            App.firebaseOps.getDynamicLink(act.intent) { deepLink ->
                if (deepLink != null && deepLink.getBooleanQueryParameter(FirebaseOps.REFERRED_BY_LABEL, false)) {
                    //sign out user to receive reward
                    App.firebaseOps.logoutUser()

                    val referredBy = deepLink.getQueryParameter(FirebaseOps.REFERRED_BY_LABEL)

                    val action = SplashScreenFragmentDirections.actionSplashToReferral().setReferredBy(referredBy)
                    findNavController().navigate(action)
                } else{
                    Handler().postDelayed({
                        val opts = NavOptions.Builder().setPopUpTo(R.id.splashScreenFragment, true).build()
                        findNavController().navigate(R.id.action_splash_to_home, null, opts)
                        (act as MainActivity).showSystemUI()
                    }, 2000)
                }
            }
        }
    }
}
