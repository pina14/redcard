package appdev.pina.redcard.model

import appdev.pina.redcard.controller.activities.MainActivity

/**
 * Created by Hugo on 06/11/2018
 */
class MenuModel(val name : String, val icon : Int, val isGroup: Boolean, val cls : Class<*> = MainActivity::class.java)