package UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodhive.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;


public class AdminFragment extends Fragment {
    //-- UI --//
    private MaterialCardView orders, users, addnewfood, edititems, profile, signout, viewAsUser, setTop;


    //-- Firebase --//
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

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


        // --------- Firebase -----//
        firebaseAuth = FirebaseAuth.getInstance();


        NavController navController = Navigation.findNavController(view);

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_adminFragment_to_orderTest);
            }
        });

        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_adminFragment_to_usersAdmin);
            }
        });
        addnewfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_adminFragment_to_addNewFood);
            }
        });
        edititems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_adminFragment_to_editItems);
            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_adminFragment_to_adminProfile);
            }
        });


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().finish();
                System.exit(0);

            }
        });

        viewAsUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                navController.navigate(R.id.action_adminFragment_to_homeFragment);
            }
        });

        setTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_adminFragment_to_topItemsFragment);
            }
        });
    }
}