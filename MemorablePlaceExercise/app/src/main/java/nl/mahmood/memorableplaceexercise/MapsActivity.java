package nl.mahmood.memorableplaceexercise;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        LocationResultListener,
        GoogleMap.OnMapLongClickListener
{
    private GoogleMap googleMap;
    //handle permission result
    private final int PERMISSION_REQUEST = 1000;
    //handle enable location result
    private final int LOCATION_REQUEST_CODE = 2000;

    private LocationHandler locationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationHandler = new LocationHandler(this, this, LOCATION_REQUEST_CODE, PERMISSION_REQUEST);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
        googleMap.setOnMapLongClickListener(this);
        Intent intent = getIntent();
        int placeNumber = intent.getIntExtra("PlaceNumber", 0);
        if (placeNumber == 0)
        {
            locationHandler.getUserLocation();
        }
        else
        {
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.locations.get(
                    intent.getIntExtra("PlaceNumber", 0)).latitude);
            placeLocation.setLongitude(MainActivity.locations.get(
                    intent.getIntExtra("PlaceNumber", 0)).longitude);
            locationHandler.setCustomLocation(placeLocation, MainActivity.places.get(
                    intent.getIntExtra("PlaceNumber", 0)));
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void getLocation(Location location, String title)
    {
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if(title!=""){
            MarkerOptions marker = new MarkerOptions().position(latLng).title(title);
            googleMap.addMarker(marker);
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST)
        {
            boolean isGranted = true;
            for (int i = 0; i < permissions.length; i++)
            {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                {
                    isGranted = false;
                    break;
                }
            }
            if (!isGranted)
            {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Cannot display location without enabling permission")
                        .setPositiveButton("Ok", (dialog, which) -> locationHandler.getUserLocation())
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
                return;
            }
            locationHandler.getUserLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                locationHandler.getUserLocation();
            }
            else
            {
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Please enable location in order to display it on map")
                        .setPositiveButton("Enable", (dialog, which) -> locationHandler.getUserLocation())
                        .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()))
                        .create()
                        .show();
            }
        }
    }


    @Override
    public void onMapLongClick(LatLng latLng)
    {
        addCity(latLng);
    }

    public void addCity(LatLng latLng)
    {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";

        try
        {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0)
            {
                if (addressList.get(0).getLocality() != null)
                {
                    if (addressList.get(0).getThoroughfare() != null)
                    {
                        if (addressList.get(0).getSubThoroughfare() != null)
                        {
                            address += addressList.get(0).getSubThoroughfare() + " ";
                        }
                        address += addressList.get(0).getThoroughfare() + " ";
                    }
                    address += addressList.get(0).getLocality();
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        if (address.equals(""))
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm yyyy-MM-dd");
            address += simpleDateFormat.format(new Date());
        }

        googleMap.addMarker(new MarkerOptions().position(latLng).title("New Place"));

        MainActivity.places.add(address);
        MainActivity.locations.add(latLng);

        MainActivity.arrayAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Location Saved!", Toast.LENGTH_SHORT).show();
    }
}
