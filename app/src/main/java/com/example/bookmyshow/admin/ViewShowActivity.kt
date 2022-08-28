package com.example.bookmyshow.admin

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookmyshow.R
import com.example.bookmyshow.databinding.ActivityViewShowBinding
import com.example.bookmyshow.utils.Constants
import com.example.bookmyshow.utils.FirebaseUtils
import com.example.bookmyshow.utils.Movie
import com.example.bookmyshow.utils.Show
import com.google.firebase.firestore.ktx.toObject

class ViewShowActivity : AppCompatActivity() {

    private lateinit var binding : ActivityViewShowBinding
    // Action Bar
    private lateinit var actionBar : ActionBar;
    // Progress Dialog
    private  lateinit var progressDialog : ProgressDialog;

    private lateinit var showsList : ArrayList<Show>;

    private lateinit var showsAdapter: ShowsAdapter

    private var movieId = ""
    private var theatreId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewShowBinding.inflate(layoutInflater);
        setContentView(binding.root);

        movieId = intent.getStringExtra("movie_id").toString()
        theatreId = intent.getStringExtra("theatreId").toString()
        val movieName = intent.getStringExtra("movie_name")

        // Action Bar
        actionBar = supportActionBar!!;
        actionBar.title = "Shows";

        showsList = ArrayList<Show>()

        // Progress Bar
        progressDialog = ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading Shows....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        binding.showsListRecyclerView.layoutManager = LinearLayoutManager(this);

        showsAdapter = ShowsAdapter(showsList, this);

        binding.showsListRecyclerView.adapter = showsAdapter;

        getShows()
    }

    private fun getShows() {

        val showsRef = FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_SHOWS)
        showsRef.whereEqualTo("movieId", movieId).whereEqualTo("theatreId", theatreId).get()
            .addOnSuccessListener { documents  ->
                if (documents.isEmpty)
                {
                    binding.showsListRecyclerView.visibility = View.GONE
                    binding.noMoviesLayout.visibility = View.VISIBLE
                    progressDialog.dismiss();
                }
                else{
                    binding.showsListRecyclerView.visibility = View.VISIBLE
                    binding.noMoviesLayout.visibility = View.GONE
                    for (document in documents) {
                        val show = document.toObject<Show>();
                        Log.d("0000", "DocumentSnapshot data: ${show!!.name}")
                        showsList.add(show)
                        progressDialog.dismiss();
                    }
                }
                showsAdapter.notifyDataSetChanged()
            }
    }
}