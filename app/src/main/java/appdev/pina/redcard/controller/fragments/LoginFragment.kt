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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.title = getString(R.string.title_login)


        login_button.setOnClickListener{
            signinUser()
        }

        signup_text.setOnClickListener{
            findNavController().navigate(R.id.action_login_to_signup)
        }
    }

    private fun signinUser() {
        val email = user_email_edit_text.text.toString().trim()
        val password = user_password_edit_text.text.toString().trim()

        if(validateInputs(email, password))
            return

        login_progress_bar.visibility = View.VISIBLE
        activity?.apply {window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)}


        App.firebaseOps.loginUser(email, password) { task ->
            login_progress_bar.visibility = View.GONE
            activity?.apply {window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)}

            if(task.isSuccessful) {
                if(task.result?.user?.isEmailVerified == false)
                    findNavController().navigate(R.id.action_verify_email)
                else
                    findNavController().navigate(R.id.action_show_profile)
            }
            else {
                if(task.exception is FirebaseAuthInvalidUserException ||
                    task.exception is FirebaseAuthInvalidCredentialsException
                )
                    Snackbar.make(login_layout, "Wrong credentials!", Snackbar.LENGTH_LONG).show()
                else
                    Snackbar.make(login_layout, "Error logging in!", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun validateInputs(email: CharSequence, password: CharSequence): Boolean {
        var error = false

        if (email.isEmpty()) {
            user_email_edit_text.error = "Email is required!"
            error = true
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            user_email_edit_text.error = "Please enter a valid email!"
            error = true
        }

        //password checks
        if (password.isEmpty()) {
            user_password_edit_text.error = "Email is required!"
            error = true
        }
        else if (password.length < 6) {
            user_password_edit_text.error = "The password is at least 6 characters!"
            error = true
        }
        return error
    }
}
