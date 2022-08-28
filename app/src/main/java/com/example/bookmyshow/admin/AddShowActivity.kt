package com.example.bookmyshow.admin

import android.app.ProgressDialog
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.bookmyshow.R
import com.example.bookmyshow.databinding.ActivityAddMovieBinding
import com.example.bookmyshow.databinding.ActivityAddShowBinding
import com.example.bookmyshow.utils.Constants
import com.example.bookmyshow.utils.FirebaseUtils
import com.example.bookmyshow.utils.Movie
import com.example.bookmyshow.utils.Show
import com.google.firebase.firestore.ktx.toObject
import java.util.*
import kotlin.collections.ArrayList
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker

class AddShowActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddShowBinding
    // Action Bar
    private lateinit var actionBar : ActionBar;
    // Progress Dialog
    private  lateinit var progressDialog : ProgressDialog;

    private lateinit var moviesList : ArrayList<String>;
    private lateinit var movieIds : ArrayList<String>;
    private var selectedPos = 0;
    private var theatreId = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddShowBinding.inflate(layoutInflater);
        setContentView(binding.root);

        theatreId = intent.getStringExtra("theatre_id").toString()

        moviesList = ArrayList<String>()
        movieIds = ArrayList<String>()
        // Action Bar
        actionBar = supportActionBar!!;
        actionBar.title = "Add Show";

        // Progress Bar
        progressDialog = ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        binding.buttonAddShow.setOnClickListener {
            addShow()
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }

        getMovies()
    }

    private fun getDate(): String {
        val day: Int = binding.datePicker.dayOfMonth
        val month: Int = binding.datePicker.month + 1
        val year: Int = binding.datePicker.year
        return "$day/$month/$year";
    }

    private fun getTime() : String{

        val hour: Int  = binding.timePicker.currentHour;
        val minute: Int  = binding.timePicker.currentMinute;
        return "$hour:$minute";

    }

    private fun addSpinner() {
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, moviesList)
        binding.movieId.adapter = adapter
        binding.movieId.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedPos = position;
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun addShow(){
        progressDialog.setMessage("Adding Show....");
        progressDialog.show()
        val showRef = FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_SHOWS)
        val newShow = Show(movieIds[selectedPos], theatreId,  moviesList[selectedPos], getDate(), getTime());
        showRef.document().set(newShow);
        progressDialog.dismiss()
        finish()
    }

    private fun getMovies() {
        val movieRef = FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_MOVIES)
        movieRef.whereArrayContainsAny("theatre", listOf(theatreId)).get()
            .addOnSuccessListener { documents  ->
                if (documents.isEmpty)
                {
                    binding.moviesLayout.visibility = View.GONE
                    binding.noMoviesLayout.visibility = View.VISIBLE
                    moviesList.add("No Movies")
                    progressDialog.dismiss();
                }
                else{
                    binding.moviesLayout.visibility = View.VISIBLE
                    binding.noMoviesLayout.visibility = View.GONE
                    for (document in documents) {
                        val movie = document.toObject<Movie>();
                        moviesList.add(movie.name)
                        movieIds.add(document.id)
                        progressDialog.dismiss();
                    }
                    addSpinner()
                }
            }
    }
}