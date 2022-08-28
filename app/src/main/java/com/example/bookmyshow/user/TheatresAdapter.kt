package com.example.bookmyshow.user

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmyshow.R
import com.example.bookmyshow.admin.ViewShowActivity
import com.example.bookmyshow.user.TheatresAdapter.*
import com.example.bookmyshow.utils.Show
import com.example.bookmyshow.utils.Theatre
import com.example.bookmyshow.utils.TheatreShow

class TheatresAdapter (private val items: List<TheatreShow>,private val imageUrl : String,
                       private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_time_theater, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = items[position]
        holder.theatreName.text = itemsViewModel.theatre!!.theatreName

        if (itemsViewModel.shows!!.size > 0)
        {
            holder.showOne.visibility = View.VISIBLE;
            holder.showOne.text = itemsViewModel.shows!![0].time;
            holder.showOne.setOnClickListener {
                val intent = Intent(context, PreviewShow::class.java)
                intent.putExtra("show_id",itemsViewModel.showIds!![0])
                intent.putExtra("movie_id",itemsViewModel.shows!![0].movieId)
                intent.putExtra("show_name",itemsViewModel.shows!![0].name)
                intent.putExtra("date",itemsViewModel.shows!![0].date)
                intent.putExtra("time",itemsViewModel.shows!![0].time)
                intent.putExtra("theatre_id",itemsViewModel.shows!![0].theatreId)
                intent.putExtra("movie_image",imageUrl)
                context.startActivity(intent);
            }
        }
        if (itemsViewModel.shows!!.size > 1)
        {
            holder.showTwo.visibility = View.VISIBLE;
            holder.showTwo.text = itemsViewModel.shows!![1].time;
            holder.showTwo.setOnClickListener {
                val intent = Intent(context, PreviewShow::class.java)
                intent.putExtra("show_id",itemsViewModel.showIds!![1])
                intent.putExtra("movie_id",itemsViewModel.shows!![1].movieId)
                intent.putExtra("show_name",itemsViewModel.shows!![1].name)
                intent.putExtra("date",itemsViewModel.shows!![1].date)
                intent.putExtra("time",itemsViewModel.shows!![1].time)
                intent.putExtra("theatre_id",itemsViewModel.shows!![1].theatreId)
                intent.putExtra("movie_image",imageUrl)
                context.startActivity(intent);
            }
        }
        if (itemsViewModel.shows!!.size > 2)
        {
            holder.showThree.visibility = View.VISIBLE;
            holder.showThree.text = itemsViewModel.shows!![2].time;
            holder.showThree.setOnClickListener {
                val intent = Intent(context, PreviewShow::class.java)
                intent.putExtra("show_id",itemsViewModel.showIds!![2])
                intent.putExtra("movie_id",itemsViewModel.shows!![2].movieId)
                intent.putExtra("show_name",itemsViewModel.shows!![2].name)
                intent.putExtra("date",itemsViewModel.shows!![2].date)
                intent.putExtra("time",itemsViewModel.shows!![2].time)
                intent.putExtra("theatre_id",itemsViewModel.shows!![2].theatreId)
                intent.putExtra("movie_image",imageUrl)
                context.startActivity(intent);
            }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder (ItemView: View) : RecyclerView.ViewHolder(ItemView)  {
        val theatreName: TextView = itemView.findViewById(R.id.tvTheaterName)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val showOne: TextView = itemView.findViewById(R.id.tvShowTime1)
        val showTwo: TextView = itemView.findViewById(R.id.tvShowTime2)
        val showThree: TextView = itemView.findViewById(R.id.tvShowTime3)

    }

}