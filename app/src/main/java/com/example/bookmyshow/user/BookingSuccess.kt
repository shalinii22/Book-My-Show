package com.example.bookmyshow.user

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.bookmyshow.LandingActivity
import com.example.bookmyshow.R
import android.graphics.BitmapFactory
import android.graphics.Color
import android.widget.Toast
import com.example.bookmyshow.databinding.ActivityBookingSuccessBinding

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class BookingSuccess : AppCompatActivity() {
    private lateinit var binding : ActivityBookingSuccessBinding
    // Action Bar
    private lateinit var actionBar : ActionBar;
    // Progress Dialog
    private  lateinit var progressDialog : ProgressDialog;

    private val CHANNEL_ID = "simple_notification"
    private val NOTIFICATION_ID = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityBookingSuccessBinding.inflate(layoutInflater);
        setContentView(binding.root);

        // Action Bar
        actionBar = supportActionBar!!;
        actionBar.title = "";

        // Progress Bar
        progressDialog = ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        binding.okButton.setOnClickListener {
            finish()
        }

        DisplayNotification();
    }

    fun DisplayNotification() {
        createNotificationChannel()
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_menu_camera)
        builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_menu_camera))
        builder.setContentTitle("Successful")
        builder.setContentText("Your booking is successful")
        builder.setAutoCancel(true)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
    }

    //create notification channel if you target android 8.0 or higher version
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Simple Notification"
            val description = "Include all the simple notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationChannel.description = description
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}