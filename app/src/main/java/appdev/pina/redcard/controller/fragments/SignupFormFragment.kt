package appdev.pina.redcard.controller.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import appdev.pina.redcard.R
import appdev.pina.redcard.controller.App
import appdev.pina.redcard.firebase.FirebaseOps
import appdev.pina.redcard.model.SignedUser
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.android.synthetic.main.fragment_signup_form.*
import android.content.Context

class SignupFormFragment : Fragment() {

    var referredBy : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        signup_button.setOnClickListener{
            registerUser()
        }

        login_text.setOnClickListener{
            findNavController().navigate(R.id.action_signup_to_login)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if(parentFragment is ReferralFragment)
            referredBy = (parentFragment as ReferralFragment).getReferredBy()
    }

    private fun registerUser() {
        val username = username_edit_text.text.toString().trim()
        val email = signup_user_email_edit_text.text.toString().trim()
        val password = signup_user_password_edit_text.text.toString().trim()

        if(validateInputs(username, email, password))
            return

        signup_progress_bar.visibility = View.VISIBLE
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        App.firebaseOps.getUser(username){ docTask ->
            val doc = docTask.result
            if(doc != null && doc.exists()) {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                signup_progress_bar.visibility = View.GONE
                Snackbar.make(signup_button, "Username already taken!", Snackbar.LENGTH_LONG).show()
            } else if(!docTask.isSuccessful) {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                signup_progress_bar.visibility = View.GONE
                Snackbar.make(signup_button, "Error creating user!", Snackbar.LENGTH_LONG).show()
            }
            else {
                App.firebaseOps.generateDynamicLink("https://hugofora.wixsite.com/crypto-imperium-app/?${FirebaseOps.REFERRED_BY_LABEL}=$username")
                { refLink, hasError ->
                    if(hasError) {
                        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        signup_progress_bar.visibility = View.GONE
                        App.firebaseOps.logoutUser()
                        Snackbar.make(signup_button, "Error generating referral link!", Snackbar.LENGTH_SHORT).show()
                    } else
                        App.firebaseOps.createUser(email, password) { task ->
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            signup_progress_bar.visibility = View.GONE

                            if(task.isSuccessful) {
                                val user = SignedUser(username, 0.0, App.firebaseOps.getUserAuth()?.email ?: "", refLink, referredBy)
                                App.firebaseOps.createNewUser(username, user) { userTask ->
                                    if(userTask.isSuccessful) {
                                        App.setSignedUser(user)

                                        Snackbar.make(signup_button, "Created user!", Snackbar.LENGTH_LONG).show()

                                        findNavController().navigate(R.id.action_verify_email)
                                    } else
                                        Snackbar.make(signup_button, "Error creating user!", Snackbar.LENGTH_LONG).show()
                                }
                            } else {
                                if (task.exception is FirebaseAuthUserCollisionException)
                                    Snackbar.make(
                                        signup_button,
                                        "User with this email already exists!",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                else
                                    Snackbar.make(signup_button, "Error creating user!", Snackbar.LENGTH_LONG).show()
                            }
                        }
                }
            }
        }
    }

    private fun validateInputs(username: String, email: CharSequence, password: CharSequence): Boolean {
        var error = false
        //username checks
        if (username.isEmpty()) {
            username_edit_text.error = "Username is required!"
            error = true
        }
        else if (!username.toCharArray().all { it.isLetterOrDigit() || it == '-' || it == '_' }) {
            username_edit_text.error = "Only allowed letters, digits, '-' and '_'"
            error = true
        }

        //email checks
        if (email.isEmpty()) {
            signup_user_email_edit_text.error = "Email is required!"
            error = true
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signup_user_email_edit_text.error = "Please enter a valid email!"
            error = true
        }

        //password checks
        if (password.isEmpty()) {
            signup_user_password_edit_text.error = "Password is required!"
            error = true
        }
        else if (password.length < 6) {
            signup_user_password_edit_text.error = "Choose a password with at least 6 characters!"
            error = true
        }
        return error
    }
}
