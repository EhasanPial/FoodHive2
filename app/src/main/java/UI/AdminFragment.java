package UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.foodhive.MainActivity;
import com.example.foodhive.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Admin.NotificationAdmin;


public class AdminFragment extends Fragment {
    //-- UI --//
    private MaterialCardView orders, users, addnewfood, edititems, profile, signout, viewAsUser, setTop;
    private TextView open_rest;
    private ProgressBar progressBar;


    //-- Firebase --//
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String isOpen = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orders = view.findViewById(R.id.order_id);
        users = view.findViewById(R.id.users_admin_id);
        addnewfood = view.findViewById(R.id.add_new_food_id);
        edititems = view.findViewById(R.id.edit_items_id);
        profile = view.findViewById(R.id.admin_profile_id);
        signout = view.findViewById(R.id.admin_logout_id);
        viewAsUser = view.findViewById(R.id.admin_viewasuser);
        setTop = view.findViewById(R.id.admin__setTopItem);
        open_rest = view.findViewById(R.id.open_res);
        progressBar = view.findViewById(R.id.admin_resclose_progress);


        // --------- Firebase -----//
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Open Close");

        // ------------- Notification for new order --------------- //
        NotificationAdmin notificationAdmin = new NotificationAdmin(getContext());
        notificationAdmin.setNotificationOnNewOrder();
        notificationAdmin.setDatabaseForChatNotification();

        // ---------------- Notification for Chat ---------------- //

       // notificationAdmin.setDatabaseForChatNotification();


       /* NotificationAdmin notificationAdmin = new NotificationAdmin(getContext(), "andfakf") ;
        notificationAdmin.setNotificationOnNewOrder();
*/

        NavController navController = Navigation.findNavController(view);
        progressBar.setVisibility(View.VISIBLE);
        open_rest.setVisibility(View.GONE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isOpen = snapshot.getValue(String.class);
                progressBar.setVisibility(View.GONE);
                open_rest.setVisibility(View.VISIBLE);
                if (isOpen.equals("false")) {
                    open_rest.setText("Restaurant is closed now");
                    if (getActivity() != null) {
                        open_rest.setTextColor(getActivity().getResources().getColor(R.color.white));
                        open_rest.setBackground(getActivity().getResources().getDrawable(R.drawable.res_closed_back));
                    }
                } else {
                    open_rest.setText("Restaurant is open");
                    if (getActivity() != null) {
                        open_rest.setTextColor(getActivity().getResources().getColor(R.color.black));
                        open_rest.setBackground(getActivity().getResources().getDrawable(R.drawable.thirty_for_dp_back));
                    }

                }
                Log.d("isopen", isOpen);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        open_rest.setOnClickListener(v -> {


            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            if (isOpen.equals("false")) {

                builder.setTitle("Do you want to open restaurant?");
                builder.setPositiveButton("NO", (dialog, id) -> {

                }).setNegativeButton("YES", (dialog, id) -> {
                    databaseReference.setValue("true");

                    open_rest.setText("Restaurant is open");
                    if (getActivity() != null) {
                        open_rest.setTextColor(getActivity().getResources().getColor(R.color.black));
                        open_rest.setBackground(getActivity().getResources().getDrawable(R.drawable.thirty_for_dp_back));
                    }

                });


            } else {


                builder.setTitle("Do you want to close restaurant?");
                builder.setPositiveButton("NO", (dialog, id) -> {

                }).setNegativeButton("YES", (dialog, id) -> {

                    databaseReference.setValue("false");

                    open_rest.setText("Restaurant is closed now");
                    if (getActivity() != null) {
                        open_rest.setTextColor(getActivity().getResources().getColor(R.color.black));
                        open_rest.setBackground(getActivity().getResources().getDrawable(R.drawable.res_closed_back));
                    }

                });


            }
            builder.create().show();


        });

        orders.setOnClickListener(v -> navController.navigate(R.id.action_adminFragment_to_orderTest));

        users.setOnClickListener(v -> navController.navigate(R.id.action_adminFragment_to_usersAdmin));
        addnewfood.setOnClickListener(v -> navController.navigate(R.id.action_adminFragment_to_addNewFood));
        edititems.setOnClickListener(v -> navController.navigate(R.id.action_adminFragment_to_editItems));


        profile.setOnClickListener(v -> navController.navigate(R.id.action_adminFragment_to_adminProfile));


        signout.setOnClickListener(v -> {

            getActivity().finish();
            System.exit(0);

        });

        viewAsUser.setOnClickListener(v -> {
            firebaseAuth.signOut();
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        });

        setTop.setOnClickListener(v -> navController.navigate(R.id.action_adminFragment_to_topItemsFragment));
    }
}