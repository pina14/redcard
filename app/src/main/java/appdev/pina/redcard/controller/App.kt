package appdev.pina.redcard.controller

import android.app.Application
import appdev.pina.redcard.firebase.FirebaseOps
import appdev.pina.redcard.model.SignedUser

/**
 * Created by Hugo on 13/11/2018
 */
class App : Application() {

    companion object {
        var signedUser : SignedUser? = null
        val firebaseOps = FirebaseOps()
    }

    override fun onCreate() {
        super.onCreate()

        if(firebaseOps.isUserLoggedIn()) {
            firebaseOps.getUserWithEmail(firebaseOps.getUserAuth()!!.email!!) { userTask ->
                if(userTask.isSuccessful && userTask.result != null) {
                    val docs = userTask.result!!.documents
                    if(docs.isNotEmpty())
                        signedUser = docs[0].toObject(SignedUser::class.java)
                }
                else
                    firebaseOps.logoutUser()
            }
        }
    }
}