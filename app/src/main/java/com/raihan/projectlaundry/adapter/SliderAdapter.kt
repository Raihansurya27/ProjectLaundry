package com.raihan.projectlaundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.raihan.projectlaundry.R
import com.smarteist.autoimageslider.SliderViewAdapter

// on below line we are creating a class for slider
// adapter and passing our array list to it.
class SliderAdapter(image: IntArray, textTitle: Array<String>, textDesc: Array<String>) :
    SliderViewAdapter<SliderAdapter.SliderViewHolder>() {

    // on below line we are creating a
    // new array list and initializing it.
    var sliderList: IntArray = image

    var sliderTitle: Array<String> = textTitle

    var sliderDesc: Array<String> = textDesc

    // on below line we are calling get method
    override fun getCount(): Int {
        // in this method we are returning
        // the size of our slider list.
        return sliderList.size
    }

    // on below line we are calling on create view holder method.
    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapter.SliderViewHolder {
        // inside this method we are inflating our layout file for our slider view.
        val inflate: View =
            LayoutInflater.from(parent!!.context).inflate(R.layout.slider_item, null)

        // on below line we are simply passing
        // the view to our slider view holder.
        return SliderViewHolder(inflate)
    }

    // on below line we are calling on bind view holder method to set the data to our image view.
    override fun onBindViewHolder(viewHolder: SliderAdapter.SliderViewHolder?, position: Int) {

        // on below line we are checking if the view holder is null or not.
        if (viewHolder != null) {
            // if view holder is not null we are simply
            // loading the image inside our image view using glide library
            Glide.with(viewHolder.itemView).load(sliderList.get(position)).fitCenter()
                .into(viewHolder.imageView)
            viewHolder.textView.setText(sliderTitle.get(position))
            viewHolder.textDesc.setText(sliderDesc.get(position))
        }
    }

    // on below line we are creating a class for slider view holder.
    class SliderViewHolder(itemView: View?) : SliderViewAdapter.ViewHolder(itemView) {

        // on below line we are creating a variable for our
        // image view and initializing it with image id.
        var imageView: ImageView = itemView!!.findViewById(R.id.imageDesc)
        var textView: TextView = itemView!!.findViewById(R.id.textTitle)
        var textDesc: TextView = itemView!!.findViewById(R.id.textDesc)
    }
}