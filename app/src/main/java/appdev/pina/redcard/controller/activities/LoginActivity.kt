package appdev.pina.redcard.controller.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns
import android.view.View
import appdev.pina.redcard.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        login_button.setOnClickListener(this)
        signup_text.setOnClickListener(this)

        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.login_button -> {
                signinUser()
            }
            R.id.signup_text -> {
                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun signinUser() {
        val email = user_email_edit_text.text.toString().trim()
        val password = user_password_edit_text.text.toString().trim()

        if(validateInputs(email, password))
            return

        login_progress_bar.visibility = View.VISIBLE

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            login_progress_bar.visibility = View.GONE

            if(task.isSuccessful)
                Snackbar.make(login_layout, "Logged in!", Snackbar.LENGTH_LONG).show()
            else {
                if(task.exception is FirebaseAuthInvalidUserException ||
                    task.exception is FirebaseAuthInvalidCredentialsException)
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
