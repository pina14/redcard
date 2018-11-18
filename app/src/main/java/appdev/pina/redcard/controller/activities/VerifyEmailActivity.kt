package appdev.pina.redcard.controller.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import appdev.pina.redcard.R
import appdev.pina.redcard.controller.App
import kotlinx.android.synthetic.main.activity_verify_email.*

class VerifyEmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)

        if(App.firebaseOps.isUserLoggedOut()) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

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
                    if(isVerified == true) {
                        Intent(this, MainActivity::class.java).also { intent ->
                            startActivity(intent)
                            finish()
                        }
                    }
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
