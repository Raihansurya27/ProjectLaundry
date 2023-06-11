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
import com.raihan.projectlaundry.model.UserModel

class GridUserAdapter(
    private val gridList: List<UserModel>,
    private val context: Context,
    private val callback: GridAdapterCallback
) : BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var phoneNumber: TextView
    private lateinit var username: TextView
    private lateinit var role: TextView
    private lateinit var address: TextView
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
            view = layoutInflater!!.inflate(R.layout.grid_account_item, null, true)
        }
//        homeImage = view!!.findViewById(R.id.homeImage)
        phoneNumber = view!!.findViewById(R.id.PhoneNumber)
        username = view!!.findViewById(R.id.Username)
        address = view!!.findViewById(R.id.Address)
        role = view!!.findViewById(R.id.Role)
        btnDelete = view!!.findViewById(R.id.btnDelete)
        btnEdit = view!!.findViewById(R.id.btnEdit)
        imageView = view!!.findViewById(R.id.imageView)

//        homeImage.setImageResource(gridList.get(position).image)
        phoneNumber.setText(gridList.get(position).phone_number)
        username.setText(gridList.get(position).username)
        address.setText(gridList.get(position).address)
        address.breakStrategy = LineBreaker.BREAK_STRATEGY_SIMPLE
        role.setText(gridList.get(position).role)
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
            callback.onDeleteUser(gridList.get(position).phone_number)
//            showMessage("hallo")
        }

        btnEdit.setOnClickListener {
            callback.onUpdateUser(gridList.get(position).phone_number)
//            Toast.makeText(
//                context,
//                gridList.get(position).username + " telah diedit",
//                Toast.LENGTH_SHORT
//            ).show()
        }

        return view
    }



    fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    interface GridAdapterCallback {
        fun onDeleteUser(phoneNumber: String)
        fun onUpdateUser(phoneNumber: String)
    }
}