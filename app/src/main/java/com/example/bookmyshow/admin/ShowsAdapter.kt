package com.example.bookmyshow.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmyshow.R
import com.example.bookmyshow.utils.Movie
import com.example.bookmyshow.utils.Show

class ShowsAdapter (private val shows: List<Show>, private val context: Context) : RecyclerView.Adapter<ShowsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_time_theater, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = shows[position]
        // sets the text to the textview from our itemHolder class
        holder.showName.text = ItemsViewModel.name

        holder.showTime.visibility = View.VISIBLE
        holder.showTime.text = ItemsViewModel.time

        holder.date.visibility = View.VISIBLE
        holder.date.text = ItemsViewModel.date

    }

    override fun getItemCount(): Int {
        return shows.size
    }


    class ViewHolder (ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val showName: TextView = itemView.findViewById(R.id.tvTheaterName)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val showTime: TextView = itemView.findViewById(R.id.tvShowTime1)
    }

}