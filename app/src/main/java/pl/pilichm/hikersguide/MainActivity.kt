package pl.pilichm.hikersguide

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient

class MainActivity : AppCompatActivity() {
    private var mLocationRequest: LocationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        Toast.makeText(
            applicationContext,
            "Location: ${location.latitude}",
            Toast.LENGTH_SHORT).show()
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
        const val UPDATE_INTERVAL = 2000L
        const val LOCATION_PERMISSION_REQUEST_CODE = 0
    }
}