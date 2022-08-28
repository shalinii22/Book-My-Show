package com.example.bookmyshow.admin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookmyshow.LandingActivity
import com.example.bookmyshow.R
import com.example.bookmyshow.databinding.ActivityViewMovieBinding
import com.example.bookmyshow.utils.Constants
import com.example.bookmyshow.utils.FirebaseUtils
import com.example.bookmyshow.utils.Movie
import com.example.bookmyshow.utils.Theatre
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseOptions
import com.google.firebase.FirebaseOptions.Builder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject


class ViewMovieActivity : AppCompatActivity() {

    private lateinit var binding : ActivityViewMovieBinding
    // Action Bar
    private lateinit var actionBar : ActionBar;
    // Progress Dialog
    private  lateinit var progressDialog : ProgressDialog;

    private lateinit var movieAdapter: FirestoreMovieAdapter
    private lateinit var moviesList : ArrayList<Movie>;
    private lateinit var movieIds : ArrayList<String>;

    private lateinit var moviesAdapter: FirestoreMovieAdapter
    private var theatreId = "";

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView

    //FirebaseAuth
    private  lateinit var firebaseAuth : FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMovieBinding.inflate(layoutInflater);
        setContentView(binding.root);
        // Action Bar
        actionBar = supportActionBar!!;
        actionBar.title = "Movies";

        /// Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        moviesList = ArrayList<Movie>()
        movieIds = ArrayList<String>()
        // Progress Bar
        progressDialog = ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading Movies....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        theatreId = intent.getStringExtra("theatre_id").toString()

        // Drawer Layout
        drawerLayout = binding.drawerLayout;

        // Pass the ActionBarToggle action into the drawerListener
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)
        // Display the hamburger icon to launch the drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Call syncState() on the action bar so it'll automatically change to the back button when the drawer layout is open
        actionBarToggle.syncState()

        // Call findViewById on the NavigationView
        navView = findViewById(R.id.navView)

        // Call setNavigationItemSelectedListener on the NavigationView to detect when items are clicked
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addMovie -> {
                    val intent : Intent = Intent(this, AddMovieActivity::class.java);
                    intent.putExtra("theatre_id", theatreId)
                    startActivity(intent);
                    if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        this.drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                R.id.addShow -> {
                    val intent : Intent = Intent(this, AddShowActivity::class.java);
                    intent.putExtra("theatre_id", theatreId)
                    startActivity(intent);
                    if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        this.drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    true
                }
                R.id.logout -> {
                    progressDialog.setMessage("Signing Out...")
                    firebaseAuth.signOut()
                    startActivity(Intent(applicationContext, LandingActivity::class.java));
                    finish();
                    Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                    true
                }
                else -> {
                    false
                }
            }
        }


        binding.moviesListRecyclerView.layoutManager = LinearLayoutManager(this);

        moviesAdapter = FirestoreMovieAdapter(moviesList, movieIds, theatreId, this);

        binding.moviesListRecyclerView.adapter = moviesAdapter;
    }

    override fun onResume() {
        super.onResume()
        moviesList.clear();
        getMovies();
    }

//    private fun getTheatreId() {
//        val userId = FirebaseUtils().userId.toString();
//        if (userId != null)
//        {
//            FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_THEATRES).document(userId)
//                .get()
//                .addOnSuccessListener { document  ->
//                    if (document != null) {
//                        val post = document.toObject<Theatre>();
//                        theatreId = document.id;
//                        getMovies();
//                        Log.d("0000", "DocumentSnapshot data: ${post!!.theatreName}")
//                    } else {
//                        binding.moviesListRecyclerView.visibility = View.GONE
//                        binding.noShowsLayout.visibility = View.VISIBLE
//                        progressDialog.dismiss();
//                        Log.d("0000", "No such document")
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    binding.moviesListRecyclerView.visibility = View.GONE
//                    binding.noShowsLayout.visibility = View.VISIBLE
//                    progressDialog.dismiss();
//                    Log.d("0000", "get failed with ", exception)
//                }
//        }
//    }

    private fun getMovies() {
        val movieRef = FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_MOVIES)
        movieRef.whereArrayContainsAny("theatre", listOf(theatreId)).get()
            .addOnSuccessListener { documents  ->
                if (documents.isEmpty)
                {
                    binding.moviesListRecyclerView.visibility = View.GONE
                    binding.noShowsLayout.visibility = View.VISIBLE
                    progressDialog.dismiss();
                }
                else {
                    binding.moviesListRecyclerView.visibility = View.VISIBLE
                    binding.noShowsLayout.visibility = View.GONE
                    for (document in documents) {
                        val movie = document.toObject<Movie>();
                        moviesList.add(movie)
                        movieIds.add(document.id)
                        progressDialog.dismiss();
                    }
                }
                moviesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                binding.moviesListRecyclerView.visibility = View.GONE
                binding.noShowsLayout.visibility = View.VISIBLE
                progressDialog.dismiss();
            }
    }

    // override the onSupportNavigateUp() function to launch the Drawer when the hamburger icon is clicked
    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navView)
        return true
    }

    // override the onBackPressed() function to close the Drawer when the back button is clicked
    override fun onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}