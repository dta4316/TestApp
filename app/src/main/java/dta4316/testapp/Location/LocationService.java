package dta4316.testapp.Location;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import dta4316.testapp.Command.Command;

public class LocationService {
    private Command.CommandFactory m_MainCommandFactory;
    private Context m_CurrentContext;
    private Location m_LastUpdatedLocation = null;
    private List<Address> m_LastUpdatedAddressList = null;
    private LocationManager m_LocationManager = null;
    private Boolean m_GPSFlag = false;
    private LocationListener m_LocationListener = null;
    private Activity m_Activity = null;

    public LocationService(Activity activity, Context context){
        m_Activity = activity;
        m_CurrentContext = context;
    }

    public void Init(){
        try {
            m_LocationManager = (LocationManager) m_CurrentContext.getSystemService(Context.LOCATION_SERVICE);
            m_LocationListener = new LocationServiceListener();
        }
        catch(Exception e){}
    }

    public Location GetLastUpdatedLocation(){
        return m_LastUpdatedLocation;
    }

    public void SetCommandFactory(Command.CommandFactory commandFactory){
        m_MainCommandFactory = commandFactory;
    }

    public void UpdateLocation() {
        m_GPSFlag = GetGPSPermission();
        if (m_GPSFlag) {
            if (ActivityCompat.checkSelfPermission(m_Activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(m_Activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(m_Activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            m_LocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, m_LocationListener);
        } else {
            m_LastUpdatedLocation = null;
            m_LastUpdatedAddressList = null;
        }
    }

    private Boolean GetGPSPermission() {
        ContentResolver contentResolver = m_CurrentContext.getContentResolver();
        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;
        } else {
            return false;
        }
    }

//    public boolean checkLocationPermission() {
//        String permission = "android.permission.ACCESS_FINE_LOCATION";
//        int res = this.checkCallingOrSelfPermission(permission);
//        return (res == PackageManager.PERMISSION_GRANTED);
//    }

//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 1: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }

//    private void LoadLocation()
//    {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }
//        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        Task task = fusedLocationProviderClient.getLastLocation();
//        task.addOnSuccessListener(new OnSuccessListener() {
//            @Override
//            public void onSuccess(Object o) {
//
//            }
//        });
//    }

    private class LocationServiceListener implements LocationListener {
        public LocationServiceListener(){
        }
        @Override
        public void onLocationChanged(Location location) {
            m_LocationManager.removeUpdates(this);
            m_LastUpdatedLocation = location;
            if(m_LastUpdatedAddressList != null) {
                m_LastUpdatedAddressList.clear();
                m_LastUpdatedAddressList = null;
            }

            Geocoder gcd = new Geocoder(m_CurrentContext, Locale.getDefault());
            try {
                m_LastUpdatedAddressList = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                m_LastUpdatedAddressList = null;
            }

            if(m_MainCommandFactory != null) {
                m_MainCommandFactory.ExecuteCommand("OnLocationChangedCommand");
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
}
