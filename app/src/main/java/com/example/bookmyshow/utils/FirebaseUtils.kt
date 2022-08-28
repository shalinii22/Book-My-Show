package com.example.bookmyshow.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseUtils {
    val fireStoreDatabase = FirebaseFirestore.getInstance();
    val firebaseAuth = FirebaseAuth.getInstance();
    val firebaseStorage = FirebaseStorage.getInstance();
    val storageReference : StorageReference = firebaseStorage.reference;
    val userId = firebaseAuth.currentUser?.uid;
    val email = firebaseAuth.currentUser?.email;
}