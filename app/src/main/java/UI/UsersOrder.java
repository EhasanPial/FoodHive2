package UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodhive.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.UserOrderAdapter;
import Admin.OrderItems;
import Model.OrderList;


public class UsersOrder extends Fragment implements UserOrderAdapter.ListClickListener, UserOrderAdapter.ListMessageClickListener {


    // UI //
    private RecyclerView recyclerView;


    // ------- Firebase --------- //
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    /// --------- variable ---------- ///
    private List<OrderList> orderItemsList;
    private UserOrderAdapter userOrderAdapter ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.userorder_recy);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderItemsList = new ArrayList<>()  ;
        userOrderAdapter = new UserOrderAdapter(getContext(), this::onListClick, this::onMessageListClick);

        /// ------- Firebase ----- ///
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Order");
        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderItemsList.clear();
                for(DataSnapshot d: snapshot.getChildren())
                {
                    OrderList orderList = d.child("others").getValue(OrderList.class);
                    orderItemsList.add(orderList);
                }

                userOrderAdapter.setList(orderItemsList);
                recyclerView.setAdapter(userOrderAdapter);
                userOrderAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }) ;


    }

    @Override
    public void onListClick(OrderList orderList) {

    }

    @Override
    public void onMessageListClick(OrderList orderList) {

    }
}