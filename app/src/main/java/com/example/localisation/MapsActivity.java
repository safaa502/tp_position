package com.example.localisation;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.localisation.databinding.ActivityMapsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    String showUrl = "http://128.10.3.135/localisation/showPositions.php";
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        JSONObject requestData = new JSONObject(); // Créez un objet JSON pour les données de la requête
        try {
            requestData.put("key", "value"); // Ajoutez des données à votre objet JSON, si nécessaire
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, showUrl, requestData, response -> {
            try {
                JSONArray positions = response.getJSONArray("positions");
                for (int i = 0; i < positions.length(); i++) {
                    JSONObject position = positions.getJSONObject(i);
                    mMap.addMarker(new MarkerOptions().position(new
                            LatLng(position.getDouble("latitude"),
                            position.getDouble("longitude"))).title("Marker"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Gérez les erreurs ici
            }
        });

        requestQueue.add(jsonObjectRequest);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}