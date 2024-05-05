package com.eug.swapz.ui.scenes.register


import androidx.compose.runtime.mutableStateOf
import android.location.Location as actualLocation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eug.swapz.AppRoutes
import com.eug.swapz.datasources.SessionDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.content.Context
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class RegisterViewModel (
    private val navController: NavController,
    private val sessionDataSource: SessionDataSource,
    private val context: Context
) : ViewModel() {

    private val _loggedIn = MutableStateFlow(false)
    val loggedIn: StateFlow<Boolean> = _loggedIn

    private var isLoading = mutableStateOf(false)
    private val errorMessage = mutableStateOf("")
    private var currentLocation = MutableStateFlow<actualLocation?>(null)


    fun isLoggedIn() {
        _loggedIn.value = sessionDataSource.isLoggedIn()
    }

    fun signUp(
        email: String,
        password: String,
        username: String,
        name: String,
        photo: String,
    ) {
        viewModelScope.launch {
            isLoading.value = true
            val success = sessionDataSource.signUpUser(email, password, username, name, photo)
            _loggedIn.value = success
            if (!success) {
                isLoading.value = false
                errorMessage.value = "Email already in use"
            } else {
                navigateToLogin()
            }
        }
    }

    fun navigateToLogin() {
        viewModelScope.launch {
            navController.navigate(AppRoutes.LOGIN.value) {
                popUpTo(AppRoutes.REGISTER.value) {
                    inclusive = true
                }
            }
        }
    }
    private fun getCurrentUserId(): String? {
        return sessionDataSource.getCurrentUserId()
    }
    fun setupMap(mapView: MapView) {
        Log.d("Location", "Setting up map...")
        mapView.getMapAsync { googleMap ->
            // Enable zoom controls

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request location permissions from the user
                val permission = Manifest.permission.ACCESS_FINE_LOCATION
                val requestCode = 1
                val activity = context as? Activity
                activity?.let {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(permission),
                        requestCode
                    )
                }
            } else {
                googleMap.isMyLocationEnabled = true

                Log.d("Location", "Location permissions granted")
                // Location permissions are granted, proceed with obtaining the current location
                getCurrentLocation(context) { location ->
                    Log.d("Location", "Received location: $location")
                    // Handle the received location
                    location?.let {
                        Log.d("Location", "Current location value: ${currentLocation.value}")
                        // Perform actions with the obtained location
                        // For example, update the map with the new location
                        updateLocation(location, googleMap)
                    }
                }
            }
        }
    }

    private fun getCurrentLocation(context: Context, onLocationReceived: (actualLocation?) -> Unit) {
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        // Check location permissions
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(context, fineLocationPermission) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, coarseLocationPermission) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocationPermission && hasCoarseLocationPermission) {
            // Create location request
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 5000 // 5 seconds
                fastestInterval = 2000 // 2 seconds
            }
            // Create location callback
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation.let { actualLocation ->
                        onLocationReceived(actualLocation)
                        Log.d("CategoryDetailSceneViewModel", "Received location: $actualLocation")
                        fusedLocationClient.removeLocationUpdates(this) // Detener las actualizaciones de ubicación

                    }
                }
            }
            // Request location updates
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            Log.d("CategoryDetailSceneViewModel", "Requesting location updates...")
            // fusedLocationClient.removeLocationUpdates(locationCallback)


        } else {
            // Request location permissions from the user
            val permission = Manifest.permission.ACCESS_FINE_LOCATION
            val requestCode = 1
            val activity = context as? Activity
            activity?.let {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun updateLocation(actualLocation: actualLocation, googleMap: GoogleMap?) {
        var currentLatLng = LatLng(actualLocation.latitude, actualLocation.longitude)
        val DEFAULT_ZOOM1 = 17f
        // Move the camera to the current location
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM1))
        // Create marker options for current location
        val markerOptions = MarkerOptions()
            .position(currentLatLng)
            .title("Mi ubicación")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        googleMap?.addMarker(markerOptions)
        // Call the addLocation() function and pass the actualLocation parameter
        // Remove previous current location marker
        //_currentLocationMarker.value?.remove()
        currentLocation.value = actualLocation
        Log.d("Location", "Location updated: $currentLocation")

    }

    @Composable
    fun rememberMapViewWithLifecycle(): MapView {
        val mapView = remember {
            MapView(context).apply {
                onCreate(Bundle())
            }
        }

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val lifecycleObserver = object : DefaultLifecycleObserver {
                override fun onResume(owner: LifecycleOwner) {
                    mapView.onResume()
                }

                override fun onPause(owner: LifecycleOwner) {
                    mapView.onPause()
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    mapView.onDestroy()
                }
            }

            lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            }
        }

        return mapView
    }
}