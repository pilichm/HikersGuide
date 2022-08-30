package pl.pilichm.hikersguide

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import pl.pilichm.hikersguide.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mLocationRequest: LocationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpLocationServices()
    }

    /**
     * Set up location service listeners with high accuracy.
     * */
    private fun setUpLocationServices(){
        mLocationRequest = LocationRequest.create()
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.interval = UPDATE_INTERVAL

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(applicationContext)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        /**
         * Check for permissions and ask if they aren't granted.
         * */
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        /**
         * Set up function called every update interval.
         * */
        getFusedLocationProviderClient(applicationContext)
            .requestLocationUpdates(mLocationRequest!!, object: LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    onLocationChanged(locationResult.lastLocation)
                }
            }, Looper.myLooper())
    }

    /**
     * Function called after location update, it refreshes information displayed to the user.
     * */
    private fun onLocationChanged(location: Location){
        /**
         * Display location info.
         * */
        binding.tvLatitude.text = StringBuilder().append(resources.getString(R.string.tv_latitude))
            .append(resources.getString(R.string.space))
            .append(String.format("%.2f", location.latitude))

        binding.tvLongitude.text = StringBuilder().append(resources.getString(R.string.tv_longitude))
            .append(resources.getString(R.string.space))
            .append(String.format("%.2f", location.longitude))

        binding.tvAccuracy.text = StringBuilder().append(resources.getString(R.string.tv_accuracy))
            .append(resources.getString(R.string.space))
            .append(String.format("%.1f", location.accuracy))

        binding.tvAltitude.text = StringBuilder().append(resources.getString(R.string.tv_altitude))
            .append(resources.getString(R.string.space))
            .append(String.format("%.1f", location.altitude))

        /**
         * Display address info.
         * */
        val geocoder = Geocoder(this, Locale.getDefault())
        val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        if (!address.isNullOrEmpty()){
            binding.tvAddress.text = StringBuilder().append(resources.getString(R.string.tv_address)).append("\n")
                .append(address[0].locality).append("\n").append(address[0].postalCode).append("\n")
                .append(address[0].countryName)
        }
    }

    /**
     * If requested permissions are granted, then sets up location services.
     * */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== LOCATION_PERMISSION_REQUEST_CODE
            && permissions.isNotEmpty()
        ){
            setUpLocationServices()
        }
    }

    /**
     * Constants:
     * Location update interval in ms.
     * Request code for permission request.
     * */
    companion object {
        const val UPDATE_INTERVAL = 1000L
        const val LOCATION_PERMISSION_REQUEST_CODE = 0
    }
}