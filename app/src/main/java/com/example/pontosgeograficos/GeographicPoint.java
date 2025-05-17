package com.example.pontosgeograficos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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

        final MapFragment mapFragment = (MapFragment) this.getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
