package appdev.pina.redcard.controller.activities
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import appdev.pina.redcard.R
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
