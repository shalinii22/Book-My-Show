package com.example.bookmyshow.user

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookmyshow.R
import com.example.bookmyshow.admin.FirestoreMovieAdapter
import com.example.bookmyshow.databinding.ActivityShowTheatresBinding
import com.example.bookmyshow.utils.*
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.toObject

class ShowTheatresActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowTheatresBinding
    // Action Bar
    private lateinit var actionBar : ActionBar;
    // Progress Dialog
    private  lateinit var progressDialog : ProgressDialog;

    private lateinit var theatresAdapter : TheatresAdapter;

    private var theatres = ArrayList<String>()
    private var theatreShows = ArrayList<TheatreShow>()

    private var movieId = "";
    private var movieName = "";
    private var movieImage = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowTheatresBinding.inflate(layoutInflater);
        setContentView(binding.root);

        movieId = intent.getStringExtra("movie_id").toString()
        movieName = intent.getStringExtra("movie_name").toString()
        movieImage = intent.getStringExtra("movie_image").toString()

        // Action Bar
        actionBar = supportActionBar!!;
        actionBar.title = "$movieName";

        // Progress Bar
        progressDialog = ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        theatres = ArrayList<String>()

        theatreShows = ArrayList<TheatreShow>()

        progressDialog.show()

        getTheatres()


        binding.theatresListRecyclerView.layoutManager = LinearLayoutManager(this);

        theatresAdapter = TheatresAdapter(theatreShows, movieImage,  this);

        binding.theatresListRecyclerView.adapter = theatresAdapter;
//

    }

    private fun getTheatres() {
        val movieRef = FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_MOVIES)
        movieRef.document(movieId).get()
            .addOnSuccessListener { movieDocument ->
                val movie = movieDocument.toObject<Movie>();
                theatres = movie!!.theatre;
                val theatreRef = FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_THEATRES)
                theatreRef.whereIn(FieldPath.documentId(), theatres).get()
                    .addOnSuccessListener { theatreDocuments ->
                        if (theatreDocuments.isEmpty)
                        {
                            binding.theatresListRecyclerView.visibility = View.GONE
                            binding.noTheatresLayout.visibility = View.VISIBLE
                            progressDialog.dismiss()
                            Log.d("0000", "empty ")
                        }
                        else {
                            binding.theatresListRecyclerView.visibility = View.VISIBLE
                            binding.noTheatresLayout.visibility = View.GONE
                            for (document in theatreDocuments) {
                                val theatre = document.toObject<Theatre>();
                                val theatreId = document.id;
                                Log.d("0000", "get theatreRef with ${theatre.theatreName} ")
                                progressDialog.dismiss()
                                val thisTheatreShows : ArrayList<Show> = ArrayList<Show>()
                                val thisTheatreShowIds : ArrayList<String> = ArrayList<String>()

                                val showsRef = FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_SHOWS)
                                showsRef.whereEqualTo("movieId", movieId).whereEqualTo("theatreId",theatreId).get()
                                    .addOnSuccessListener { documents  ->
                                        if (documents.isEmpty)
                                        {
                                            binding.theatresListRecyclerView.visibility = View.GONE
                                            binding.noTheatresLayout.visibility = View.VISIBLE
                                            progressDialog.dismiss();
                                        }
                                        else{
                                            binding.theatresListRecyclerView.visibility = View.VISIBLE
                                            binding.noTheatresLayout.visibility = View.GONE
                                            for (document in documents) {
                                                val show = document.toObject<Show>();
                                                Log.d("0000", "DocumentSnapshot data: ${show!!.name}")
                                                thisTheatreShows.add(show)
                                                thisTheatreShowIds.add(document.id)
                                                progressDialog.dismiss();
                                            }
                                        }
                                        val theatreShow = TheatreShow(movieId, theatreId, theatre, thisTheatreShows, thisTheatreShowIds)
                                        theatreShows.add(theatreShow)
                                        theatresAdapter.notifyDataSetChanged()
                                    }
                                    .addOnFailureListener { showExp ->
                                        Log.d("0000", "get showExp failed with ", showExp )
                                    }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        binding.theatresListRecyclerView.visibility = View.GONE
                        binding.noTheatresLayout.visibility = View.VISIBLE
                        progressDialog.dismiss()
                        Log.d("0000", "get theatreRef failed with ", e)
                    }
            }
            .addOnFailureListener { e ->
                binding.theatresListRecyclerView.visibility = View.GONE
                binding.noTheatresLayout.visibility = View.VISIBLE
                progressDialog.dismiss()
                Log.d("0000", "get movieDocument failed with ", e)
            }
    }
}