package com.example.localisation ;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.localisation.R;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private double latitude;
    private double longitude;
    private double altitude;
    private float accuracy;
    private RequestQueue requestQueue;
    private String insertUrl = "http://128.10.3.135/localisation/createPosition.php";
    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Vérifiez les autorisations de localisation ici avant de demander des mises à jour de localisation.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            requestLocationUpdates(locationManager);
        }
        Button btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code pour naviguer vers l'activité Map
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void requestLocationUpdates(LocationManager locationManager) {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 150, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    altitude = location.getAltitude();
                    accuracy = location.getAccuracy();
                    @SuppressLint("StringFormatMatches") String msg = String.format(
                            getResources().getString(R.string.new_location), latitude,
                            longitude, altitude, accuracy);
                    addPosition(latitude, longitude);
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    String newStatus = "";
                    switch (status) {
                        case LocationProvider.OUT_OF_SERVICE:
                            newStatus = "OUT_OF_SERVICE";
                            break;
                        case LocationProvider.TEMPORARILY_UNAVAILABLE:
                            newStatus = "TEMPORARILY_UNAVAILABLE";
                            break;
                        case LocationProvider.AVAILABLE:
                            newStatus = "AVAILABLE";
                            break;
                    }
                    String msg = String.format(getResources().getString(R.string.provider_new_status),
                            provider, newStatus);
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderEnabled(String provider) {
                    String msg = String.format(getResources().getString(R.string.provider_enabled),
                            provider);
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderDisabled(String provider) {
                    String msg = String.format(getResources().getString(R.string.provider_disabled),
                            provider);
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                requestLocationUpdates(locationManager);
            } else {
                // Gérez le cas où l'autorisation de localisation n'a pas été accordée.
            }
        }
    }

    void addPosition(final double lat, final double lon) {
        StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Gérez la réponse du serveur ici
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Gérez les erreurs ici
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                HashMap<String, String> params = new HashMap<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                params.put("latitude", String.valueOf(lat));
                params.put("longitude", String.valueOf(lon));
                params.put("date", sdf.format(new Date()));
                params.put("imei", "12345678");
                return params;
            }
        };
        requestQueue.add(request);
    }
}
