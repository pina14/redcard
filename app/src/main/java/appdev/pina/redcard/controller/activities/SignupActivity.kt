package appdev.pina.redcard.controller.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns
import android.view.View
import appdev.pina.redcard.R
import appdev.pina.redcard.controller.App
import appdev.pina.redcard.model.SignedUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.content_signup.*
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity(), View.OnClickListener {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        signup_button.setOnClickListener(this)
        login_text.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.signup_button -> {
                registerUser()
            }
            R.id.login_text -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    //TODO store username for user
    private fun registerUser() {
        val username = username_edit_text.text.toString().trim()
        val email = signup_user_email_edit_text.text.toString().trim()
        val password = signup_user_password_edit_text.text.toString().trim()

        if(validateInputs(username, email, password))
            return

        signup_progress_bar.visibility = View.VISIBLE


        db.collection("users")
            .document(username)
            .get().addOnCompleteListener { docTask ->
                val doc = docTask.result
                if(doc == null || doc.exists()) {
                    signup_progress_bar.visibility = View.GONE
                    Snackbar.make(signup_layout, docTask.result!!.data.toString(), Snackbar.LENGTH_LONG).show()
                }
                else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        signup_progress_bar.visibility = View.GONE

                        if(task.isSuccessful) {
                            val user = SignedUser(username, 0.0, firebaseAuth.currentUser?.email ?: "")
                            App.signedUser = user
                            db.collection("users").document(username).set(user)

                            Snackbar.make(signup_layout, "Created user!", Snackbar.LENGTH_LONG).show()
                        }
                        else {
                            if(task.exception is FirebaseAuthUserCollisionException)
                                Snackbar.make(signup_layout, "User with this email already exists!", Snackbar.LENGTH_LONG).show()
                            else
                                Snackbar.make(signup_layout, "Error creating user!", Snackbar.LENGTH_LONG).show()
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
            signup_user_password_edit_text.error = "Email is required!"
            error = true
        }
        else if (password.length < 6) {
            signup_user_password_edit_text.error = "Choose a password with at least 6 characters!"
            error = true
        }
        return error
    }
}
