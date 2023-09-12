package com.raihan.projectlaundry.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.activitity.ResultActivity
import com.raihan.projectlaundry.model.HistoryModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GridHistoryUserAdapter(private val gridList: List<HistoryModel>, private val context: Context): BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null
    private lateinit var entryCode: TextView
    private lateinit var dateReceive: TextView
    private lateinit var service: TextView
    private lateinit var status: TextView
    private lateinit var detailButton: Button
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

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
            view = layoutInflater!!.inflate(R.layout.grid_history_user_item, null, true)
        }
        entryCode = view!!.findViewById(R.id.entryCode)
        dateReceive = view!!.findViewById(R.id.dateReceive)
        service = view!!.findViewById(R.id.service)
        status = view!!.findViewById(R.id.status)
        detailButton = view!!.findViewById(R.id.buttonDesc)

        val orderDate = gridList.get(position).orderDate
        val result = LocalDate.parse(orderDate.toString(), formatter)
//        val localDate = LocalDate.parse(dateString, formatter)

        entryCode.setText(gridList.get(position).orderId)
        dateReceive.setText(result.toString())
        service.setText(gridList.get(position).serviceName)
        detailButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("orderId",gridList[position].orderId)
            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        return view
    }
}

