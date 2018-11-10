package appdev.pina.redcard.model

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.view.LayoutInflater
import appdev.pina.redcard.R
import kotlinx.android.synthetic.main.drawer_list_child_item.view.*
import kotlinx.android.synthetic.main.drawer_list_group_item.view.*

class DrawerExpandableListListener(
    private val context: Context,
    private val listDataHeader: List<MenuModel>,
    private val listChildData: HashMap<MenuModel, List<MenuModel>>) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = listDataHeader.size

    override fun getChildrenCount(groupPosition: Int): Int {
        return listChildData[listDataHeader[groupPosition]]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int) : MenuModel = listDataHeader[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int) : MenuModel? = listChildData[this.listDataHeader[groupPosition]]?.get(childPosition)

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, cView: View?, parent: ViewGroup): View {
        var convertView = cView
        val header = getGroup(groupPosition)
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.drawer_list_group_item, null)
        }

        if(!header.isGroup)
            convertView?.group_item_expand_icon?.visibility = View.INVISIBLE
        else
            convertView?.group_item_expand_icon?.visibility = View.VISIBLE

        if(isExpanded)
            convertView?.group_item_expand_icon?.setImageResource(R.drawable.arrow_up)
        else
            convertView?.group_item_expand_icon?.setImageResource(R.drawable.arrow_down)

        convertView?.group_item_text?.text = header.name
        return convertView!!
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        cView: View?,
        parent: ViewGroup
    ): View {
        var convertView = cView
        val header = getChild(groupPosition, childPosition)
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.drawer_list_child_item, null)
        }

        convertView?.child_item_text?.text = header?.name
        convertView?.child_item_image?.setImageResource(header?.icon ?: -1)
        return convertView!!
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}
