package com.example.pontosgeograficos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GeographicPoint extends Activity implements OnMapReadyCallback, LocationListener {
    public LocationManager lm;
    public Criteria criteria;
    public String provider;
    public int TEMPO_REQUISICAO_LATLONG = 5000;
    public int DISTANCIA_MIN_METROS = 0;
    public int LOCATION_CODE_REQUEST = 1;
    private boolean isRequestingLocationUpdates = false;
    private Marker currentLocationMarker;
    private Location currentLocation;
    private LatLng VICOSA;
    private LatLng TIMOTEO;
    private LatLng DPI;
    private String selectedDestination;
    private GoogleMap map;

    private void getLocationPointsFromDataBase() {
        final BancoDadosSingleton db = BancoDadosSingleton.getInstance();
        final Cursor c = db.buscar("Location", new String[]{"descricao", "latitude", "longitude"}, "", "");

        while (c.moveToNext()) {
            @SuppressLint("Range") final String desc = c.getString(c.getColumnIndex("descricao"));
            @SuppressLint("Range") final double lat = c.getDouble(c.getColumnIndex("latitude"));
            @SuppressLint("Range") final double lng = c.getDouble(c.getColumnIndex("longitude"));

            final LatLng point = new LatLng(lat, lng);
            this.map.addMarker(new MarkerOptions().position(point).title(desc));

            if (desc.equals("Meu apartamento em Viçosa")) {
                this.VICOSA = point;
            } else if ("Minha casa em Timóteo".equals(desc)) {
                this.TIMOTEO = point;
            } else if ("DPI - UFV".equals(desc)) {
                this.DPI = point;
            }
        }

        c.close();
        db.fechar();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.location_view);

        final Intent it = this.getIntent();
        final Bundle params = it.getExtras();

        this.selectedDestination = params.getString("destination");

        this.lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        this.criteria = new Criteria();

        final PackageManager packageManager = this.getPackageManager();
        final boolean hasGps = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        if (hasGps) {
            this.criteria.setAccuracy(Criteria.ACCURACY_FINE);
        } else {
            this.criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        }

        final MapFragment mapFragment = (MapFragment) this.getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                Toast.makeText(this, "Permita o uso da localização para obter a localização atual.", Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        this.LOCATION_CODE_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        this.LOCATION_CODE_REQUEST);
            }

        } else if (!this.isRequestingLocationUpdates) {
            this.provider = this.lm.getBestProvider(this.criteria, true);
            this.provider = LocationManager.GPS_PROVIDER;

            if (this.provider != null) {
                this.lm.requestLocationUpdates(this.provider, this.TEMPO_REQUISICAO_LATLONG, this.DISTANCIA_MIN_METROS, this);
                this.isRequestingLocationUpdates = true;
            }
        }
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        if (requestCode == this.LOCATION_CODE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.provider = this.lm.getBestProvider(this.criteria, true);
                this.provider = LocationManager.GPS_PROVIDER;

                if (this.provider != null) {
                    this.lm.requestLocationUpdates(this.provider, this.TEMPO_REQUISICAO_LATLONG, this.DISTANCIA_MIN_METROS, this);
                    this.isRequestingLocationUpdates = true;
                }
            } else {
                Toast.makeText(this, "Permita o uso da localicação para obter a localização atual.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (this.lm != null) {
            this.lm.removeUpdates(this);
            this.isRequestingLocationUpdates = false;
        }
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(final Location location) {
        if (location != null) {
            this.currentLocation = location;
        }
    }

    @Override
    public void onMapReady(@NonNull final GoogleMap googleMap) {
        this.map = googleMap;
        this.map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        this.getLocationPointsFromDataBase();

        LatLng initialTarget = null;

        if (this.selectedDestination.equals("Minha casa na cidade natal")) {
            initialTarget = this.TIMOTEO;
        } else if (this.selectedDestination.equals("Minha casa em Viçosa")) {
            initialTarget = this.VICOSA;
        } else if (this.selectedDestination.equals("Meu departamento")) {
            initialTarget = this.DPI;
        }

        if (initialTarget != null) {
            final CameraUpdate update = CameraUpdateFactory.newLatLngZoom(initialTarget, 16);
            this.map.animateCamera(update);
        }
    }

    public void onClickLocation(final View view) {
        final String tag = (String) view.getTag();

        if (tag.equals("actualLocation")) {
            this.requestCameraPermission();

            if (this.currentLocation != null && this.map != null) {
                final LatLng latLng = new LatLng(this.currentLocation.getLatitude(), this.currentLocation.getLongitude());

                if (this.currentLocationMarker != null) {
                    this.currentLocationMarker.remove();
                }

                this.currentLocationMarker = this.map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Minha localização")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                final CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                this.map.animateCamera(update);
            } else {
                Toast.makeText(this, "Aguardando localização atual...", Toast.LENGTH_SHORT).show();
            }

            return;
        }

        LatLng targetPoint = null;

        if (tag.equals("timoteo")) {
            targetPoint = this.TIMOTEO;
        } else if (tag.equals("vicosa")) {
            targetPoint = this.VICOSA;
        } else if (tag.equals("dpi")) {
            targetPoint = this.DPI;
        }

        if (targetPoint != null) {
            final CameraUpdate update = CameraUpdateFactory.newLatLngZoom(targetPoint, 16);
            this.map.animateCamera(update);
        }
    }
}
