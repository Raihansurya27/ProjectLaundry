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
import java.text.NumberFormat
import java.util.Locale

class GridCalculationAdapter(
    private val gridList: List<ItemModel>,
    private val context: Context,
    private val callbacks: GridAdapterCallback
): BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private  lateinit var Image: ImageView
    private lateinit var Title: TextView
    private lateinit var Desc: TextView
    private lateinit var totalItem: TextView
    private lateinit var btnMinus: ImageButton
    private lateinit var btnPlus: ImageButton
    private var itemSum = mutableMapOf<String, Int>()
    val localeID = Locale("id", "ID")
    val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(localeID)

    override fun getCount(): Int {
        return  gridList.size
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

        if(layoutInflater == null){
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (view == null){
            view = layoutInflater!!.inflate(R.layout.grid_calculation_item, null, true)
        }
        Image = view!!.findViewById(R.id.imageView)
        Title = view!!.findViewById(R.id.txtName)
        Desc = view!!.findViewById(R.id.txtDesc)
        Desc.breakStrategy = LineBreaker.BREAK_STRATEGY_SIMPLE
        btnMinus = view!!.findViewById(R.id.btnMinus)
        totalItem = view!!.findViewById(R.id.txtTotal)
        btnPlus = view!!.findViewById(R.id.btnPlus)
        totalItem.text = "0"

        val pathpicture =
            "https://laundrynajmi.000webhostapp.com/${gridList.get(position).picture}"
        Glide
            .with(context)
            .load(pathpicture)
            .into(Image)
        Title.setText(gridList[position].name)
        Desc.setText("Berat: ~${gridList[position].weight}Kg\n${gridList.get(position).desc}")
        Desc.breakStrategy = LineBreaker.BREAK_STRATEGY_SIMPLE

        btnMinus.setOnClickListener {
            substractionItem(gridList[position].item_id)
        }

        btnPlus.setOnClickListener{
            addItem(gridList[position].item_id)
        }

        return view
    }

    fun addItem(itemId:String){
//        val jumlahBarang = itemSum[item] ?: 0
//        itemSum[item] = jumlahBarang + 1
        val sumItem = itemSum[itemId]?: 0
        itemSum[itemId] = sumItem + 1
        totalItem.text = "${itemSum[itemId].toString()}"
    }

    fun substractionItem(itemId:String){
        val sumItem = itemSum[itemId]?: 0
        if (sumItem - 1 > -1){
            itemSum[itemId] = sumItem - 1
            totalItem.text = "${itemSum[itemId].toString()}"
        }
    }

    fun getItemSum(item:List<ItemModel>){
        var message = StringBuilder("Hasil ")
        item.forEach {
//            if (itemSum[it.item_id]!! > -1) {
                message.append("\n ${it.name}: "+itemSum[it.item_id])
//            }
        }
//        showMessage(message.toString())
        callbacks.showResult(message.toString())
    }

    fun Result(){
        getItemSum(gridList)
    }

    fun showMessage(message:String){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
    }

    interface GridAdapterCallback {
        fun onDeleteService(service_id: String)
        fun onUpdateService(service_id: String)
        fun showResult(message:String)
    }

}