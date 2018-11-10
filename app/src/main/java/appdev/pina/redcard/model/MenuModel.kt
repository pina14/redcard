package appdev.pina.redcard.model

/**
 * Created by Hugo on 06/11/2018
 */
class MenuModel(val name : String, val icon : Int, val isGroup: Boolean, val command : Runnable? = null)