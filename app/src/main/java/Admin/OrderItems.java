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
import android.widget.TextView;

import com.example.foodhive.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.AdminOrderItemsAdapter;
import Adapter.ChatterAdapter;
import Model.CartModel;
import Model.OrderList;


public class OrderItems extends Fragment implements AdminOrderItemsAdapter.ListClickListener {

    // UI //
    private TextView subtotal, total;
    private RecyclerView recyclerView;

    // Firebase //
    private DatabaseReference databaseReferenceOrder;

    // Var //
    private List<CartModel> cartModelList;
    private String phonetext = "";
    private String addresstext = "";
    private AdminOrderItemsAdapter orderItemsAdapter;
    private String totalText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        subtotal = view.findViewById(R.id.orderItem_subtotal);
        total = view.findViewById(R.id.orderItem_total);
        recyclerView = view.findViewById(R.id.orderItem_recy);

        /// --- Getting Arguments --- ///
        OrderItemsArgs orderItemsArgs = OrderItemsArgs.fromBundle(getArguments());
        String orderID = orderItemsArgs.getOrdeId();

        databaseReferenceOrder = FirebaseDatabase.getInstance().getReference().child("Order");

        orderItemsAdapter = new AdminOrderItemsAdapter(getContext(), this::onListClick);
        cartModelList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReferenceOrder.child(orderID).child("cartItems").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    cartModelList.add(d.getValue(CartModel.class));
                }

                orderItemsAdapter.setList(cartModelList);
                recyclerView.setAdapter(orderItemsAdapter);
                orderItemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceOrder.child(orderID).child("others").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                OrderList orderList = snapshot.getValue(OrderList.class);
                subtotal.setText(orderList.getTotalprice() + " TK");
                int sub = Integer.parseInt(orderList.getTotalprice());
                total.setText(sub + 30 + " TK");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onListClick(CartModel cartModel) {

    }
}