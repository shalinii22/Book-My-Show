package com.example.bookmyshow.admin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.bookmyshow.databinding.ActivityAdminSignupBinding
import com.example.bookmyshow.utils.User
import com.example.bookmyshow.utils.Constants
import com.example.bookmyshow.utils.FirebaseUtils
import com.example.bookmyshow.utils.Theatre
import com.google.firebase.auth.FirebaseAuth

class AdminSignupActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAdminSignupBinding;
    // Action Bar
    private lateinit var actionBar : ActionBar;
    // Progress Dialog
    private  lateinit var progressDialog : ProgressDialog;
    //FirebaseAuth
    private  lateinit var firebaseAuth : FirebaseAuth;

    // Variables
    private var theatreName = "";
    private var theatreCapacity = "";
    private var theatreMobile = "";
    private var theatreCiy = "";
    private var theatreZip = "";
    private var password = "";
    private var userName = "";
    private var userEmail = "";
    private var userMobile = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSignupBinding.inflate(layoutInflater);
        setContentView(binding.root);

        // Action Bar
        actionBar = supportActionBar!!;
        actionBar.title = "Admin Signup";
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Progress Bar
        progressDialog = ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Creating new account....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        /// Firebase

        firebaseAuth = FirebaseAuth.getInstance();

        //
        binding.buttonSignUp.setOnClickListener {
            validateData();
        }
        binding.textSingIn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun validateData() {

        theatreName = binding.inputTheatreName.text.toString().trim();
        theatreCapacity = binding.inputCapacity.text.toString().trim();
        theatreMobile = binding.inputMobile.text.toString().trim();
        theatreCiy = binding.inputCity.text.toString().trim();
        theatreZip = binding.inputZipcode.text.toString().trim();
        password = binding.inputPassword.text.toString().trim();

        userName = binding.inputName.text.toString().trim();
        userEmail = binding.inputEmail.text.toString().trim();
        userMobile = binding.inputMobile.text.toString().trim();


        if (TextUtils.isEmpty(theatreName)) {
            binding.inputTheatreName.error = "Please Enter Theatre Name";
        }
        else if (TextUtils.isEmpty(theatreCapacity)) {
            binding.inputCapacity.error = "Please Enter Capacity";
        }
        else if (!Patterns.PHONE.matcher(theatreMobile).matches())
        {
            binding.inputMobile.error = "Invalid Phone Format";
        }
        else if (TextUtils.isEmpty(theatreCiy)) {
            binding.inputCity.error = "Please Enter City";
        }
        else if (TextUtils.isEmpty(theatreZip)) {
            binding.inputZipcode.error = "Please Enter Zip";
        }
        else if (TextUtils.isEmpty(password)) {
            binding.inputPassword.error = "Please Enter Password";
        }
        if (TextUtils.isEmpty(userName)) {
            binding.inputName.error = "Please Enter Name";
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
        {
            binding.inputEmail.error = "Invalid Email Format";
        }
        else if (!Patterns.PHONE.matcher(userMobile).matches())
        {
            binding.inputContactMobile.error = "Invalid Phone Format";
        }
        else
        {
            firebaseSignup();
        }

    }

    private fun firebaseSignup() {
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(userEmail, password)
            .addOnSuccessListener {
                // Success
                val email = FirebaseUtils().email;
                val userId = FirebaseUtils().userId.toString();
                val user = User(userId, "Admin", userName, userEmail, userMobile)
                FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_USERS).document(userId)
                    .set(user)
                    .addOnSuccessListener {
                        val theatre = Theatre(theatreName, theatreCapacity, theatreMobile, theatreCiy, theatreZip);
                        FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_THEATRES).document(userId)
                            .set(theatre)
                            .addOnSuccessListener {
                                progressDialog.dismiss()
                                Toast.makeText(this, "Account Created with email -  $email", Toast.LENGTH_SHORT).show();
                                val intent : Intent = Intent(this, MapsActivity::class.java);
                                intent.putExtra("theatre_id", userId)
                                startActivity(intent);
                                finish();
                            }
                            .addOnFailureListener { e ->
                                progressDialog.dismiss();
                                Toast.makeText(this, "Sign up Failed due to ${e.message}", Toast.LENGTH_SHORT).show();
                            }
                    }
                    .addOnFailureListener { e->
                        progressDialog.dismiss();
                        Toast.makeText(this, "Sign up Failed due to ${e.message}", Toast.LENGTH_SHORT).show();
                    }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss();
                Toast.makeText(this, "Sign up Failed due to ${e.message}", Toast.LENGTH_SHORT).show();
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return super.onSupportNavigateUp()
    }
}