package com.example.bookmyshow.user

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmyshow.R
import com.example.bookmyshow.admin.FirestoreMovieAdapter
import com.example.bookmyshow.admin.ViewShowActivity
import com.example.bookmyshow.utils.Movie
import com.example.bookmyshow.utils.UserMovie
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class UserMoviesAdapter(private val items: ArrayList<UserMovie>, private val context: Context) : RecyclerView.Adapter<UserMoviesAdapter.ViewHolder>(), Filterable {

    var moviesFilterList = ArrayList<UserMovie>();

    init {
        moviesFilterList = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_movie_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = moviesFilterList[position]
        // sets the text to the textview from our itemHolder class
        holder.movieName.text = itemsViewModel.name
        holder.genre.text = itemsViewModel.genre
        holder.caste.text = itemsViewModel.cast

        holder.movieItem.setOnClickListener {
            val intent = Intent(context, ShowTheatresActivity::class.java)
            intent.putExtra("movie_id",itemsViewModel.id)
            intent.putExtra("movie_name",itemsViewModel.name)
            intent.putExtra("movie_image",itemsViewModel.imageUri)
            context.startActivity(intent);
        }

        Picasso.get().load(itemsViewModel.imageUri).into(holder.imageView);
    }

    override fun getItemCount(): Int {
        return moviesFilterList.size
    }


    class ViewHolder (ItemView: View) : RecyclerView.ViewHolder(ItemView)  {
        val movieItem: RelativeLayout = itemView.findViewById(R.id.buzzItemLayoutRelativeLayout)
        val movieName: TextView = itemView.findViewById(R.id.my_movie_name)
        val genre: TextView = itemView.findViewById(R.id.movie_genre)
        val caste: TextView = itemView.findViewById(R.id.movie_cast)
        val imageView: ImageView = itemView.findViewById(R.id.ivBuzzImage)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    moviesFilterList = items
                } else {
                    val resultList = ArrayList<UserMovie>()
                    for (row in items) {
                        if (row.name.lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))
                            || row.genre.lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))
                            || row.cast.lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))
                            || row.category.lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    moviesFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = moviesFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                moviesFilterList = results?.values as ArrayList<UserMovie>
                notifyDataSetChanged()
            }
        }
    }

}