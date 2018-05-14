package com.entuition.wekend.view.main.campaign;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.databinding.GoogleMapsActivityBinding;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.util.Utilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMapsActivityBinding binding;

    private GoogleMap mMap;
    private String address;
    private String markerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.google_maps_activity);

        setupToolbar();

        address = getIntent().getStringExtra(Constants.ExtraKeys.MAP_ADDRESS);
        markerTitle = getIntent().getStringExtra(Constants.ExtraKeys.PRODUCT_TITLE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
        LatLng place = Utilities.getLocationFromAddress(this, address);
        if (place != null) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(place).title(markerTitle));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, ProductInfo.GOOGLE_MAP_ZOOM));
            marker.showInfoWindow();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.mapToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
