package com.example.pontosgeograficos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
    private String selectedDestination;
    private GoogleMap map;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.location_view);

        final Intent it = this.getIntent();
        final Bundle params = it.getExtras();

        this.selectedDestination = params.getString("destination");

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
