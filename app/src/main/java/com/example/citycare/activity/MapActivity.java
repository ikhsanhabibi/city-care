package com.example.citycare.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.citycare.BuildConfig;
import com.example.citycare.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private static final int REQUEST_LOCATION = 1;
    double latitude;
    double longitude;
    private LocationManager locationManager;
    private Marker startMarker;

    private MapView map = null;
    private Context ctx;
    private MyLocationNewOverlay myLocationNewOverlay;
    private ImageButton btCenterMap;
    private IMapController mapController;
    private GpsMyLocationProvider provider;
    private Overlay touchOverlay;
    private Button set_location;
    private TextView address;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = getIntent();
        String previousActivity = intent.getStringExtra("FROM_ACTIVITY");
        if (previousActivity.equals("COMPLAINT")) {
            Intent newIntent = new Intent(this, ComplaintFormActivity.class);
            startActivity(newIntent);
            finish();
        } else {
            Intent newIntent = new Intent(this, SuggestionFormActivity.class);
            startActivity(newIntent);
            finish();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ctx = MapActivity.this;

        // Map Controller
        map = findViewById(R.id.map);
        openMap();
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        // Set location
        set_location = findViewById(R.id.setLocation);
        address = findViewById(R.id.address);

        set_location = findViewById(R.id.setLocation);
        set_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String previousActivity = intent.getStringExtra("FROM_ACTIVITY");
                if (previousActivity.equals("COMPLAINT")) {
                    Intent c = new Intent(getApplicationContext(), ComplaintFormActivity.class);
                    c.putExtra("address", address.getText());
                    c.putExtra("latitude", latitude);
                    c.putExtra("longitude", longitude);
                    startActivity(c);
                    finish();
                } else {
                    Intent s = new Intent(getApplicationContext(), SuggestionFormActivity.class);
                    s.putExtra("address", address.getText());
                    s.putExtra("latitude", latitude);
                    s.putExtra("longitude", longitude);
                    startActivity(s);
                    finish();
                }

            }
        });

        // Go back to default location
        btCenterMap = findViewById(R.id.ic_center_map);
        btCenterMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToDefaultLocation();
            }
        });


        // Saved Preferences
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        this.latitude = Double.parseDouble(sharedPreferences.getString("lat", "0"));
        this.longitude = Double.parseDouble(sharedPreferences.getString("longi", "0"));
        String lastAddress = sharedPreferences.getString("address", "");

        // GPS settings
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGPS();
        } else {
            if (latitude != 0 && longitude != 0) {
                showLastLocation(latitude, longitude);
                address.setText(lastAddress);
            } else {
                getLocation();
                myNewLocationOverlay();
            }

        }

        // Overlay map icon
        touchOverlay();
        map.getOverlays().add(touchOverlay);

    }

    public void onResume() {
        super.onResume();
        map.onResume();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
    }

    public void onPause() {
        super.onPause();
        map.onPause();
        SavePreferences("lat", String.valueOf(latitude));
        SavePreferences("longi", String.valueOf(longitude));
        SavePreferences("address", address.getText().toString());
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                // Log.w(TAG, strReturnedAddress.toString());
            } else {
                strAdd = getResources().getString(R.string.finding_location);
                Log.w(TAG, "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            strAdd = getResources().getString(R.string.finding_location);
            Log.w(TAG, "Can not get Address!");
        }
        return strAdd.trim();
    }

    private void onGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.enable_gps)).setCancelable(false).setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                ActivityCompat.requestPermissions((Activity) ctx,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                latitude = locationGPS.getLatitude();
                longitude = locationGPS.getLongitude();
                address.setText(getCompleteAddressString(latitude, longitude));
            } else {
                //Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        mapController = map.getController();
        mapController.setZoom(18.5);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setScrollableAreaLimitDouble(new BoundingBox(85, 180, -85, -180));
        map.setMaxZoomLevel(20.0);
        map.setMinZoomLevel(4.0);
        map.setHorizontalMapRepetitionEnabled(false);
        map.setVerticalMapRepetitionEnabled(false);
        map.setScrollableAreaLimitLatitude(MapView.getTileSystem().getMaxLatitude(), MapView.getTileSystem().getMinLatitude(), 0);
        map.setMultiTouchControls(true);

        map.getTileProvider().getTileCache().getProtectedTileComputers().clear();
        map.getTileProvider().getTileCache().setAutoEnsureCapacity(false);
    }

    private void myNewLocationOverlay() {
        provider = new GpsMyLocationProvider(ctx);
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        myLocationNewOverlay = new MyLocationNewOverlay(provider, map);
        myLocationNewOverlay.enableMyLocation();
        myLocationNewOverlay.enableFollowLocation();

        Bitmap bitmapNotMoving = BitmapFactory.decodeResource(getResources(), R.drawable.map_red);
        Bitmap bitmapMoving = BitmapFactory.decodeResource(getResources(), R.drawable.map_red);
        myLocationNewOverlay.setDirectionArrow(bitmapNotMoving, bitmapMoving);

        map.getOverlays().add(myLocationNewOverlay);
    }

    private void goBackToDefaultLocation() {
        map.getOverlays().clear();

        provider = new GpsMyLocationProvider(ctx);
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        myLocationNewOverlay = new MyLocationNewOverlay(map);
        myLocationNewOverlay.enableMyLocation();
        myLocationNewOverlay.enableFollowLocation();
        mapController.setZoom(18.5);

        Bitmap bitmapNotMoving = BitmapFactory.decodeResource(getResources(), R.drawable.map_red);
        Bitmap bitmapMoving = BitmapFactory.decodeResource(getResources(), R.drawable.map_red);
        myLocationNewOverlay.setDirectionArrow(bitmapNotMoving, bitmapMoving);

        map.getOverlays().add(myLocationNewOverlay);
        map.getOverlays().add(touchOverlay);
        map.invalidate();

        getLocation();
    }

    private void touchOverlay() {
        touchOverlay = new Overlay() {
            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;
            Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.map_red, null);
            Marker secondMarker = new Marker(map);

            @Override
            public void draw(Canvas arg0, MapView arg1, boolean arg2) {
            }

            @Override
            public boolean onScroll(MotionEvent pEvent1, MotionEvent pEvent2, float pDistanceX, float pDistanceY, final MapView pMapView) {
                latitude = pMapView.getMapCenter().getLatitude();
                longitude = pMapView.getMapCenter().getLongitude();

                if (myLocationNewOverlay != null) {
                    pMapView.getOverlays().remove(anotherItemizedIconOverlay);
                    map.getOverlays().remove(myLocationNewOverlay);
                    map.getOverlays().add(secondMarker);

                    secondMarker.setIcon(icon);
                    secondMarker.setPosition(new GeoPoint((float) latitude,
                            (float) longitude));

                } else if (anotherItemizedIconOverlay != null) {
                    pMapView.getOverlays().remove(anotherItemizedIconOverlay);

                    secondMarker.setIcon(icon);
                    secondMarker.setPosition(new GeoPoint((float) latitude,
                            (float) longitude));

                    secondMarker.setTitle(getCompleteAddressString(pMapView.getMapCenter().getLatitude(), pMapView.getMapCenter().getLongitude()));

                    secondMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker, MapView mapView) {
                            return false;
                        }
                    });

                } else {
                    map.getOverlays().remove(myLocationNewOverlay);
                    map.getOverlays().add(secondMarker);

                    secondMarker.setIcon(icon);
                    secondMarker.setPosition(new GeoPoint((float) latitude,
                            (float) longitude));
                }

                address.setText(getCompleteAddressString(pMapView.getMapCenter().getLatitude(), pMapView.getMapCenter().getLongitude()));


                return super.onScroll(pEvent1, pEvent2, pDistanceX, pDistanceY, pMapView);
            }

            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {
                mapView.getOverlays().remove(myLocationNewOverlay);
                mapView.getOverlays().remove(anotherItemizedIconOverlay);
                if (startMarker != null) {
                    map.getOverlays().remove(startMarker);
                }

                Projection proj = map.getProjection();
                GeoPoint loc = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());
                longitude = loc.getLongitude();
                latitude = loc.getLatitude();

                ArrayList<OverlayItem> overlayArray = new ArrayList<OverlayItem>();
                OverlayItem mapItem = new OverlayItem("", "", new GeoPoint(loc.getLatitude(), loc.getLongitude()));

                address.setText(getCompleteAddressString(latitude, longitude));

                mapItem.setMarker(icon);
                secondMarker.setIcon(icon);
                secondMarker.setTitle(getCompleteAddressString(loc.getLatitude(), loc.getLongitude()));
                secondMarker.setPosition(loc);
                secondMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                secondMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        return false;
                    }
                });

                map.getOverlays().add(secondMarker);
                overlayArray.add(mapItem);

                if (anotherItemizedIconOverlay == null) {
                    anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getApplicationContext(), overlayArray, null);
                    mapView.getOverlays().add(anotherItemizedIconOverlay);
                    mapView.invalidate();

                } else {
                    mapView.getOverlays().remove(anotherItemizedIconOverlay);
                    mapView.invalidate();
                    anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getApplicationContext(), overlayArray, null);
                    mapView.getOverlays().add(anotherItemizedIconOverlay);
                }

                return true;
            }

        };
    }

    private void SavePreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SavePreferences("lat", String.valueOf(latitude));
        SavePreferences("longi", String.valueOf(longitude));
        SavePreferences("address", address.getText().toString());
    }

    public void showLastLocation(double lat, double longi) {
        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.map_red, null);
        GeoPoint myPosition = new GeoPoint(lat, longi);

        startMarker = new Marker(map);
        map.getController().animateTo(myPosition);
        mapController.setZoom(18.5);
        mapController.setCenter(myPosition);

        startMarker.setPosition(myPosition);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon(icon);
        map.getOverlays().add(startMarker);

        startMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                return false;
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
