package com.example.bookmyshow.admin

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.bookmyshow.databinding.ActivityAddMovieBinding
import com.example.bookmyshow.utils.Constants
import com.example.bookmyshow.utils.FirebaseUtils
import com.example.bookmyshow.utils.Movie
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class AddMovieActivity : AppCompatActivity() {

    private val pickImage = 100
    private lateinit var binding : ActivityAddMovieBinding
    // Action Bar
    private lateinit var actionBar : ActionBar;
    // Progress Dialog
    private  lateinit var progressDialog : ProgressDialog;
    //FirebaseAuth
    private  lateinit var firebaseAuth : FirebaseAuth;

    private var movieName = "";
    private var movieGenre = "";
    private var movieCategory = "";
    private var movieCast = "";
    private var movieLanguage = "";

    private var theatreId = "";

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddMovieBinding.inflate(layoutInflater);
        setContentView(binding.root);
        // Action Bar
        actionBar = supportActionBar!!;
        actionBar.title = "Add Movie";

        theatreId = intent.getStringExtra("theatre_id").toString()

        // Progress Bar
        progressDialog = ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Adding Movie....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        binding.buttonAddMovie.setOnClickListener {
            addMovie()
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }

        binding.uploadImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(
                    intent,
                    "Select Image from here..."
                ),
                pickImage
            )
        }
    }

    private fun addMovie() {
        movieName = binding.inputMovieName.text.toString().trim()
        movieGenre = binding.inputGenere.text.toString().trim()
        movieCategory = binding.inputCategory.text.toString().trim()
        movieCast = binding.inputStarCast.text.toString().trim()
        movieLanguage = binding.inputLanguage.text.toString().trim()

        if (TextUtils.isEmpty(movieName))
        {
            binding.inputMovieName.error = "Please Enter Movie Name";
        }
        else if (TextUtils.isEmpty(movieGenre))
        {
            binding.inputGenere.error = "Please Enter Genre";
        }
        else if (TextUtils.isEmpty(movieCategory))
        {
            binding.inputCategory.error = "Please Enter Category";
        }
        else if (TextUtils.isEmpty(movieCast))
        {
            binding.inputStarCast.error = "Please Enter Cast";
        }
        else if (TextUtils.isEmpty(movieLanguage))
        {
            binding.inputLanguage.error = "Please Enter Language";
        }
        else if (imageUri == null)
        {
            Toast.makeText(applicationContext, "Please Add Movie Image", Toast.LENGTH_SHORT).show()
        }
        else {
            addImageToStorage();
        }
    }

    private fun addImageToStorage() {
        progressDialog.show()
        if (imageUri != null)
        {
            val storageReference = FirebaseUtils().storageReference;
            val ref = storageReference.child("uploads/" + Constants.COMPANION_DB_MOVIES + "/" +  UUID.randomUUID().toString())
            ref.putFile(imageUri!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener{
                    Log.d("tag",it.toString())
                    var uri =it.toString()
                    addToDatabase(uri)
                }
            }
        }
    }

    private fun addToDatabase(filePath: String) {
        progressDialog.show()
        val userId = FirebaseUtils().userId.toString();
        val movieRef = FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_MOVIES)
        movieRef.get()
            .addOnSuccessListener { documents  ->
                if (documents.isEmpty)
                {
                    progressDialog.dismiss()

                    var theatre_Id : ArrayList<String> = ArrayList<String>();
                    theatre_Id.add(theatreId)

                    val newMovie = Movie(movieName, movieGenre, movieCategory, movieCast, movieLanguage,filePath.toString(),theatre_Id)
                    movieRef.document().set(newMovie)
                    Toast.makeText(applicationContext, "Movie Added", Toast.LENGTH_SHORT).show()
                    finish()
                } else{
                    for (document in documents) {
                        val movie = document.toObject<Movie>();
                        if (movie!!.name != movieName)
                        {
                            progressDialog.dismiss()
                            var theatre_Id : ArrayList<String> = ArrayList<String>();
                            theatre_Id.add(theatreId)
                            val newMovie = Movie(movieName, movieGenre, movieCategory, movieCast, movieLanguage, filePath.toString(), theatre_Id)
                            movieRef.document().set(newMovie);
                            Toast.makeText(applicationContext, "Movie Added", Toast.LENGTH_SHORT).show()
                            finish()
                            break
                        }
                        else {
                            progressDialog.dismiss()
                            movieRef.document(document.id).update("theatre", FieldValue.arrayUnion(theatreId))
                            Toast.makeText(applicationContext, "Error while adding a movie", Toast.LENGTH_SHORT).show()
                            finish()
                            break
                        }
                        Log.d("0000", "${document.id} => ${document.data}")
                    }
                }
            }
            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Error while adding a movie", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            if(data == null || data.data == null){
                return
            }
            imageUri = data?.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                binding.imageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}