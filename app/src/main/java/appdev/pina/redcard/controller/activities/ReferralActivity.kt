package appdev.pina.redcard.controller.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import appdev.pina.redcard.R
import appdev.pina.redcard.firebase.FirebaseOps

class ReferralActivity : AppCompatActivity() {

    var referredBy : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_referral)

        referredBy = intent.extras?.getString(FirebaseOps.REFERRED_BY_LABEL)
    }
}
