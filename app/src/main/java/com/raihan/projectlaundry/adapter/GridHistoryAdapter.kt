package com.raihan.projectlaundry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.model.HistoryModel

class GridHistoryAdapter(private val gridList: List<HistoryModel>, private val context: Context): BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null
    private lateinit var entryCode: TextView
    private lateinit var dateReceive: TextView
    private lateinit var service: TextView
//    private lateinit var detailButton: Button
    private lateinit var status:TextView
    private lateinit var phoneNumber:TextView

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
//        detailButton = view!!.findViewById(R.id.buttonDesc)
        status = view!!.findViewById(R.id.status)
//        phoneNumber = view!!.findViewById(R.id.phoneNumber)

        entryCode.setText(gridList.get(position).orderId)


        dateReceive.setText(gridList.get(position).orderDate)
        service.setText(gridList.get(position).serviceName)
        status.setText(gridList[position].status)
//        phoneNumber.setText(gridList[position].phoneNumber)

        return view
    }
}