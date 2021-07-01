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
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.foodhive.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.AdminOrderItemsAdapter;
import Admin.OrderItemsArgs;
import Model.CartModel;
import Model.OrderList;


public class OrderItem extends Fragment implements AdminOrderItemsAdapter.ListClickListener {

    // UI //
    private TextView  total, deliveryType, status, phone, address;
    private RecyclerView recyclerView;



    // Firebase //
    private DatabaseReference databaseReferenceOrder;
    private DatabaseReference databaseReferenceUsersOrder;
    private FirebaseAuth firebaseAuth ;

    // Var //
    private List<CartModel> cartModelList;

    private AdminOrderItemsAdapter orderItemsAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         return inflater.inflate(R.layout.fragment_order_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        total = view.findViewById(R.id.userorderitem_total);
        recyclerView = view.findViewById(R.id.userorderitem_recy);
        deliveryType = view.findViewById(R.id.userorderitem_deliveryTpe);
        status = view.findViewById(R.id.userorderitem_status);
        phone = view.findViewById(R.id.userorderitem_contact);
        address = view.findViewById(R.id.userorderitem_address);

        // Args //
        OrderItemArgs args = OrderItemArgs.fromBundle(getArguments()) ;
        OrderList orderList = args.getOrderlist() ;

        databaseReferenceOrder = FirebaseDatabase.getInstance().getReference().child("Order");
        databaseReferenceUsersOrder = FirebaseDatabase.getInstance().getReference().child("Users Order");
        firebaseAuth = FirebaseAuth.getInstance() ;

        orderItemsAdapter = new AdminOrderItemsAdapter(getContext(), this::onListClick);
        cartModelList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // ------- Cart Items -------- //
        databaseReferenceUsersOrder.child(firebaseAuth.getCurrentUser().getUid()).child(orderList.getOrderId()).child("cartItems").addListenerForSingleValueEvent(new ValueEventListener() {
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

        databaseReferenceUsersOrder.child(firebaseAuth.getCurrentUser().getUid()).child(orderList.getOrderId()).child("others").child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                status.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }) ;


        total.setText(orderList.getTotalprice());
        deliveryType.setText(orderList.getDeliverytype());
        address.setText(orderList.getCurrentaddress());



    }

    @Override
    public void onListClick(CartModel cartModel) {

    }
}