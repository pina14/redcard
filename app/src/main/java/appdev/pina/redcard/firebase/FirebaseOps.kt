package appdev.pina.redcard.firebase

import android.content.Intent
import android.net.Uri
import appdev.pina.redcard.model.SignedUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

/**
 * Created by Hugo on 17/11/2018
 */
class FirebaseOps {

    companion object {
        const val REFERRED_BY_LABEL = "referredBy"
        const val USERS_COLLECTION = "users"
        const val USER_EMAIL_FIELD = "email"
    }

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val dynamicLinks by lazy { FirebaseDynamicLinks.getInstance() }

    /********************* AUTH OPS *********************/
    /**
     * Sign out current user
     */
    fun signOutUser() = firebaseAuth.signOut()

    /**
     * Check if user is logged in
     */
    fun isUserLoggedIn() : Boolean = firebaseAuth.currentUser != null

    /**
     * Check if user is logged out
     */
    fun isUserLoggedOut() : Boolean = firebaseAuth.currentUser == null

    /**
     * Get current user
     */
    fun getUserAuth() : FirebaseUser? = firebaseAuth.currentUser

    /**
     * Create new user auth
     */
    fun createUser(email: String, password: String, cb : (Task<AuthResult>) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            cb(task)
        }
    }

    /**
     * Create new user auth
     */
    fun loginUser(email: String, password: String, cb : (Task<AuthResult>) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            cb(task)
        }
    }

    /********************* DB OPS *********************/

    /**
     * Get user from firestore DB with given email
     */
    fun getUserWithEmail(email : String, cb : (Task<QuerySnapshot>) -> Unit) {
        db.collection(USERS_COLLECTION)
            .whereEqualTo(USER_EMAIL_FIELD, email)
            .get().addOnCompleteListener { userTask ->
                cb(userTask)
            }
    }

    /**
     * Get user from firestore DB
     */
    fun getUser(username : String, cb : (Task<DocumentSnapshot>) -> Unit) {
        db.collection(USERS_COLLECTION)
            .document(username)
            .get().addOnCompleteListener { docTask ->
                cb(docTask)
            }
    }

    /**
     * Create new user in firestore DB
     */
    fun createNewUser(username: String, user : SignedUser, cb : (Task<Void>) -> Unit) {
        db.collection(USERS_COLLECTION).document(username).set(user).addOnCompleteListener { task ->
            cb(task)
        }
    }

    /********************* DYNAMIC LINKS OPS *********************/
    /**
     * Generate dynamic link
     */
    fun generateDynamicLink(link : String, cb : (String, Boolean) -> Unit) {
        dynamicLinks.createDynamicLink()
            .setLink(Uri.parse(link))
            .setDomainUriPrefix("https://redcardapp.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder().setMinimumVersion(8).build()
            ).buildShortDynamicLink()
            .addOnSuccessListener { shortDynamicLink ->
                cb(shortDynamicLink.shortLink.toString(), false)
            }.addOnFailureListener {
                cb("", true)
            }
    }

    /**
     * Get dynamic link
     */
    fun getDynamicLink(intent : Intent, cb : (Uri?) -> Unit) {
        dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                val deepLink: Uri? = pendingDynamicLinkData?.link

                cb(deepLink)
            }
    }
}