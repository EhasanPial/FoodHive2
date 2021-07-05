package UI;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
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
import Constants.BaseString;
import Model.CartModel;
import Model.OrderList;
import cn.iwgang.countdownview.CountdownView;


public class OrderItem extends Fragment implements AdminOrderItemsAdapter.ListClickListener {

    // UI //
    private TextView total, deliveryType, status, phone, address;
    private RecyclerView recyclerView;
    private CountdownView countDownTimer;
    private LottieAnimationView lottieAnimationView;


    private ImageView one, two, three;
    private TextView onet, twot, threet;


    // Firebase //
    private DatabaseReference databaseReferenceOrder;
    private DatabaseReference databaseReferenceUsersOrder;
    private FirebaseAuth firebaseAuth;

    // Var //
    private List<CartModel> cartModelList;
    private OrderList orderList;
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
        //  status = view.findViewById(R.id.userorderitem_status);
        address = view.findViewById(R.id.userorderitem_address);
        countDownTimer = view.findViewById(R.id.countdownid);
        lottieAnimationView = view.findViewById(R.id.lottie_image);

        one = view.findViewById(R.id.one);
        two = view.findViewById(R.id.two);
        three = view.findViewById(R.id.three);
        onet = view.findViewById(R.id.text_one);
        twot = view.findViewById(R.id.text_two);
        threet = view.findViewById(R.id.text_three);

        // Args //
        OrderItemArgs args = OrderItemArgs.fromBundle(getArguments());
        orderList = args.getOrderlist();


        //databaseReferenceOrder = FirebaseDatabase.getInstance().getReference().child("Order");
        databaseReferenceUsersOrder = FirebaseDatabase.getInstance().getReference().child("Users Order");
        firebaseAuth = FirebaseAuth.getInstance();

        getPrevTime();

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

        ////// ---- Cooking = accpted , cooked= cooking , ready= ready;

        databaseReferenceUsersOrder.child(firebaseAuth.getCurrentUser().getUid()).child(orderList.getOrderId()).child("others").child("status").addValueEventListener(new ValueEventListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String statustext = snapshot.getValue(String.class);
                if (statustext.equals(getString(R.string.accepted))) {

                    lottieAnimationView.setAnimation(R.raw.cooking);

                    one.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_looks_one_24));
                    onet.setTextColor(getResources().getColor(R.color.colorPrimary));

                    two.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_looks_two_alpha));
                    twot.setTextColor(getResources().getColor(R.color.alpha_white));

                    three.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_looks_3_alpha));
                    threet.setTextColor(getResources().getColor(R.color.alpha_white));


                } else if (statustext.equals(getString(R.string.cooking_))) {

                    lottieAnimationView.setAnimation(R.raw.cooking);

                    one.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_looks_one_alpha));
                    onet.setTextColor(getResources().getColor(R.color.alpha_white));

                    two.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_looks_two_24));
                    twot.setTextColor(getResources().getColor(R.color.colorPrimary));

                    three.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_looks_3_alpha));
                    threet.setTextColor(getResources().getColor(R.color.alpha_white));

                } else if (statustext.equals(getString(R.string.ready_for_delivery))) {


                    lottieAnimationView.setAnimation(R.raw.delivery);

                    one.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_looks_one_alpha));
                    onet.setTextColor(getResources().getColor(R.color.alpha_white));

                    two.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_looks_two_alpha));
                    twot.setTextColor(getResources().getColor(R.color.alpha_white));

                    three.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_looks_3_24));
                    threet.setTextColor(getResources().getColor(R.color.colorPrimary));


                } else {
                    one.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_looks_one_alpha));
                    onet.setTextColor(getResources().getColor(R.color.alpha_white));

                    two.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_looks_two_alpha));
                    twot.setTextColor(getResources().getColor(R.color.alpha_white));

                    three.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_looks_3_alpha));
                    threet.setTextColor(getResources().getColor(R.color.alpha_white));

                }

                //status.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        total.setText(orderList.getTotalprice());
        deliveryType.setText(orderList.getDeliverytype());
        address.setText(orderList.getCurrentaddress());


    }

    @Override
    public void onListClick(CartModel cartModel) {

    }


    private void getPrevTime() {

        long diff = BaseString.getTimeLong(orderList.getTimestamp());

        countDownTimer.start(diff);
    }

    private void saveTime() {
        databaseReferenceUsersOrder.child(orderList.getUid()).child(orderList.getOrderId()).child("others").child("pausetime").setValue(countDownTimer.getRemainTime());

    }


   /* @Override
    public void onStart() {
        super.onStart();

        getPrevTime();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPrevTime();
    }*/

    @Override
    public void onStop() {
        super.onStop();

        saveTime();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        saveTime();
    }


}