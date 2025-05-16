package com.example.pontosgeograficos;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GeographicPoint extends Activity implements OnMapReadyCallback {
    private final LatLng VICOSA = new LatLng(-20.753170, -42.878656);
    private final LatLng TIMOTEO = new LatLng(-19.545576, -42.656434);
    private final LatLng DPI = new LatLng(-20.764978, -42.868461);

    private GoogleMap map;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.location_view);

        final MapFragment mapFragment = (MapFragment) this.getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final GoogleMap googleMap) {
        this.map = googleMap;

        this.map.addMarker(new MarkerOptions().position(this.VICOSA).title("Meu apt Viçosa"));
        this.map.addMarker(new MarkerOptions().position(this.TIMOTEO).title("Minha casa Timóteo"));
        this.map.addMarker(new MarkerOptions().position(this.DPI).title("DPI - UFV"));

        this.map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        final CameraUpdate update = CameraUpdateFactory.newLatLngZoom(this.TIMOTEO, 16);
        this.map.animateCamera(update);
    }
}
