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
import com.raihan.projectlaundry.model.ItemModel

class GridItemAdapter (
    private val gridList: List<ItemModel>,
    private val context: Context,
    private val callback: GridAdapterCallback
) : BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var name: TextView
    private lateinit var weight: TextView
    private lateinit var desc: TextView
    lateinit var btnDelete: ImageButton
    lateinit var btnEdit: ImageButton
    lateinit var imageView: ImageView

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
            view = layoutInflater!!.inflate(R.layout.grid_item_item, null, true)
        }
        name = view!!.findViewById(R.id.Name)
        weight = view!!.findViewById(R.id.Weight)
        desc = view!!.findViewById(R.id.Desc)
        btnDelete = view!!.findViewById(R.id.btnDelete)
        btnEdit = view!!.findViewById(R.id.btnEdit)
        imageView = view!!.findViewById(R.id.imageView)

//        homeImage.setImageResource(gridList.get(position).image)
        name.setText(gridList.get(position).name)
        weight.setText(gridList.get(position).weight.toString() + " Kg")
        desc.setText(gridList.get(position).desc)
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
            callback.onDeleteItem(gridList.get(position).item_id)
        }

        btnEdit.setOnClickListener {
            callback.onUpdateItem(gridList.get(position).item_id)
        }

        return view
    }



    fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    interface GridAdapterCallback {
        fun onDeleteItem(itemId: String)
        fun onUpdateItem(itemId: String)
    }
}