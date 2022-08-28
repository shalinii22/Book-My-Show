package com.example.bookmyshow.user

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.bookmyshow.databinding.ActivityUserSignupBinding
import com.example.bookmyshow.utils.Constants
import com.example.bookmyshow.utils.Constants.Companion.COMPANION_DB_USERS
import com.example.bookmyshow.utils.FirebaseUtils
import com.example.bookmyshow.utils.User
import com.google.firebase.auth.FirebaseAuth

class UserSignupActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUserSignupBinding;
    // Action Bar
    private lateinit var actionBar : ActionBar;
    // Progress Dialog
    private  lateinit var progressDialog : ProgressDialog;
    //FirebaseAuth
    private  lateinit var firebaseAuth : FirebaseAuth;

    // Variables
    private var userName = "";
    private var userEmail = "";
    private var userMobile = "";
    private var userPassword = "";
    private var userConfirmPassword = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserSignupBinding.inflate(layoutInflater);
        setContentView(binding.root);

        // Action Bar
        actionBar = supportActionBar!!;
        actionBar.title = "User Signup";
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Progress Bar
        progressDialog = ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Creating new account....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        /// Firebase

        firebaseAuth = FirebaseUtils().firebaseAuth;

        //
        binding.buttonSignUp.setOnClickListener {
            validateData();
        }
        binding.textSingIn.setOnClickListener {
            onBackPressed()
        }
    }
    private fun validateData() {
        userEmail = binding.inputEmail.text.toString().trim();
        userName = binding.inputName.text.toString().trim();
        userMobile = binding.inputMobile.text.toString().trim();
        userPassword = binding.inputPassword.text.toString().trim();
        userConfirmPassword = binding.inputConfirmPassword.text.toString().trim();

        if (TextUtils.isEmpty(userName)) {
            binding.inputName.error = "Please Enter Name";
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
        {
            binding.inputEmail.error = "Invalid Email Format";
        }
        else if (!Patterns.PHONE.matcher(userMobile).matches())
        {
            binding.inputMobile.error = "Invalid Phone Format";
        }
        else if (TextUtils.isEmpty(userPassword)) {
            binding.inputPassword.error = "Please Enter Password";
        }
        else if (userPassword.length < 6 ) {
            binding.inputPassword.error = "Please must be at-least 6 characters long";
        }
        else if (TextUtils.isEmpty(userConfirmPassword)) {
            binding.inputConfirmPassword.error = "Please Enter Confirm Password";
        }
        else if (userPassword != userConfirmPassword) {
            binding.inputConfirmPassword.error = "Password &  Confirm Password must be same";
        }
        else
        {
            firebaseSignup();
        }
    }

    private fun firebaseSignup() {
        progressDialog.show();
        // Create an account
        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnSuccessListener {
                // Success
                val firebaseUser = firebaseAuth.currentUser;
                val email = FirebaseUtils().email;
                val userId = FirebaseUtils().userId.toString();
                val user = User(userId, "User", userName, userEmail, userMobile)

                FirebaseUtils().fireStoreDatabase.collection(COMPANION_DB_USERS).document(userId)
                    .set(user)
                    .addOnSuccessListener {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Account Created with email -  $email", Toast.LENGTH_SHORT).show();
                        startActivity(Intent(this, UserDashboardActivity::class.java));
                        finish();
                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss();
                        Toast.makeText(this, "Sign up Failed due to ${e.message}", Toast.LENGTH_SHORT).show();
                    }
            }
            .addOnFailureListener{ e ->
                progressDialog.dismiss();
                Toast.makeText(this, "Sign up Failed due to ${e.message}", Toast.LENGTH_SHORT).show();
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return super.onSupportNavigateUp()
    }
}