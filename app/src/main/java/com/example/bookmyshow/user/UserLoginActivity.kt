package com.example.bookmyshow.user

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.bookmyshow.R
import com.example.bookmyshow.databinding.ActivityUserLoginBinding
import com.google.firebase.auth.FirebaseAuth

class UserLoginActivity : AppCompatActivity() {
    // view Binding
    private lateinit var binding: ActivityUserLoginBinding;
    // Action Bar
    private lateinit var actionBar : ActionBar;
    // Progress Dialog
    private  lateinit var progressDialog : ProgressDialog;
    //Firebase
    private  lateinit var firebaseAuth : FirebaseAuth;

    // Variables
    private var email = "";
    private var password = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserLoginBinding.inflate(layoutInflater);
        setContentView(binding.root);

        // Action Bar
        actionBar = supportActionBar!!;
        actionBar.title = "User Login";
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        progressDialog = ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Logging In ....");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        //handle Click, Open Signup Activity
        binding.createNewAccount.setOnClickListener {
            startActivity(Intent(this, UserSignupActivity::class.java));
        }

        binding.buttonSignIn.setOnClickListener {
            validateData();
        }
    }

    private fun validateData() {
        // Get Data
        email = binding.inputEmail.text.toString().trim();
        password = binding.inputPassword.text.toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            binding.inputEmail.error = "Invalid Email Format";
        }
        else if (TextUtils.isEmpty(password)) {
            binding.inputPassword.error = "Please Enter Password";
        }
        else {
            // Data is Valid
            firebaseLogin();
        }
    }

    private fun firebaseLogin() {

        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful)
                {
                    progressDialog.dismiss();
                    val firebaseUser = firebaseAuth.currentUser;
                    var email = firebaseUser!!.email;
                    Toast.makeText(this, "Logged In  as $email", Toast.LENGTH_SHORT).show();
                    startActivity(Intent(this, UserDashboardActivity::class.java));
                    finish();
                }
                else {
                    // Login Failed
                    progressDialog.dismiss();
                    Toast.makeText(this, "Login Failed due to ${it.exception.toString()}", Toast.LENGTH_SHORT).show();

                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss();
                Toast.makeText(this, "Login Failed due to ${e.message}", Toast.LENGTH_SHORT).show();
            }
    }

    private fun checkUser() {
        // if user is already logged in go to Dashboard
        // getCurrentUser
        val firebaseUser = firebaseAuth.currentUser;
        if (firebaseUser != null)
        {
            startActivity(Intent(this, UserDashboardActivity::class.java));
            finish();
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return super.onSupportNavigateUp()
    }
}