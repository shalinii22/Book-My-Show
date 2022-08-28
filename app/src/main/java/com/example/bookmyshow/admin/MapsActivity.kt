package com.example.bookmyshow.admin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.bookmyshow.R

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.bookmyshow.databinding.ActivityMapsBinding
import com.example.bookmyshow.utils.Constants
import com.example.bookmyshow.utils.FirebaseUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private lateinit var binding: ActivityMapsBinding

    private var mMap: GoogleMap? = null
    internal lateinit var mLastLocation: Location
    internal var mCurrLocationMarker: Marker? = null
    internal var mGoogleApiClient: GoogleApiClient? = null
    internal lateinit var mLocationRequest: LocationRequest

    var address : Address? = null;
    private var theatreId = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        theatreId = intent.getStringExtra("theatre_id").toString()

        binding.searchButton.setOnClickListener {
            searchLocation()
        }

        binding.addToDatabase.setOnClickListener {
            if (this.address != null)
            {
                val latlang = hashMapOf(
                    "latitude" to address!!.latitude,
                    "longitude" to address!!.longitude
                )
                FirebaseUtils().fireStoreDatabase.collection(Constants.COMPANION_DB_THEATRES_LOCATION).document(theatreId)
                    .set(latlang);
                startActivity(Intent(applicationContext, AdminDashboardActivity::class.java));
                finish();
            }

        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled = true
            }
        } else {
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true
        }
    }

    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }
        //Place current location marker
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        mCurrLocationMarker = mMap!!.addMarker(markerOptions)

        //move map camera
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.getFusedLocationProviderClient(this)
        }

    }

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    fun searchLocation() {
        var location = binding.editText.text.toString()
        var addressList: List<Address>? = null

        if (location == null || location == "") {
            Toast.makeText(applicationContext,"provide location",Toast.LENGTH_SHORT).show()
        }
        else {
            val geoCoder = Geocoder(this)
            try {
                addressList = geoCoder.getFromLocationName(location, 1)

            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (addressList != null) {
                if (addressList.isNotEmpty()) {
                    address = addressList[0]
                    val latLng = LatLng(address!!.latitude, address!!.longitude)
                    mMap!!.addMarker(MarkerOptions().position(latLng).title(location))
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    Toast.makeText(
                        applicationContext,
                        address!!.latitude.toString() + " " + address!!.longitude,
                        Toast.LENGTH_LONG
                    ).show()
                }
                else
                {
                    Toast.makeText(
                        applicationContext,
                        "Can't Find location",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else
            {
                Toast.makeText(
                    applicationContext,
                    "Please enter valid Address",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}