package com.raihan.projectlaundry.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.activitity.ResultActivity
import com.raihan.projectlaundry.model.GridHistoryModel

class GridHistoryAdapter(private val gridList: List<GridHistoryModel>, private val context: Context): BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null
    private lateinit var entryCode: TextView
    private lateinit var dateReceive: TextView
    private lateinit var service: TextView
    private lateinit var detailButton: Button

    override fun getCount(): Int {
        return  gridList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getViewTypeCount(): Int {
        return count
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if(layoutInflater == null){
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (view == null){
            view = layoutInflater!!.inflate(R.layout.grid_history_item, null, true)
        }
        entryCode = view!!.findViewById(R.id.entryCode)
        dateReceive = view!!.findViewById(R.id.dateReceive)
        service = view!!.findViewById(R.id.service)
        detailButton = view!!.findViewById(R.id.buttonDesc)

        entryCode.setText(gridList.get(position).entryCode)
        dateReceive.setText(gridList.get(position).dateReceive)
        service.setText(gridList.get(position).service)
        detailButton.setOnClickListener {
            val intent = Intent(context,ResultActivity::class.java)
            context.startActivity(intent)
        }

        return view
    }
}