package com.taskapp.presentation.mytaskspage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.taskapp.R
import com.taskapp.domain.Task

class MyTasksRecyclerAdapter(
    private var listGroup: ArrayList<String>,
    private var listChild: HashMap<String, ArrayList<Task>>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return listGroup.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return listChild[listGroup[groupPosition]]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any {
        return listGroup[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return listChild[listGroup[groupPosition]]?.get(childPosition)!!
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_my_tasks_header,
            parent,
            false
        )
        val textView: TextView = view.findViewById(R.id.header_text_view)
        val sGroup: String = getGroup(groupPosition).toString()
        textView.text = sGroup
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_search_task_card,
            parent,
            false
        )
        val sChild: Task = getChild(groupPosition, childPosition) as Task

        val date = sChild.creation_data?.date.toString() + "-" +
                sChild.creation_data?.month.toString() + "-" +
                sChild.creation_data?.year?.plus(1900).toString()

        val title: TextView = view.findViewById(R.id.title_textView)
        val desc: TextView = view.findViewById(R.id.description_text_view)
        val price: TextView = view.findViewById(R.id.price_textView)
        val time: TextView = view.findViewById(R.id.date_text_view)

        title.text = sChild.title
        desc.text = sChild.description
        price.text = sChild.price.toString() + " ₾"
        time.text = date

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

}