package com.raihan.projectlaundry.adapter

import android.content.Context
import android.graphics.text.LineBreaker
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.raihan.projectlaundry.R
import com.raihan.projectlaundry.api.ApiClient
import com.raihan.projectlaundry.model.ServiceModel
import java.text.NumberFormat
import java.util.Locale

class GridServiceAdapter (
    private val gridList: List<ServiceModel>,
    private val context: Context,
    private val callback: GridAdapterCallback
) : BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var name: TextView
    private lateinit var price: TextView
    private lateinit var desc: TextView
    lateinit var btnDelete: ImageButton
    lateinit var btnEdit: ImageButton
    lateinit var imageView: ImageView
    val localeID = Locale("id", "ID")
    val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
    val apiService = ApiClient.apiService

    override fun getCount(): Int {
        return gridList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (view == null) {
            view = layoutInflater!!.inflate(R.layout.grid_service_item, null, true)
        }



        name = view!!.findViewById(R.id.Name)
        price = view!!.findViewById(R.id.Price)
        desc = view!!.findViewById(R.id.Desc)
        btnDelete = view!!.findViewById(R.id.btnDelete)
        btnEdit = view!!.findViewById(R.id.btnEdit)
        imageView = view!!.findViewById(R.id.imageView)

//        homeImage.setImageResource(gridList.get(position).image)
        name.setText(gridList.get(position).name)
        price.setText("Rp." + currencyFormat.format(gridList.get(position).price.toLong()) +"/${gridList.get(position).per_kg}Kg")
        desc.setText("${gridList.get(position).desc}")
        desc.breakStrategy = LineBreaker.BREAK_STRATEGY_SIMPLE
        if (gridList.get(position).picture == "") {
            imageView.setImageResource(R.drawable.ic_menu_camera)
        } else {
            val path_picture =
                "https://laundrynajmi.000webhostapp.com/${gridList.get(position).picture}"
            Glide
                .with(context)
                .load(path_picture)
                .into(imageView)
        }

        btnDelete.setOnClickListener {
            callback.onDeleteService(gridList.get(position).service_id)
        }

        btnEdit.setOnClickListener {
            callback.onUpdateService(gridList.get(position).service_id)
        }

        return view
    }



    fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    interface GridAdapterCallback {
        fun onDeleteService(service_id: String)
        fun onUpdateService(service_id: String)
    }
}