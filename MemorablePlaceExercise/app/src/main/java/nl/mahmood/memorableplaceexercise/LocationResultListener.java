package nl.mahmood.memorableplaceexercise;

import android.location.Location;

public interface LocationResultListener {
    void getLocation(Location location,String title);
}