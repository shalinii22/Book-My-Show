package com.example.bookmyshow.user

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import com.example.bookmyshow.R
import com.example.bookmyshow.admin.AdminSignupActivity
import com.example.bookmyshow.databinding.ActivityPreviewShowBinding
import com.example.bookmyshow.utils.Constants
import com.example.bookmyshow.utils.FirebaseUtils
import com.example.bookmyshow.utils.Movie
import com.example.bookmyshow.utils.Theatre
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso

class PreviewShow : AppCompatActivity() {

    private lateinit var binding : ActivityPreviewShowBinding
    // Action Bar
    private lateinit var actionBar : ActionBar;
    // Progress Dialog
    private  lateinit var progressDialog : ProgressDialog;

    private var showId = ""
    private var movieId = ""
    private var showName = ""
    private var date = ""
    private var time = ""
    private var theatreId = ""
    private var movieImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreviewShowBinding.inflate(layoutInflater);
        setContentView(binding.root);

        showId = intent.getStringExtra("show_id").toString()
        movieId = intent.getStringExtra("movie_id").toString()
        showName = intent.getStringExtra("show_name").toString()
        date = intent.getStringExtra("date").toString()
        time = intent.getStringExtra("time").toString()
        theatreId = intent.getStringExtra("theatre_id").toString()
        movieImage = intent.getStringExtra("movie_image").toString()

        // Action Bar
        actionBar = supportActionBar!!;
        actionBar.title = "Book A Show";

        // Progress Bar
        progressDialog = ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show()

//        binding.buttonCancel.setOnClickListener {
//            finish()
//        }

        binding.bookNow.setOnClickListener {
            progressDialog.show()
            finish()
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent);
            progressDialog.dismiss()
        }

        Picasso.get().load(movieImage).into(binding.imageView);

        getTheatreDetails()
    }

    private fun getTheatreDetails() {
        FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_THEATRES)
            .whereEqualTo(FieldPath.documentId(), theatreId)
            .get()
            .addOnSuccessListener { documents  ->
                if (documents.isEmpty)
                {
                    progressDialog.dismiss()
                    finish()
                }
                else {
                    progressDialog.dismiss()
                    for (document in documents) {
                        val theatre = document.toObject<Theatre>();
                        val theatreId = document.id;
                        binding.txtTheatreName.text = theatre.theatreName
//                        binding.movieName.text = showName
                        binding.txtWhereToWatch.text = theatre.city
                        binding.txtTime.text = "Date - $date, Time - $time"
                    }
                }
            }
            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                finish()
                Log.d("0000", "get failed with ", exception)
            }
    }

    private fun getShowDetails() {

    }
}