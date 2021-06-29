package com.example.foodhive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            navView.getMenu().removeItem(R.id.login2);
            navView.getMenu().removeItem(R.id.signUp);
        }
        else
        {
            navView.getMenu().removeItem(R.id.logout);
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
                        || destination.getId() == R.id.editItems || destination.getId() == R.id.selectedItemEditAdmin) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    toolbar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                } else if (destination.getId() == R.id.addNewFood || destination.getId() == R.id.selectedItemEditAdmin) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    toolbar.setVisibility(View.GONE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                } else if (destination.getId() == R.id.foodDetails || destination.getId() == R.id.login2 || destination.getId() == R.id.signUp) {
                    toolbar.setVisibility(View.GONE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                } else if (destination.getId() == R.id.chat) {
                    toolbar.setVisibility(View.GONE);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                } else if (destination.getId() == R.id.logout) {


                    firebaseAuth.signOut();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();


                } else {
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setTitleTextColor(getResources().getColor(R.color.white));
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }


              /*  if (destination.getId() == R.id.login2) {
                    toolbar.setVisibility(View.GONE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                } else if (destination.getId() == R.id.signUp) {
                    toolbar.setVisibility(View.GONE);

                    //   getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                } else if ( destination.getId() == R.id.orders || destination.getId() == R.id.usersAdmin
                        || destination.getId() == R.id.editItems || destination.getId() == R.id.adminProfile) {


                    //------------------ ADMIN------------------//
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setBackgroundColor(Color.parseColor("#fbc02d"));
                    toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setHomeButtonEnabled(false);


                } else if (destination.getId() == R.id.addNewFood) {
                    toolbar.setVisibility(View.GONE);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                } else if (destination.getId() == R.id.foodDetails) {
                    toolbar.setVisibility(View.GONE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                } else if (destination.getId() == R.id.homeFragment) {
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setBackgroundColor(Color.parseColor("#FAFAFA"));
                    toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else if (destination.getId() == R.id.orderTest) {
                    toolbar.setVisibility(View.GONE);
                } else if (destination.getId() == R.id.chat) {
                    toolbar.setVisibility(View.GONE);
                } else if (destination.getId() == R.id.usersOrder) {
                    toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else if (destination.getId() == R.id.splashScreen) {
                    toolbar.setVisibility(View.GONE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                } else {
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    toolbar.setTitleTextColor(Color.parseColor("#000000"));
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);


                }*/

            }
        });
    }
}