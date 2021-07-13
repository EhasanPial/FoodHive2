package Admin;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodhive.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import Adapter.AdminUsersRecyclerAdapter;
import Model.UsersModel;
import UI.NotificationUser;

public class UsersAdmin extends Fragment implements AdminUsersRecyclerAdapter.userSelect {

    private RecyclerView recyclerView;
    private Query databaseReference;
    private NavController navController;
    /// -- Vari --- /
    private List<UsersModel> usersModelList;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView = view.findViewById(R.id.user_recy_id);


        databaseReference = FirebaseDatabase.getInstance().getReference("Info").child("Users Info").orderByChild("name");
        navController = Navigation.findNavController(view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdminUsersRecyclerAdapter adminUsersRecyclerAdapter = new AdminUsersRecyclerAdapter(getContext(), this::onUserClick);
        usersModelList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();


        List<String> notifiUser = new ArrayList<>();
        List<UsersModel> newFreshList = new ArrayList<>();

        DatabaseReference databaseReferenceNotification = FirebaseDatabase.getInstance().getReference().child("Notification").child("Message").child("Admin Messages");
        databaseReferenceNotification.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                notifiUser.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    String has = d.getKey();

                    notifiUser.add(has);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersModelList.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    usersModelList.add(d.getValue(UsersModel.class));

                }

               /* adminUsersRecyclerAdapter.setList(usersModelList);
                recyclerView.setAdapter(adminUsersRecyclerAdapter);
                adminUsersRecyclerAdapter.notifyDataSetChanged(); */

                Collections.reverse(notifiUser);

                for (int i = 0; i < notifiUser.size(); i++) {
                    for (int j = 0; j < usersModelList.size(); j++) {
                        if (usersModelList.get(j).getUid().equals(notifiUser.get(i))) {
                            newFreshList.add(usersModelList.get(j));
                            usersModelList.remove(j);
                        }
                    }
                }


                Log.d("UserAdmin", notifiUser.size() + " notifyuser");
                Log.d("UserAdmin", newFreshList.size() + " freshlist");
                Log.d("UserAdmin", usersModelList.size() + " userList");


                newFreshList.addAll(usersModelList);
                adminUsersRecyclerAdapter.setList(newFreshList);
                recyclerView.setAdapter(adminUsersRecyclerAdapter);
                adminUsersRecyclerAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onUserClick(String uid, String notify) {


        UsersAdminDirections.ActionUsersAdminToChat action = UsersAdminDirections.actionUsersAdminToChat();
        action.setUid(uid);

        Navigation.findNavController(getView()).navigate(action);
    }
}