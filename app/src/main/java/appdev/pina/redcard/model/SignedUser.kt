package appdev.pina.redcard.model

/**
 * Created by Hugo on 13/11/2018
 */
class SignedUser(val username : String, var balance : Double, val email : String,
                 val referralLink : String, val referredBy : String?){

    constructor() : this("", 0.0, "", "", null)
}