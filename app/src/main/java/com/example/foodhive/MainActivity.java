package com.example.foodhive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.Locale;

import Constants.PermissionUtils;
import Model.UsersModel;
import UI.FoodDetails;
import UI.FoodHive;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private NavController navController;
    public static NavigationView navView;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.main_toolbar);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.main_drawwer_layout);


        setSupportActionBar(toolbar);


        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        .setDrawerLayout(drawerLayout)
                        .build();

        navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        hidetoolbar(navController);
        View header = navView.getHeaderView(0);
        TextView text = (TextView) header.findViewById(R.id.nav_header_use_email);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            navView.getMenu().removeItem(R.id.login2);
            navView.getMenu().removeItem(R.id.signUp);
            text.setText(firebaseAuth.getCurrentUser().getEmail());
        } else {
            navView.getMenu().removeItem(R.id.logout);
            navView.getMenu().removeItem(R.id.orderItems);
            text.setText("");

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.chatterBox) {
            return NavigationUI.onNavDestinationSelected(item, navController);
        }

        return super.onOptionsItemSelected(item);
    }

    private void hidetoolbar(NavController navController) {
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

                if (destination.getId() == R.id.adminFragment || destination.getId() == R.id.usersAdmin || destination.getId() == R.id.adminProfile
                        || destination.getId() == R.id.editItems) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    toolbar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                } else if (destination.getId() == R.id.addNewFood || destination.getId() == R.id.selectedItemEditAdmin) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    toolbar.setVisibility(View.GONE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                } else if (R.id.topItemsFragment == destination.getId()) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    toolbar.setVisibility(View.GONE);
                } else if (destination.getId() == R.id.orderTest) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    toolbar.setVisibility(View.GONE);
                } else if (destination.getId() == R.id.adminFoodItems || destination.getId() == R.id.orders || destination.getId() == R.id.completedOrders || destination.getId() == R.id.orderItems) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    toolbar.setVisibility(View.GONE);
                } else if (destination.getId() == R.id.sendNotificaion) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    toolbar.setVisibility(View.GONE);
                }

                ////////////////// -------------------------------- ADMIN UPORE SESH -------------------------- ////////////////////////////////////

                else if (destination.getId() == R.id.foodDetails || destination.getId() == R.id.login2 || destination.getId() == R.id.signUp) {
                    toolbar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    //getWindow().setFlags(WindowManager.LayoutParams.F, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                } else if (destination.getId() == R.id.chat) {
                    toolbar.setVisibility(View.GONE);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                } else if (destination.getId() == R.id.logout) {


                    firebaseAuth.signOut();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();


                } else if (destination.getId() == R.id.orderItem) {
                    toolbar.setVisibility(View.GONE);


                } else {
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                }


            }
        });
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();


        }
    }


}