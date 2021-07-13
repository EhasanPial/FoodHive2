package Admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.foodhive.FcmNotificationsSender;
import com.example.foodhive.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.AdminOrderItemsAdapter;
import Adapter.ChatterAdapter;
import Model.CartModel;
import Model.OrderList;
import UI.NotificationUser;


public class OrderItems extends Fragment implements AdminOrderItemsAdapter.ListClickListener {

    // UI //
    private TextView subtotal, total, deliveryType, contact, deliveryaddress;
    private RecyclerView recyclerView;
    private RadioButton cooking, ready, cooked;
    private TextView applyStatus;

    // Firebase //
    private DatabaseReference databaseReferenceOrder;
    private DatabaseReference databaseReferenceUsersOrder;

    // Var //
    private List<CartModel> cartModelList;
    private String phonetext = "";
    private String addresstext = "";
    private AdminOrderItemsAdapter orderItemsAdapter;
    private String totalText;
    private OrderList orderList;

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
        cooking = view.findViewById(R.id.status_cooking);
        ready = view.findViewById(R.id.status_readytodeliver);
        cooked = view.findViewById(R.id.status_cooked);
        applyStatus = view.findViewById(R.id.apply_status_id);
        deliveryType = view.findViewById(R.id.orderItem_deliveryTpe);
        contact = view.findViewById(R.id.orderItem_contact);
        deliveryaddress = view.findViewById(R.id.orderItem_address);


        /// --- Getting Arguments --- ///
        OrderItemsArgs orderItemsArgs = OrderItemsArgs.fromBundle(getArguments());
        OrderList orderListargs = orderItemsArgs.getOrderList();
        int completeORUncomplete = orderItemsArgs.getComORuncomplete();

        // --------- Setting order status ---------- //

        contact.setText(orderListargs.getPhone());
        deliveryaddress.setText(orderListargs.getCurrentaddress());

        if (orderListargs.getStatus().equals(getString(R.string.ready_for_delivery))) {
            ready.setChecked(true);
        } else if (orderListargs.getStatus().equals(getString(R.string.cooking_))) {
            cooked.setChecked(true);
        } else if (orderListargs.getStatus().equals(getString(R.string.accepted))) {
            cooking.setChecked(true);
        } else if (orderListargs.getStatus().equals("Completed")) {
            ready.setChecked(true);
        }
        // --------- setting from which fargment it came completed or uncompleted ------------- //

        String type = completeORUncomplete == 0 ? "Order" : "CompletedOrder";
        databaseReferenceOrder = FirebaseDatabase.getInstance().getReference().child(type);
        databaseReferenceUsersOrder = FirebaseDatabase.getInstance().getReference().child("Users Order");

        orderItemsAdapter = new AdminOrderItemsAdapter(getContext(), this::onListClick);
        cartModelList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // ------- Cart Items -------- //
        databaseReferenceOrder.child(orderListargs.getOrderId()).child("cartItems").addListenerForSingleValueEvent(new ValueEventListener() {
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

        /// ----- others ----- ///
        orderList = new OrderList();

        databaseReferenceOrder.child(orderListargs.getOrderId()).child("others").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList = snapshot.getValue(OrderList.class);
                subtotal.setText(orderList.getTotalprice() + " TK");
                if (orderList.getTotalprice() != null) {
                    int sub = Integer.parseInt(orderList.getTotalprice());
                    total.setText(sub + 30 + " TK");
                }
                deliveryType.setText(orderList.getDeliverytype());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        NotificationUser notificationUser = new NotificationUser(getContext(), orderListargs.getUid(), getActivity()) ;

        DatabaseReference databaseReferenceNotification = FirebaseDatabase.getInstance().getReference().child("Notification").child("Order Status");
        applyStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // applyStatus.setEnabled(false);
                if (ready.isChecked()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("status", getString(R.string.ready_for_delivery));
                    databaseReferenceOrder.child(orderListargs.getOrderId()).child("others").updateChildren(m);
                    databaseReferenceUsersOrder.child(orderList.getUid()).child(orderListargs.getOrderId()).child("others").updateChildren(m);

                   // databaseReferenceNotification.child(orderList.getUid()).child(orderListargs.getOrderId()).setValue(getString(R.string.ready_for_delivery));
                     notificationUser.setFirebaseOrderNotification(getString(R.string.Your_order_is), getString(R.string.ready_for_delivery));
                    ready.setChecked(true);
                    Snackbar.make(view, "New status is send to user", Snackbar.LENGTH_SHORT).show();

                } else if (cooked.isChecked()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("status", getString(R.string.cooking_));
                    databaseReferenceOrder.child(orderListargs.getOrderId()).child("others").updateChildren(m);
                    databaseReferenceUsersOrder.child(orderList.getUid()).child(orderListargs.getOrderId()).child("others").updateChildren(m);


                  //  databaseReferenceNotification.child(orderList.getUid()).child(orderListargs.getOrderId()).setValue(getString(R.string.cooking_));

                    notificationUser.setFirebaseOrderNotification(getString(R.string.Your_order_is), getString(R.string.cooking_));
                    cooked.setChecked(true);
                    Snackbar.make(view, "New status is send to user", Snackbar.LENGTH_SHORT).show();
                } else if (cooking.isChecked()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("status", getString(R.string.accepted));
                    databaseReferenceOrder.child(orderListargs.getOrderId()).child("others").updateChildren(m);
                    databaseReferenceUsersOrder.child(orderList.getUid()).child(orderListargs.getOrderId()).child("others").updateChildren(m);
                   // databaseReferenceNotification.child(orderList.getUid()).child(orderListargs.getOrderId()).setValue(getString(R.string.accepted));

                    notificationUser.setFirebaseOrderNotification(getString(R.string.Your_order_is), getString(R.string.accepted));

                    cooking.setChecked(true);
                    Snackbar.make(view, "New status is send to user", Snackbar.LENGTH_SHORT).show();

                } else {
                    Snackbar.make(view, "Please Select a status", Snackbar.LENGTH_SHORT).show();
                }


            }
        });


    }

    @Override
    public void onListClick(CartModel cartModel) {

    }
}