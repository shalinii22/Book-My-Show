package com.example.bookmyshow

import android.app.ProgressDialog
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import com.example.bookmyshow.admin.AdminDashboardActivity
import com.example.bookmyshow.admin.AdminLoginActivity
import com.example.bookmyshow.admin.MapsActivity
import com.example.bookmyshow.admin.ViewMovieActivity
import com.example.bookmyshow.databinding.ActivityLandingBinding
import com.example.bookmyshow.utils.User
import com.example.bookmyshow.user.UserDashboardActivity
import com.example.bookmyshow.user.UserLoginActivity
import com.example.bookmyshow.utils.Constants
import com.example.bookmyshow.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject

class LandingActivity : AppCompatActivity() {

    // view Binding
    private lateinit var binding: ActivityLandingBinding;
    // Action Bar
    private lateinit var actionBar : ActionBar;

    // Progress Dialog
    private  lateinit var progressDialog : ProgressDialog;
    //Firebase
    private  lateinit var firebaseAuth : FirebaseAuth;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLandingBinding.inflate(layoutInflater);
        setContentView(binding.root);
        // Action Bar
        actionBar = supportActionBar!!;
        actionBar.title = "Book My Show";


        binding.buttonTheatreAdmin.setOnClickListener {
            val intent = Intent(this, AdminLoginActivity::class.java)
            startActivity(intent)
        }
        binding.buttonUserLogin.setOnClickListener {
            val intent = Intent(this, UserLoginActivity::class.java)
            startActivity(intent)
        }
        firebaseAuth = FirebaseAuth.getInstance()

    }

    override fun onStart() {
        super.onStart()
        checkUser();
        Log.d("000000", "onStart")

    }

    private fun checkUser() {
        // if user is already logged in go to Dashboard
        // getCurrentUser
        val firebaseUser = firebaseAuth.currentUser;
        if (firebaseUser != null)
        {
            val userId = firebaseUser!!.uid;

            FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_USERS).document(userId)
                .get()
                .addOnSuccessListener { document  ->
                    if (document != null) {
                        val post = document.toObject<User>();
                        binding.progressCircular.visibility = View.GONE;
                        binding.landingLayout.visibility = View.GONE;
                        if (post!!.type == "User")
                        {
                            startActivity(Intent(applicationContext, UserDashboardActivity::class.java));
                            finish();
                        }
                        else {
                            startActivity(Intent(applicationContext, AdminDashboardActivity::class.java));
                            finish();
                        }
                    } else {
                        binding.progressCircular.visibility = View.GONE;
                        binding.landingLayout.visibility = View.VISIBLE;
                        Log.d("000000", "No such document")
                        firebaseAuth.signOut();
                    }
                }
                .addOnFailureListener { exception ->
                    binding.progressCircular.visibility = View.GONE;
                    binding.landingLayout.visibility = View.VISIBLE;
                    firebaseAuth.signOut();
                    Log.d("000000", "get failed with ", exception)
                }
        }
        else {
            Log.d("000000", "No such document");
            binding.progressCircular.visibility = View.GONE;
            binding.landingLayout.visibility = View.VISIBLE;
            firebaseAuth.signOut();
        }
    }
}
