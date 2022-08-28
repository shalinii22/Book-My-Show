package com.example.bookmyshow.utils

class TheatreShow(
    val movieId : String? = "",
    val theatreId : String? = "",
    val theatre: Theatre? = null ,
    val shows : ArrayList<Show>? = ArrayList<Show>(),
    val showIds : ArrayList<String>? = ArrayList<String>()
) {
}