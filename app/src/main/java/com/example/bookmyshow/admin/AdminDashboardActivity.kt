package com.example.bookmyshow.admin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.bookmyshow.LandingActivity
import com.example.bookmyshow.R
import com.example.bookmyshow.databinding.ActivityAdminDashboardBinding
import com.example.bookmyshow.databinding.ActivityUserDashboardBinding
import com.example.bookmyshow.utils.Constants
import com.example.bookmyshow.utils.FirebaseUtils
import com.example.bookmyshow.utils.Theatre
import com.example.bookmyshow.utils.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    // Action Bar
    private lateinit var actionBar : ActionBar;
    // Progress Dialog
    private  lateinit var progressDialog : ProgressDialog;
    //FirebaseAuth
    private  lateinit var firebaseAuth : FirebaseAuth;

    private var theatreId : String = "";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminDashboardBinding.inflate(layoutInflater);
        setContentView(binding.root);
        // Action Bar
        actionBar = supportActionBar!!;
//        actionBar.title = "";

        // Progress Bar
        progressDialog = ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
        /// Firebase
        firebaseAuth = FirebaseAuth.getInstance();

//        binding.buttonAddMovie.setOnClickListener {
//            progressDialog.show()
//            val intent : Intent = Intent(this, AddMovieActivity::class.java);
//            intent.putExtra("theatre_id", theatreId)
//            startActivity(intent);
////            startActivity(Intent(this, AddMovieActivity::class.java));
//        }
//        binding.buttonAddShow.setOnClickListener {
//            progressDialog.show()
//            val intent : Intent = Intent(this, AddShowActivity::class.java);
//            intent.putExtra("theatre_id", theatreId)
//            startActivity(intent);
//
////            startActivity(Intent(this, AddShowActivity::class.java));
//        }
//
//        binding.buttonViewMovie.setOnClickListener {
//            progressDialog.show()
//            val intent : Intent = Intent(this, ViewMovieActivity::class.java);
//            intent.putExtra("theatre_id", theatreId)
//            startActivity(intent);
////            startActivity(Intent(this, ViewMovieActivity::class.java));
//        }

//        binding.buttonAddBanner.setOnClickListener {
////            progressDialog.show()
////            val intent : Intent = Intent(this, AddMovieActivity::class.java);
////            intent.putExtra("theatre_id", theatreId)
////            startActivity(intent);
//        }

        getData();
    }

    private fun getData() {
        val userId = FirebaseUtils().userId.toString();
        if (userId != null)
        {
            FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_THEATRES).document(userId)
                .get()
                .addOnSuccessListener { document  ->
                    if (document != null) {
                        val post = document.toObject<Theatre>();
                        theatreId = document.id;
                        val intent : Intent = Intent(this, ViewMovieActivity::class.java);
                        intent.putExtra("theatre_id", theatreId)
                        startActivity(intent);
                        finish()
                        progressDialog.dismiss();
                    } else {
                        Log.d("0000", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("0000", "get failed with ", exception)
                }
        }
    }

}