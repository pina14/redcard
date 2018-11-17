package appdev.pina.redcard.controller.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import appdev.pina.redcard.R
import appdev.pina.redcard.controller.App
import appdev.pina.redcard.firebase.FirebaseOps

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        App.firebaseOps.getDynamicLink(intent) { deepLink ->
            if (deepLink != null && deepLink.getBooleanQueryParameter(FirebaseOps.REFERRED_BY_LABEL, false)) {
                //sign out user to receive reward
                App.firebaseOps.signOutUser()

                val referredBy = deepLink.getQueryParameter(FirebaseOps.REFERRED_BY_LABEL)

                val i = Intent(this, ReferralActivity::class.java)
                i.putExtra(FirebaseOps.REFERRED_BY_LABEL, referredBy)
                startActivity(i)
                finish()
            } else
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
