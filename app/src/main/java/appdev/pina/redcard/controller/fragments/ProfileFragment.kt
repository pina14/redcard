package appdev.pina.redcard.controller.fragments

import android.arch.lifecycle.Observer
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import appdev.pina.redcard.R
import appdev.pina.redcard.controller.App
import appdev.pina.redcard.model.SignedUser
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.title = getString(R.string.title_profile)

        val user = App.getSignedUser()
        if(App.firebaseOps.isUserLoggedOut() || user == null) {
            App.firebaseOps.logoutUser()
            findNavController().navigate(R.id.action_profile_to_login)
            return
        }

        if(!App.firebaseOps.isUserLoggedInAndVerified()) {
            findNavController().navigate(R.id.action_verify_email)
            return
        }

        updateUI(user)

        copy_referral_button.setOnClickListener {
            val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Referral", user.referralLink)
            clipboard.primaryClip = clip

            Snackbar.make(profile_layout, "Copied referral!", Snackbar.LENGTH_SHORT).show()
        }

        delete_account_button.setOnClickListener {
            App.firebaseOps.deleteUser { task ->
                if(!task.isSuccessful)
                    Snackbar.make(profile_layout, "Error deleting account!", Snackbar.LENGTH_SHORT).show()
            }
        }

        logout_button.setOnClickListener {
            App.firebaseOps.logoutUser()
        }

        App.getUserLive().observe(this, Observer { nUser ->
            if(nUser == null)
                findNavController().navigate(R.id.action_profile_to_home)
            else
                updateUI(nUser)
        })
    }

    private fun updateUI(user: SignedUser) {
        username_text.text = user.username
        email_text.text = user.email
        user_balance_value.text = user.balance.toString()
        referral_value.text = user.referralLink
    }
}
