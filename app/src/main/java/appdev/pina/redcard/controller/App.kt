package appdev.pina.redcard.controller

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import appdev.pina.redcard.firebase.FirebaseOps
import appdev.pina.redcard.model.SignedUser

/**
 * Created by Hugo on 13/11/2018
 */
class App : Application() {

    companion object {
         private val signedUser : MutableLiveData<SignedUser?> = MutableLiveData()

        fun getUserLive() = signedUser
        fun getSignedUser() = signedUser.value
        fun setSignedUser(newUser : SignedUser?) {signedUser.value =  newUser}

        val firebaseOps = FirebaseOps()
    }

    override fun onCreate() {
        super.onCreate()

        if(firebaseOps.isUserLoggedIn())
            firebaseOps.createUserListener(firebaseOps.getUserAuth()?.email ?: "")
    }
}