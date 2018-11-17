package appdev.pina.redcard.controller.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import appdev.pina.redcard.R

class ReferralActivity : AppCompatActivity() {

    var referredBy : String? = null
    companion object {
        const val REFERRED_BY_LABEL = "referredBy"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_referral)

        referredBy = intent.extras?.getString(REFERRED_BY_LABEL)
    }
}
