package Admin;

import android.app.Notification;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodhive.R;
import com.google.firebase.database.ChildEventListener;
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
import java.util.Objects;

import Adapter.OrderListAdapter;
import Model.OrderList;

import static com.example.foodhive.App.CHANNEL_ID;


public class Orders extends Fragment implements OrderListAdapter.ListClickListener, OrderListAdapter.ListMessageClickListener {

    // --- UI --- //
    private RecyclerView recyclerView;
    private NavController navController;


    // -- DatabaseRef --//
    private Query databaseReference;
    private DatabaseReference databaseReferenceChildEvent;

    // -- Var --//
    private List<OrderList> orderListList;
    private OrderListAdapter orderListAdapter;
    private NotificationManagerCompat notificationManagerCompat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.admin_order_recy);

        // ---------- Notification ---------- //


        // Recyler Set///

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderListAdapter = new OrderListAdapter(getContext(), false, this::onListClick, this::onMessageListClick);
        orderListList = new ArrayList<>();

        //---- Firebase ----//
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Order").orderByChild("timestamp");
        databaseReferenceChildEvent = FirebaseDatabase.getInstance().getReference().child("Order");
        navController = Navigation.findNavController(view);

        // ------------- Notification for new order --------------- //
        NotificationAdmin notificationAdmin = new NotificationAdmin(getContext());
        notificationAdmin.setDatabaseForChatNotification();

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

        // ----------- On child changed ---------- //

       /* databaseReferenceChildEvent.addChildEventListener(new ChildEventListener() {

            private final long attachTime = System.currentTimeMillis();

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String time = snapshot.child("others").child("timestamp").getValue(String.class);
                Log.d("Notification", "Child ADDED !" + time);
                assert time != null;
                long timelong = Long.parseLong(time);
                if (timelong > attachTime) {
                    Log.d("Notification", snapshot.getKey() + "");
                    setNotification(snapshot.getKey() + "");
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
*/

    }

    private void setNotification(String orderKey) {
        Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_add_24)
                .setContentTitle("FOOD HIVE")
                .setContentText("You have new order \n"+orderKey)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .build();

        notificationManagerCompat.notify(1, notification);

    }

    @Override
    public void onListClick(OrderList orderList) {

        int integer = 0;
        OrderTestDirections.ActionOrderTestToOrderItems action = OrderTestDirections.actionOrderTestToOrderItems(orderList, integer);
        navController.navigate(action);


    }

    @Override
    public void onMessageListClick(OrderList orderList) {

        OrderTestDirections.ActionOrderTestToChat action = OrderTestDirections.actionOrderTestToChat(orderList.getUid());
        navController.navigate(action);
    }
}