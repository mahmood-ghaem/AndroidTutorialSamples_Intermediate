package nl.mahmood.mapsclusteringdemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.mahmood.mapsclusteringdemo.model.MyClusterItem;

import static nl.mahmood.mapsclusteringdemo.utils.JSONHandler.readAssets;

/**
 * In this tutorial
 *      1- we cluster map markers.
 *      2- customize markers and clusters by DefaultClusterRenderer.
 *      3- set click on cluster and put markers in a listView inside a dialog.
 *      4- read a JSON file in asset folder.
 * TODO: Before you run your application, you need a Google Maps API key.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap googleMap;
    private ClusterManager<MyClusterItem> clusterManager;
    private List<MarkerOptions> listMarkers = new ArrayList<>();

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
        clusterManager = new ClusterManager<>(this, googleMap);
        addMarkers();
        CustomClusterRenderer renderer = new CustomClusterRenderer(this, googleMap, clusterManager);
        clusterManager.setRenderer(renderer);
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
        clusterManager.cluster();

        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyClusterItem>()
        {
            @Override
            public boolean onClusterClick(Cluster<MyClusterItem> cluster)
            {
                showClusterItemsDialog();
                final Collection<MyClusterItem> listItems = cluster.getItems();
                final List<String> listNames = new ArrayList<>();
                for (MyClusterItem item : listItems)
                {
                    listNames.add(item.getTitle());
                }

                ListView listViewClusterItems = alertDialog.findViewById(R.id.listViewClusterItems);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                        (MapsActivity.this, android.R.layout.simple_list_item_1, listNames);
                listViewClusterItems.setAdapter(arrayAdapter);

                listViewClusterItems.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        Toast.makeText(MapsActivity.this, listNames.get(position), Toast.LENGTH_SHORT).show();
                    }
                });
                
                return true;
            }

        });
    }


    private void addMarkers()
    {
        JSONArray jsonArray = readAssets(this, "data.json");
        MyClusterItem myClusterItem = null;
        try
        {
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("places");
                double lat = jsonObject.getDouble("latitude");
                double lng = jsonObject.getDouble("longitude");
                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(name);
                listMarkers.add(markerOptions);
                myClusterItem = new MyClusterItem(lat, lng, name, "");
                clusterManager.addItem(myClusterItem);
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myClusterItem.getPosition(), 5));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void showClusterItemsDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.cluster_items_dialog, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }

        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle("Cluster Items");
        alertDialog.show();
    }
}
