package appdev.pina.redcard.controller.fragments

import android.arch.lifecycle.Observer
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import appdev.pina.redcard.R
import appdev.pina.redcard.controller.App
import appdev.pina.redcard.controller.activities.MainActivity
import appdev.pina.redcard.controller.activities.VerifyEmailActivity
import appdev.pina.redcard.model.SignedUser
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.title = "Profile"

        val user = App.getSignedUser()
        if(App.firebaseOps.isUserLoggedOut() || user == null) {
            App.firebaseOps.logoutUser()
            activity?.startActivity(Intent(context, MainActivity::class.java))
            activity?.finish()
            return
        }

        if(!App.firebaseOps.isUserLoggedInAndVerified()) {
            activity?.startActivity(Intent(context, VerifyEmailActivity::class.java))
            activity?.finish()
            return
        }

        updateUI(user)

        copy_referral_button.setOnClickListener {
            val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Referral", user.referralLink)
            clipboard.primaryClip = clip

            Snackbar.make(profile_layout, "Copied referral!", Snackbar.LENGTH_SHORT).show()
        }

        delete_account_text.setOnClickListener {
            App.firebaseOps.deleteUser { task ->
                if(task.isSuccessful) {
                    Intent(activity, MainActivity::class.java).also { intent ->
                        startActivity(intent)
                    }
                    activity?.finish()
                } else
                    Snackbar.make(profile_layout, "Error deleting account!", Snackbar.LENGTH_SHORT).show()
            }
        }

        App.getUserLive().observe(this, Observer { nUser ->
            if(nUser == null) {
                Intent(activity, MainActivity::class.java).also {
                    activity?.startActivity(it)
                }
                activity?.finish()
                return@Observer
            }
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
