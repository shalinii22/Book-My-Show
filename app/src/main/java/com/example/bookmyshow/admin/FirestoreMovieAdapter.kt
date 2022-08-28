package com.example.bookmyshow.admin

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmyshow.R
import com.example.bookmyshow.user.UserDashboardActivity
import com.example.bookmyshow.utils.Movie
import com.squareup.picasso.Picasso
import java.io.IOException

class FirestoreMovieAdapter(private val items: List<Movie>,
                            private val movieItemsId: List<String>,
                            private val theatreId : String,
                            private val context: Context) : RecyclerView.Adapter<FirestoreMovieAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_movie_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = items[position]
        // sets the text to the textview from our itemHolder class
        holder.movieName.text = itemsViewModel.name
        holder.genre.text = itemsViewModel.genre
        holder.caste.text = itemsViewModel.cast
        holder.movieItem.setOnClickListener {
            val intent = Intent(context,ViewShowActivity::class.java)
            intent.putExtra("movie_id",movieItemsId[position])
            intent.putExtra("movie_name",itemsViewModel.name)
            intent.putExtra("theatreId",theatreId)
            context.startActivity(intent);
        }
        Picasso.get().load(itemsViewModel.imageUri).into(holder.imageView);

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder (ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val movieItem: RelativeLayout = itemView.findViewById(R.id.buzzItemLayoutRelativeLayout)
        val movieName: TextView = itemView.findViewById(R.id.my_movie_name)
        val genre: TextView = itemView.findViewById(R.id.movie_genre)
        val caste: TextView = itemView.findViewById(R.id.movie_cast)
        val imageView: ImageView = itemView.findViewById(R.id.ivBuzzImage)
    }


}