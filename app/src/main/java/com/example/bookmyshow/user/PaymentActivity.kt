package com.example.bookmyshow.user

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import androidx.appcompat.app.ActionBar
import com.example.bookmyshow.R
import com.example.bookmyshow.databinding.ActivityPaymentBinding
import com.example.bookmyshow.utils.CardDetails
import com.example.bookmyshow.utils.Constants
import com.example.bookmyshow.utils.FirebaseUtils
import com.example.bookmyshow.utils.Movie
import java.util.*
import kotlin.concurrent.schedule

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPaymentBinding
    // Action Bar
    private lateinit var actionBar : ActionBar;
    // Progress Dialog
    private  lateinit var progressDialog : ProgressDialog;

    // Variables
    private var cardNumber = "";
    private var cardName = "";
    private var cardMonth = "";
    private var cardYear = "";
    private var cardCVV = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentBinding.inflate(layoutInflater);
        setContentView(binding.root);

        // Action Bar
        actionBar = supportActionBar!!;
        actionBar.title = "Make Payment";
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        progressDialog = ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Checking Card Details ....");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.buttonPay.setOnClickListener {
            validateCard();
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }

    }

    private fun validateCard() {
        // Get Data
        cardNumber = binding.inputCard.text.toString().trim();
        cardName = binding.inputNameOnCard.text.toString().trim();
        cardMonth = binding.inputMonth.text.toString().trim();
        cardYear = binding.inputYear.text.toString().trim();
        cardCVV = binding.inputCvv.text.toString().trim();

        if (TextUtils.isEmpty(cardNumber)) {
            binding.inputCard.error = "Please Enter Valid Card Details";
        }
        else if (cardNumber.length < 16)
        {
            binding.inputCard.error = "Please must be at-least 16 characters long";
        }
        else if (TextUtils.isEmpty(cardName))
        {
            binding.inputNameOnCard.error = "Please Enter Name on the card.";
        }
        else if (TextUtils.isEmpty(cardMonth))
        {
            binding.inputMonth.error = "Please Enter Expiry Month on the card";
        }
        else if (cardMonth.length > 2)
        {
            binding.inputMonth.error = "Month should not be more than numbers";
        }
        else if (TextUtils.isEmpty(cardYear))
        {
            binding.inputYear.error = "Please Enter Expiry Year on the card";
        }
        else if (cardYear.length > 2)
        {
            binding.inputYear.error = "Year should not be more than numbers";
        }
        else if (TextUtils.isEmpty(cardCVV))
        {
            binding.inputCvv.error = "Please Enter CVV";
        }
        else {
            progressDialog.show()
            storeCardDetails();
            Handler().postDelayed({
                makePayment()
            }, 3000)
        }
    }

    private fun storeCardDetails() {
        val cardRef = FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_PAYMENTS)
        val newCard = CardDetails(cardNumber, cardName, cardMonth, cardYear)
        cardRef.document().set(newCard);
    }

    private fun makePayment() {
        val intent = Intent(this, BookingSuccess::class.java)
        startActivity(intent);
        finish()
        progressDialog.dismiss()
    }
}