package appdev.pina.redcard.controller.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import appdev.pina.redcard.R
import appdev.pina.redcard.controller.App
import appdev.pina.redcard.controller.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.title = "Profile"

        val user = App.signedUser

        if(firebaseAuth.currentUser == null || user == null) {
            firebaseAuth.signOut()
            activity?.startActivity(Intent(context, MainActivity::class.java))
            activity?.finish()
        }

        username_text.text = user?.username
        email_text.text = user?.email
        user_balance_value.text = user?.balance.toString()
    }
}
