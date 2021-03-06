package Admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapter.OrderListAdapter;
import Model.OrderList;


public class completedOrders extends Fragment implements OrderListAdapter.ListClickListener, OrderListAdapter.ListMessageClickListener {

    // --- UI --- //
    private RecyclerView recyclerView;
    private NavController navController;


    // -- DatabaseRef --//
    private Query databaseReference;

    // -- Var --//
    private List<OrderList> orderListList;
    private OrderListAdapter orderListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.admin_completed_order_recy);

        // RecyerView Set///

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderListAdapter = new OrderListAdapter(getContext(), true, this::onListClick, this::onMessageListClick, getActivity());
        orderListList = new ArrayList<>();

        //---- Firebase ----//
        databaseReference = FirebaseDatabase.getInstance().getReference().child("CompletedOrder").orderByChild("timestamp");
        navController = Navigation.findNavController(view);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                orderListList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    orderListList.add(d.child("others").getValue(OrderList.class));

                }
                Collections.reverse(orderListList);

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

        Integer integer = 1 ;
        OrderTestDirections.ActionOrderTestToOrderItems action = OrderTestDirections.actionOrderTestToOrderItems(orderList, integer) ;
        navController.navigate(action);

    }

    @Override
    public void onMessageListClick(OrderList orderList) {

        OrderTestDirections.ActionOrderTestToChat action = OrderTestDirections.actionOrderTestToChat(orderList.getUid());
        navController.navigate(action);

    }
}