package appdev.pina.redcard.controller.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import appdev.pina.redcard.R

class ReferralFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_referral, container, false)
    }

    fun getReferredBy () : String? = ReferralFragmentArgs.fromBundle(arguments).referredBy
}
