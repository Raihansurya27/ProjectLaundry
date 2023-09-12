package com.raihan.projectlaundry.adapter

import android.content.Context
import android.graphics.text.LineBreaker
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
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
    private lateinit var totalItem: EditText
//    private lateinit var btnMinus: ImageButton
//    private lateinit var btnPlus: ImageButton
    private var itemSum = mutableMapOf<String, Int>()
    private var itemWeight = mutableMapOf<String, Float>()
    val localeID = Locale("id", "ID")
    val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
    private var totalWeight: Float = 0.0f

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
//        btnMinus = view!!.findViewById(R.id.btnMinus)
        totalItem = view!!.findViewById(R.id.txtTotal)
//        btnPlus = view!!.findViewById(R.id.btnPlus)
//        totalItem.text = "0"

        val pathpicture =
            "https://laundrynajmi.000webhostapp.com/${gridList.get(position).picture}"
        Glide
            .with(context)
            .load(pathpicture)
            .into(Image)
        Title.setText(gridList[position].name)
        Desc.setText("Berat: ~${gridList[position].weight}Kg\n${gridList.get(position).desc}")
        Desc.breakStrategy = LineBreaker.BREAK_STRATEGY_SIMPLE

        totalItem.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() == "" || s.toString().toInt() == 0){
                    getItemSum(gridList[position].item_id, 0)
                    getItemWeight(gridList[position].item_id,0,position)
                }else{
                    getItemSum(gridList[position].item_id, s.toString().trim().toInt())
                    getItemWeight(gridList[position].item_id,s.toString().trim().toInt(),position)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == "" || s.toString().toInt() == 0){
                    getItemSum(gridList[position].item_id, 0)
                    getItemWeight(gridList[position].item_id,0,position)
                }else{
                    getItemSum(gridList[position].item_id, s.toString().trim().toInt())
                    getItemWeight(gridList[position].item_id,s.toString().trim().toInt(),position)
                }
            }

        })

        return view
    }

    fun getItemSum(itemId:String, sum:Int){
//        itemSum[itemId] = sum
        callbacks.getTotalItem(itemId,sum)
//        showMessage(itemSum.toString())
    }

    fun getItemWeight(itemId:String, sum:Int, position: Int){
        itemWeight[itemId] = gridList[position].weight * sum
        callbacks.getTotalWeight(itemWeight.values.sum())
//        showMessage(itemWeight.toString())
        callbacks.total()
    }

    fun showMessage(message:String){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
    }

    interface GridAdapterCallback {
        fun getTotalWeight(sumWeight:Float)
        fun getTotalItem(itemId:String, sum:Int)
        fun getItemDesc(message:String)

        fun total()
    }

}