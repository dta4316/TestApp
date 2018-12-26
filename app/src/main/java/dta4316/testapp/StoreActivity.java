package dta4316.testapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amazonaws.models.nosql.StoreDO;
import com.amazonaws.models.nosql.StoreInfoDO;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import dta4316.testapp.Common.Common;
import dta4316.testapp.Common.ThisApplication;
import dta4316.testapp.Database.StoreService;

public class StoreActivity extends AppCompatActivity {
    private static final String TAG = StoreActivity.class.getSimpleName();
    private String m_SelectedStoreUUID;
    private GoogleMap m_GoogleMap;
    private ArrayAdapter<StoreInfoDO.Service> m_StoreServiceAdapter;

    @BindView(R.id.ivStore)
    ImageView ivStore;
    @BindView(R.id.tvStoreName)
    TextView tvStoreName;
    @BindView(R.id.tvStoreDescription)
    TextView tvStoreDescription;
    @BindView(R.id.tvStoreAddress)
    TextView tvStoreAddress;
    @BindView(R.id.tvStoreContactPerson)
    TextView tvStoreContactPerson;
    @BindView(R.id.tvStorePhone)
    TextView tvStorePhone;
    @BindView(R.id.tvStoreEmail)
    TextView tvStoreEmail;
    @BindView(R.id.ratingBarStore)
    RatingBar ratingBarStore;
    @BindView(R.id.btnBackToSearch)
    ImageButton btnBackToSearch;
    @BindView(R.id.mapViewStore)
    MapView mapViewStore;
    @BindView(R.id.listStoreService)
    ListView listStoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        ButterKnife.bind(this);

        m_SelectedStoreUUID = getIntent().getStringExtra("SELECTED_STORE_UUID");
        btnBackToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        StoreService.LoadStore(this, m_SelectedStoreUUID);
    }

    private BroadcastReceiver broadcastReceiver_StoreLoaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LoadStoreInfo();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(ThisApplication.GetContext()).registerReceiver(broadcastReceiver_StoreLoaded, new IntentFilter("Load_" + m_SelectedStoreUUID));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(ThisApplication.GetContext()).unregisterReceiver(broadcastReceiver_StoreLoaded);
    }

    private void LoadStoreInfo() {
        StoreDO store = StoreService.GetStore(m_SelectedStoreUUID);
        StoreInfoDO storeInfo = StoreService.GetStoreInfo(m_SelectedStoreUUID);

        if (storeInfo != null) {
            storeInfo.ParseService();
            tvStoreAddress.setText(storeInfo.GetAddressText());
            tvStoreDescription.setText(storeInfo.getStoreDescription());
            tvStoreContactPerson.setText(storeInfo.getStoreContactPerson());
            tvStorePhone.setText(storeInfo.getStorePhone());
            tvStoreEmail.setText(storeInfo.getStoreEmail());

            ratingBarStore.setRating(4);

            m_StoreServiceAdapter = new StoreServicesViewAdapter(this, storeInfo.GetStoreServiceList());
            listStoreService.setAdapter(m_StoreServiceAdapter);
            Common.setListViewHeightBasedOnChildren(listStoreService);
        }

        if (store != null) {
            Glide.with(ThisApplication.GetContext())
                    .load(Common.GetHeaderImageURL(store.getStoreHeaderImage()))
                    .apply(RequestOptions.centerCropTransform())
                    .into(ivStore);
            tvStoreName.setText(store.getStoreName());

            OnMapReadyCallback OnMapReadyListener = new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    m_GoogleMap = googleMap;
                    m_GoogleMap.addMarker(new MarkerOptions()
                            .title(store.getStoreName())
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_flag))
                            .anchor(0.0f, 1.0f)
                            .position(new LatLng(store.getStoreLatitude(), store.getStoreLongtitude())));
                    m_GoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    if (ActivityCompat.checkSelfPermission(StoreActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(StoreActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    m_GoogleMap.setMyLocationEnabled(true);
                    m_GoogleMap.getUiSettings().setZoomControlsEnabled(true);
                    MapsInitializer.initialize(StoreActivity.this);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(new LatLng(store.getStoreLatitude(), store.getStoreLongtitude()));
                    LatLngBounds bounds = builder.build();
                    int padding = 0;
                    // Updates the location and zoom of the MapView
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    m_GoogleMap.moveCamera(cameraUpdate);


//                    int width = getResources().getDisplayMetrics().widthPixels;
//                    int height = getResources().getDisplayMetrics().heightPixels;
//                    int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen
//                    m_GoogleMap.animateCamera((CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)));
                }
            };

            if (mapViewStore != null) {
                mapViewStore.onCreate(null);
                mapViewStore.getMapAsync(OnMapReadyListener);
            }
        }
    }
}
