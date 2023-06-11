package com.raihan.projectlaundry.adapter

import android.content.Context
import android.graphics.text.LineBreaker
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.model.ServiceModel
import java.text.NumberFormat
import java.util.Locale

class GridMainAdapter(private val gridList: List<ServiceModel>, private val context: Context): BaseAdapter() {

    private var layoutInflater:LayoutInflater? = null
    private  lateinit var homeImage:ImageView
    private lateinit var homeTitle:TextView
    private lateinit var homePrice:TextView
    private lateinit var homeDesc:TextView
    val localeID = Locale("id", "ID")
    val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(localeID)

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
            view = layoutInflater!!.inflate(R.layout.grid_item, null, true)
        }
        homeImage = view!!.findViewById(R.id.homeImage)
        homeTitle = view!!.findViewById(R.id.homeTitle)
        homePrice = view!!.findViewById(R.id.homePrice)
        homeDesc = view!!.findViewById(R.id.homeDesc)
        homeDesc.breakStrategy = LineBreaker.BREAK_STRATEGY_SIMPLE

        val path_picture =
            "https://laundrynajmi.000webhostapp.com/${gridList.get(position).picture}"
        Glide
            .with(context)
            .load(path_picture)
            .into(homeImage)
        homeTitle.setText(gridList.get(position).name)
        homePrice.setText(currencyFormat.format(gridList.get(position).price.toLong()) +"/${gridList.get(position).per_kg}Kg")
        homeDesc.setText(gridList.get(position).desc)

        return view
    }
}