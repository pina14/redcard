package appdev.pina.redcard.controller.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import appdev.pina.redcard.R
import appdev.pina.redcard.controller.App
import kotlinx.android.synthetic.main.fragment_verify_email.*

class VerifyEmailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_verify_email, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(App.firebaseOps.isUserLoggedOut())
            findNavController().navigate(R.id.action_verifyEmail_to_home)

        activity?.title = getString(R.string.title_verify_email)

        val user = App.firebaseOps.getUserAuth()!!

        email_sent_to_text.text = getString(R.string.email_sent_string, user.email)

        sendEmail{}

        resend_button.setOnClickListener {
            sendEmail {
                Snackbar.make(verify_email_layout, "Email sent!", Snackbar.LENGTH_LONG).show()
            }
        }

        continue_button.setOnClickListener {
            App.firebaseOps.getUserAuth()?.reload()?.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val isVerified = App.firebaseOps.getUserAuth()?.isEmailVerified
                    if(isVerified == true)
                        findNavController().navigate(R.id.action_verifyEmail_to_profile)
                    else
                        Snackbar.make(verify_email_layout, "Account is not yet verified!", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun sendEmail(cb : () -> Unit) {
        App.firebaseOps.sendVerificationEmail { task ->
            if(task == null || !task.isSuccessful)
                Snackbar.make(verify_email_layout, "Error sending email, try again!", Snackbar.LENGTH_LONG).show()
            else
                cb()
        }
    }
}
