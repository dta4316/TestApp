package dta4316.testapp.ui.Fragments;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.models.nosql.StoreDO;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dta4316.testapp.Command.Command;
import dta4316.testapp.Common.Authentication;
import dta4316.testapp.Common.Common;
import dta4316.testapp.Common.ThisApplication;
import dta4316.testapp.Database.StoreService;
import dta4316.testapp.Location.LocationService;
import dta4316.testapp.R;
import dta4316.testapp.StoreActivity;
import dta4316.testapp.StoreViewAdapter;

public class SearchStoreFragment extends Fragment implements StoreViewAdapter.StoreViewClickedListener {
    private View m_RootView;
    private LocationService m_LocationService;
    private SearchStoreViewModel mViewModel;
    private BottomNavigationView m_BottomNavigation;
    private RecyclerView m_RecyclerView;
    private StoreViewAdapter m_StoreViewAdapter;
    private Boolean m_broadcastReceiverRegistered = false;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_store_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);

        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                m_StoreViewAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                m_StoreViewAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        m_RootView = inflater.inflate(R.layout.activity_search, container, false);
        mViewModel = ViewModelProviders.of(this).get(SearchStoreViewModel.class);

        Command.CommandFactory commandFactory = Command.CommandFactory.init();
        commandFactory.AddCommand("StoreLoaded", this::DisplayCurrentLocation);

        m_LocationService = new LocationService(getActivity(), Objects.requireNonNull(getActivity()).getBaseContext());
        m_LocationService.Init();
        m_LocationService.SetCommandFactory(commandFactory);
        m_LocationService.UpdateLocation();

        StoreService.LoadAllStore(ThisApplication.GetContext());

//        for(int i=1; i<25; i++) {
//            String counter = " " + String.valueOf(i);
//            String StoreName = "Store" + counter;
//            Double StoreLatitude = 37.687966 + (i % 2 == 0 ? 1.0 : -1.0);
//            Double StoreLongtitude = -123.082531 + (i % 2 == 0 ? 1.0 : -1.0);
//            String StoreHeaderImage = i % 3 == 0 ? "johnny.jpg" : "tom_cruise.jpg";
//
//
//            StoreService.AddStore(StoreName, StoreLatitude, StoreLongtitude, StoreHeaderImage);
//        }

        Toolbar toolbar = (Toolbar) m_RootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(R.string.toolbar_title);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        m_BottomNavigation = getActivity().findViewById(R.id.navigation);
        m_RecyclerView = m_RootView.findViewById(R.id.rvLocations);
        m_RecyclerView.setLayoutManager(layoutManager);
        m_RecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        m_RecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && m_BottomNavigation.isShown()) {
                    //m_BottomNavigation.setScaleY(0);
                    //m_BottomNavigation.setVisibility(View.GONE);
                    //m_BottomNavigation.setAnimation(m_AnimFadeout);
                    Common.SlideDown(m_BottomNavigation);
                } else if (dy < 0) {
                    //m_BottomNavigation.setScaleY(1);
                    //m_BottomNavigation.setVisibility(View.VISIBLE);
                    //m_BottomNavigation.setAnimation(m_AnimFadein);
                    Common.SlideUp(m_BottomNavigation);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        return m_RootView;
    }


    private BroadcastReceiver broadcastReceiver_AllStoreLoaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DisplayCurrentLocation();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!m_broadcastReceiverRegistered) {
            LocalBroadcastManager.getInstance(ThisApplication.GetContext()).registerReceiver(broadcastReceiver_AllStoreLoaded, StoreService.GetAllStoreLoadedIntent());
            m_broadcastReceiverRegistered = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Authentication.GetIsSignedIn()) {
            LocalBroadcastManager.getInstance(ThisApplication.GetContext()).unregisterReceiver(broadcastReceiver_AllStoreLoaded);
            m_broadcastReceiverRegistered = false;
        }
    }

    private void DisplayCurrentLocation() {
        Location currentLocation = m_LocationService.GetLastUpdatedLocation();
        if (currentLocation != null) {
            LoadLocationView();
        }
    }

    private void LoadLocationView() {
        if (!StoreService.GetIsLoaded()) {
            return;
        }

        List<StoreDO> locationList = StoreService.GetStoreList();
        boolean sortAscending = true;//((CheckBox) m_RootView.findViewById(R.id.chkSort)).isChecked();

        Location currentLocation = m_LocationService.GetLastUpdatedLocation();
        StoreService.UpdateDistance(currentLocation);

        StoreDO[] locationArray = locationList.toArray(new StoreDO[0]);

        if (sortAscending) {
            Arrays.sort(locationArray, (o1, o2) -> o1.getStoreDistance().compareTo(o2.getStoreDistance()));
        }

        m_StoreViewAdapter = new StoreViewAdapter(getActivity(), Arrays.asList(locationArray));
        m_StoreViewAdapter.SetClickListener(this);
        m_RecyclerView.setAdapter(m_StoreViewAdapter);
    }

    @Override
    public void onStoreViewClicked(View view, int position) {
        StoreDO store = m_StoreViewAdapter.GetStore(position);
        if (store != null) {
            Intent intent = new Intent(ThisApplication.GetContext(), StoreActivity.class);
            intent.putExtra("SELECTED_STORE_UUID", store.getUUID());
            startActivity(intent);
        }
    }
}
