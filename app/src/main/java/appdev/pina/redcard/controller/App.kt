package appdev.pina.redcard.controller

import android.app.Application
import appdev.pina.redcard.model.SignedUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Created by Hugo on 13/11/2018
 */
class App : Application() {

    companion object {
        var signedUser : SignedUser? = null
    }

    override fun onCreate() {
        super.onCreate()

        val fAuth = FirebaseAuth.getInstance()
        if(fAuth.currentUser != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("email", fAuth.currentUser!!.email)
                .get().addOnCompleteListener { userTask ->
                    if(userTask.isSuccessful && userTask.result != null) {
                        val docs = userTask.result!!.documents
                        if(docs.isNotEmpty())
                            signedUser = docs[0].toObject(SignedUser::class.java)
                    }
                    else
                        fAuth.signOut()
                }
        }
    }
}