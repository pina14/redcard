package appdev.pina.redcard.controller.activities

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import appdev.pina.redcard.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

class ReferralActivity : AppCompatActivity() {

    var referredBy : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_referral)

        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                val deepLink: Uri? = pendingDynamicLinkData?.link
                val user = FirebaseAuth.getInstance().currentUser

                if (user == null && deepLink != null && deepLink.getBooleanQueryParameter("referredBy", false)) {
                    referredBy = deepLink.getQueryParameter("referredBy")
                }
            }
    }
}
