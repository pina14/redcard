package appdev.pina.redcard.controller.activities

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import appdev.pina.redcard.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                val deepLink: Uri? = pendingDynamicLinkData?.link

                if (deepLink != null && deepLink.getBooleanQueryParameter(ReferralActivity.REFERRED_BY_LABEL, false)) {
                    //sign out user to receive reward
                    FirebaseAuth.getInstance().signOut()

                    val referredBy = deepLink.getQueryParameter(ReferralActivity.REFERRED_BY_LABEL)

                    val i = Intent(this, ReferralActivity::class.java)
                    i.putExtra(ReferralActivity.REFERRED_BY_LABEL, referredBy)
                    startActivity(i)
                    finish()
                } else
                    goToMain()
            }.addOnFailureListener {
                goToMain()
            }
    }

    private fun goToMain() {
        Handler().postDelayed({
            val i = Intent(this, MainActivity::class.java)
            if(intent.extras != null)
                i.putExtras(intent.extras)
            startActivity(i)
            finish()
        }, 1500)
    }
}
