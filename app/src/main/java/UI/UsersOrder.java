package UI;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.foodhive.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import Adapter.UserOrderAdapter;
import Admin.OrderItems;
import Model.OrderList;


public class UsersOrder extends Fragment implements UserOrderAdapter.ListClickListener, UserOrderAdapter.ListMessageClickListener {


    // UI //
    private RecyclerView recyclerView;
    private TextView pleaselogin;
    private LinearLayout noOrder ;

    // ------- Firebase --------- //
    private Query databaseReference;
    private FirebaseAuth firebaseAuth;

    /// --------- variable ---------- ///
    private List<OrderList> orderItemsList;
    private UserOrderAdapter userOrderAdapter;
    private NavController navController;


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
        pleaselogin = view.findViewById(R.id.order_please_login);
        noOrder = view.findViewById(R.id.no_order_id);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderItemsList = new ArrayList<>();
        userOrderAdapter = new UserOrderAdapter(getContext(), this::onListClick, this::onMessageListClick);
        navController = Navigation.findNavController(view);

        /// ------- Firebase ----- ///
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            pleaselogin.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            noOrder.setVisibility(View.GONE);
        } else {
            pleaselogin.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users Order").child(firebaseAuth.getCurrentUser().getUid()).orderByChild("timestamp");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    orderItemsList.clear();
                    if(snapshot.getChildrenCount() == 0)
                    {
                        noOrder.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        pleaselogin.setVisibility(View.GONE);
                    }
                    else
                    {
                        noOrder.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        pleaselogin.setVisibility(View.GONE);
                    }
                    for (DataSnapshot d : snapshot.getChildren()) {
                        OrderList orderList = d.child("others").getValue(OrderList.class);
                        orderItemsList.add(orderList);
                    }

                    Collections.reverse(orderItemsList);
                    userOrderAdapter.setList(orderItemsList);
                    recyclerView.setAdapter(userOrderAdapter);
                    userOrderAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // ------------- Notification for order status--------------- //
            NotificationUser notificationUser = new NotificationUser(getContext(), Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
            notificationUser.setNotificationOnNewOrder();
            // ------------ Notification for chat --------------------- //

        }


    }

    @Override
    public void onListClick(OrderList orderList) {
        UsersOrderDirections.ActionUsersOrderToOrderItem aciton = UsersOrderDirections.actionUsersOrderToOrderItem(orderList);
        navController.navigate(aciton);
    }

    @Override
    public void onMessageListClick(OrderList orderList) {

        UsersOrderDirections.ActionUsersOrderToChat action = UsersOrderDirections.actionUsersOrderToChat();
        action.setUid(orderList.getUid());
        navController.navigate(action);

    }
}