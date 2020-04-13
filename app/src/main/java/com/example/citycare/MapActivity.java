package com.example.citycare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;

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
    private MapView map = null;
    private Context ctx;
    private MyLocationNewOverlay myLocationNewOverlay;
    private ImageButton btCenterMap;
    private IMapController mapController;
    private GpsMyLocationProvider provider;
    private Overlay touchOverlay;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MapActivity.this, NavigationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ctx = MapActivity.this;

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // Map Controller
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        mapController = map.getController();
        mapController.setZoom(18.5);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setScrollableAreaLimitDouble(new BoundingBox(85, 180, -85, -180));
        map.setMaxZoomLevel(20.0);
        map.setMinZoomLevel(4.0);
        map.setHorizontalMapRepetitionEnabled(false);
        map.setVerticalMapRepetitionEnabled(false);
        map.setScrollableAreaLimitLatitude(MapView.getTileSystem().getMaxLatitude(), MapView.getTileSystem().getMinLatitude(), 0);

        // final GeoPoint startPoint = new GeoPoint(-6.1918644, 106.8229880);
        // mapController.setCenter(startPoint);

        // Set default GPS Location
        provider = new GpsMyLocationProvider(ctx);
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        myLocationNewOverlay = new MyLocationNewOverlay(provider, map);
        myLocationNewOverlay.enableFollowLocation();
        myLocationNewOverlay.enableMyLocation();

        Bitmap bitmapNotMoving = BitmapFactory.decodeResource(getResources(), R.drawable.map_red);
        Bitmap bitmapMoving = BitmapFactory.decodeResource(getResources(), R.drawable.map_red);
        myLocationNewOverlay.setDirectionArrow(bitmapNotMoving, bitmapMoving);

        map.getOverlays().add(myLocationNewOverlay);

        // Click default location
        btCenterMap = (ImageButton) findViewById(R.id.ic_center_map);
        btCenterMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.getOverlays().clear();

                map.getOverlays().add(myLocationNewOverlay);
                map.getOverlays().add(touchOverlay);

                myLocationNewOverlay.enableFollowLocation();

            }
        });


        // Overlay map icon
        touchOverlay = new Overlay() {
            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;
            Marker startMarker = new Marker(map);
            Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.map_red, null);

            @Override
            public void draw(Canvas arg0, MapView arg1, boolean arg2) {
            }

            @Override
            public boolean onScroll(MotionEvent pEvent1, MotionEvent pEvent2, float pDistanceX, float pDistanceY, MapView pMapView) {
                pMapView.getOverlays().remove(anotherItemizedIconOverlay);

                startMarker.setIcon(icon);
                startMarker.setPosition(new GeoPoint((float) pMapView.getMapCenter().getLatitude(),
                        (float) pMapView.getMapCenter().getLongitude()));

                startMarker.setTitle(getCompleteAddressString(pMapView.getMapCenter().getLatitude(), pMapView.getMapCenter().getLongitude()));

                startMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        return false;
                    }
                });

                return super.onScroll(pEvent1, pEvent2, pDistanceX, pDistanceY, pMapView);
            }

            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {
                mapView.getOverlays().remove(myLocationNewOverlay);
                mapView.getOverlays().remove(anotherItemizedIconOverlay);

                Projection proj = map.getProjection();
                GeoPoint loc = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());
                String longitude = Double.toString((double) loc.getLongitude());
                String latitude = Double.toString((double) loc.getLatitude());
                //System.out.println("- Latitude = " + latitude + ", Longitude = " + longitude);

                ArrayList<OverlayItem> overlayArray = new ArrayList<OverlayItem>();
                OverlayItem mapItem = new OverlayItem("", "", new GeoPoint(((double) loc.getLatitude()), ((double) loc.getLongitude())));

                mapItem.setMarker(icon);
                startMarker.setIcon(icon);
                startMarker.setTitle(getCompleteAddressString(loc.getLatitude(), loc.getLongitude()));
                startMarker.setPosition(loc);
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                startMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        return false;
                    }
                });

                map.getOverlays().add(startMarker);
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

        map.getOverlays().add(touchOverlay);
    }

    public void onResume() {
        super.onResume();
        map.onResume();
    }

    public void onPause() {
        super.onPause();
        map.onPause();
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                // Log.w(TAG, strReturnedAddress.toString());
            } else {
                Log.w(TAG, "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "Can not get Address!");
        }
        return strAdd;
    }

}
