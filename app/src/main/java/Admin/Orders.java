package Admin;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.OrderListAdapter;
import Model.OrderList;


public class Orders extends Fragment implements OrderListAdapter.ListClickListener {

    // --- UI --- //
    private RecyclerView recyclerView;


    // -- DatabaseRef --//
    private DatabaseReference databaseReference;

    // -- Var --//
    private List<OrderList> orderListList;
    private OrderListAdapter orderListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.admin_order_recy);

        // RecyerView SEt///

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderListAdapter = new OrderListAdapter(getContext(), this::onListClick);
        orderListList = new ArrayList<>();

        //---- Firebase ----//
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Order");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                orderListList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    orderListList.add(d.child("others").getValue(OrderList.class));

                }

                orderListAdapter.setList(orderListList);
                recyclerView.setAdapter(orderListAdapter);
                orderListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onListClick(OrderList orderList) {

    }
}