package com.example.bookmyshow.user

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookmyshow.LandingActivity
import com.example.bookmyshow.R
import com.example.bookmyshow.admin.FirestoreMovieAdapter
import com.example.bookmyshow.databinding.ActivityUserDashboardBinding
import com.example.bookmyshow.databinding.ActivityUserSignupBinding
import com.example.bookmyshow.utils.Constants
import com.example.bookmyshow.utils.FirebaseUtils
import com.example.bookmyshow.utils.Movie
import com.example.bookmyshow.utils.UserMovie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject

class UserDashboardActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUserDashboardBinding
    // Action Bar
    private lateinit var actionBar : ActionBar;
    // Progress Dialog
    private  lateinit var progressDialog : ProgressDialog;

    private lateinit var moviesList : ArrayList<UserMovie>;

    private lateinit var moviesAdapter: UserMoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserDashboardBinding.inflate(layoutInflater);
        setContentView(binding.root);
        // Action Bar
        actionBar = supportActionBar!!;
        actionBar.title = "Dashboard";


        moviesList = ArrayList<UserMovie>()

        // Progress Bar
        progressDialog = ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading ....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show()

        binding.recyclerView.layoutManager = LinearLayoutManager(this);

        moviesAdapter = UserMoviesAdapter(moviesList, this);

        binding.recyclerView.adapter = moviesAdapter;

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                moviesAdapter.filter.filter(newText)
                return false
            }
        })
        getMovies()
    }

    private fun getMovies() {

        val movieRef = FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_MOVIES)
        movieRef.get()
            .addOnSuccessListener { documents  ->
                if (documents.isEmpty)
                {
                    progressDialog.dismiss();
                }
                else {
                    for (document in documents) {
                        val movie = document.toObject<Movie>();
                        val id = document.id;
                        val userMovie = UserMovie(id, movie.name, movie.genre, movie.category, movie.cast, movie.languages,movie.imageUri, movie.theatre)
                        moviesList.add(userMovie)
                        progressDialog.dismiss();
                    }
                }
                moviesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss();
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true;
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.logout -> {
                progressDialog.setMessage("Signing Out...")
                FirebaseUtils().firebaseAuth.signOut()
                startActivity(Intent(applicationContext, LandingActivity::class.java));
                finish();
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item)
    }
}