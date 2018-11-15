package appdev.pina.redcard.controller.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import appdev.pina.redcard.R
import appdev.pina.redcard.controller.App
import appdev.pina.redcard.controller.activities.LoginActivity
import appdev.pina.redcard.controller.activities.MainActivity
import appdev.pina.redcard.controller.activities.ReferralActivity
import appdev.pina.redcard.model.SignedUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_signup_form.*

class SignupFormFragment : Fragment() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val reward : Double = 100.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        signup_button.setOnClickListener{
            registerUser()
        }

        login_text.setOnClickListener{
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun registerUser() {
        val username = username_edit_text.text.toString().trim()
        val email = signup_user_email_edit_text.text.toString().trim()
        val password = signup_user_password_edit_text.text.toString().trim()

        if(validateInputs(username, email, password))
            return

        signup_progress_bar.visibility = View.VISIBLE
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        db.collection("users")
            .document(username)
            .get().addOnCompleteListener { docTask ->
                val doc = docTask.result
                if(doc != null && doc.exists()) {
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    signup_progress_bar.visibility = View.GONE
                    Snackbar.make(signup_button, "Username already taken!", Snackbar.LENGTH_LONG).show()
                }
                else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        generateReferral(username) { refLink ->
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            signup_progress_bar.visibility = View.GONE

                            if(task.isSuccessful) {
                                var referredBy : String? = null
                                var balance = 0.0
                                if(activity is ReferralActivity) {
                                    referredBy = (activity as ReferralActivity).referredBy
                                    balance = reward
                                }

                                val user = SignedUser(username, balance, firebaseAuth.currentUser?.email ?: "", refLink, referredBy)
                                App.signedUser = user
                                db.collection("users").document(username).set(user)

                                if(referredBy != null)
                                    distributeRewards(referredBy)

                                Snackbar.make(signup_button, "Created user!", Snackbar.LENGTH_LONG).show()

                                val intent = Intent(context, MainActivity::class.java)
                                startActivity(intent)
                                activity?.finish()
                            }
                            else {
                                if(task.exception is FirebaseAuthUserCollisionException)
                                    Snackbar.make(signup_button, "User with this email already exists!", Snackbar.LENGTH_LONG).show()
                                else
                                    Snackbar.make(signup_button, "Error creating user!", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }.addOnFailureListener {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                signup_progress_bar.visibility = View.GONE
                Snackbar.make(signup_button, "Error creating user!", Snackbar.LENGTH_LONG).show()
            }
    }

    private fun distributeRewards(referredBy: String) {
        val refBy = db.collection("users").document(referredBy)

        refBy.get().addOnCompleteListener { userTask ->
                if(userTask.isSuccessful && userTask.result != null) {
                    val doc = userTask.result
                    if(doc != null) {
                        val user = doc.toObject(SignedUser::class.java)
                        user?.let {
                            refBy.update("balance", user.balance + reward)
                        }
                    }
                }
            }
    }

    private fun generateReferral(username: String, cb : (String) -> Unit) {
        val link = "https://redcard.pina.appdev/?referredBy=$username"
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(link))
            .setDomainUriPrefix("https://redcardapp.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder("redcard.pina.android")
                    .setMinimumVersion(1)
                    .build()
            )
            .buildShortDynamicLink()
            .addOnSuccessListener { shortDynamicLink ->
                cb(shortDynamicLink.shortLink.toString())
            }.addOnFailureListener { ex ->
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                signup_progress_bar.visibility = View.GONE
                firebaseAuth.signOut()
                Snackbar.make(signup_button, "Error generating referral link!", Snackbar.LENGTH_SHORT).show()
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
