package UI;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.foodhive.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Adapter.ChatterAdapter;
import Model.CartModel;
import Model.OrderList;
import Model.UsersModel;
import cn.iwgang.countdownview.CountdownView;


public class ChatterBox extends Fragment implements ChatterAdapter.ListClickListener {

    //---- UI ----//
    private TextView subtotal, total, deliveryFee, hungryText;
    private EditText phone, address;
    private Button placeoder;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private NestedScrollView linearLayout;
    private LinearLayout hungryLinear;
    private RadioButton pickup, homedelivery;



    private TextView pleaseLogin;

    //-- -- Firebase ----//
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceOrder;
    private DatabaseReference databaseReferenceCart;
    private DatabaseReference databaseReferenceUsersOrder;
    private DatabaseReference databaseReferenceOpenClose;
    private FirebaseAuth firebaseAuth;

    //---- Variable ----//
    private List<CartModel> cartModelList;
    private String phonetext = "";
    private String addresstext = "";
    private ChatterAdapter chatterAdapter;
    private String totalText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chatter_box, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        subtotal = view.findViewById(R.id.chatter_subtotal);
        total = view.findViewById(R.id.chatter_total);
        phone = view.findViewById(R.id.chatter_contact_info);
        address = view.findViewById(R.id.chatter_address);
        placeoder = view.findViewById(R.id.place_order_button_id);
        recyclerView = view.findViewById(R.id.chatter_recy_id);
        deliveryFee = view.findViewById(R.id.delivery_fee);
        pleaseLogin = view.findViewById(R.id.chatter_please_login);
        linearLayout = view.findViewById(R.id.chatter_box_nested);
        pickup = view.findViewById(R.id.status_pickup);
        homedelivery = view.findViewById(R.id.status_homedelivery);
        hungryLinear = view.findViewById(R.id.hungry_linear);
        hungryText = view.findViewById(R.id.chatter_hungrytext);








        //----- FireabaseDatase -----//


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart");
        databaseReferenceOrder = FirebaseDatabase.getInstance().getReference().child("Order");
        databaseReferenceUsersOrder = FirebaseDatabase.getInstance().getReference().child("Users Order");
        databaseReferenceOpenClose = FirebaseDatabase.getInstance().getReference().child("Open Close");
        cartModelList = new ArrayList<>();
        databaseReferenceCart = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();


        NavController navController = Navigation.findNavController(view);



        hungryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_chatterBox_to_homeFragment);
            }
        });

        if (firebaseAuth.getCurrentUser() == null) {
            pleaseLogin.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            hungryLinear.setVisibility(View.GONE);

            pleaseLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navController.navigate(R.id.action_chatterBox_to_login2);
                }
            });

            return;
        } else {
            setUserInfo();
        }


        // --- RecycleView ----//
        chatterAdapter = new ChatterAdapter(getContext(), this::onListClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("TotalPrice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String subtotalString = snapshot.getValue(String.class);

                if (subtotalString != null) {
                    subtotal.setText(subtotalString + " TK");
                    total.setText(Integer.parseInt(subtotalString) + 30 + " TK");
                    totalText = subtotalString;
                    chatterAdapter.setPhone(phonetext, subtotalString);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //// Cart Items List Setup ////

        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("CartItems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                cartModelList.clear();
                if (snapshot.getChildrenCount() == 0) {
                    linearLayout.setVisibility(View.GONE);
                    pleaseLogin.setVisibility(View.GONE);
                    hungryLinear.setVisibility(View.VISIBLE);
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                    pleaseLogin.setVisibility(View.GONE);
                    hungryLinear.setVisibility(View.GONE);
                    deliveryFee.setText("30 TK (May vary for different locations) ");
                }

                for (DataSnapshot d : snapshot.getChildren()) {

                    Log.d("PHONE SIZE", snapshot.getChildrenCount() + "  " + phonetext);

                    CartModel cartModel = d.getValue(CartModel.class);
                    if (!cartModel.getQuantity().equals("0"))
                        cartModelList.add(cartModel);
                    else {
                        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("CartItems").child(cartModel.getItemkey()).removeValue();
                    }
                }


                Log.d("PHONE LIST SIZE", cartModelList.size() + "");


                chatterAdapter.setList(cartModelList);

                recyclerView.setAdapter(chatterAdapter);
                chatterAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceOpenClose.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String isOpen = snapshot.getValue(String.class);
                if (isOpen.equals("false")) {
                    placeoder.setText("Restaurant is currently closed");
                    placeoder.setEnabled(false);
                } else {
                    placeoder.setEnabled(true);
                }
                Log.d("isopen user", isOpen);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        placeoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                databaseReferenceOpenClose.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String isOpen = snapshot.getValue(String.class);

                        assert isOpen != null;
                        if (isOpen.equals("false")) {
                            placeoder.setText("Restaurant is currently closed");
                            placeoder.setEnabled(false);
                        } else {
                            placeoder.setEnabled(true);
                            placeoder.setText("Place Order");
                            placeoder.setEnabled(false);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Place your order?");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    placeOder(view);

                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {


                                }
                            });

                            builder.create().show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


    }

    private void setUserInfo() {
        // String finalEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        databaseReferenceCart.child("Info").child("Users Info").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UsersModel usersModel = snapshot.getValue(UsersModel.class);
                phonetext = usersModel.getPhone();
                addresstext = usersModel.getAddress();
                phone.setText(phonetext);
                address.setText(addresstext);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void placeOder(View view) {

        String orderId = databaseReferenceOrder.push().getKey();
        String timestamp = System.currentTimeMillis() + "";
        String deliveryType = homedelivery.isChecked() ? "Home Delivery" : "Pick Up";
        OrderList orderList = new OrderList(addresstext, phone.getText().toString(), "Placed", totalText, orderId, firebaseAuth.getCurrentUser().getUid(), deliveryType, timestamp,7200000);
        databaseReferenceOrder.child(orderId).child("others").setValue(orderList);

        // --- Notification -- //
        DatabaseReference databaseReferenceNotification = FirebaseDatabase.getInstance().getReference().child("Notification").child("Order Notify");
        databaseReferenceNotification.child(orderId).setValue(false);


        for (int i = 0; i < cartModelList.size(); i++) {

            databaseReferenceOrder.child(orderId).child("cartItems").child(cartModelList.get(i).getItemkey()).setValue(cartModelList.get(i));

        }


        databaseReferenceUsersOrder.child(firebaseAuth.getCurrentUser().getUid()).child(orderId).child("others").setValue(orderList);
        for (int i = 0; i < cartModelList.size(); i++) {

            databaseReferenceUsersOrder.child(firebaseAuth.getCurrentUser().getUid()).child(orderId).child("cartItems").child(cartModelList.get(i).getItemkey()).setValue(cartModelList.get(i));

        }
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).removeValue();
        ChatterBoxDirections.ActionChatterBoxToOrderItem action = ChatterBoxDirections.actionChatterBoxToOrderItem(orderList);
        Navigation.findNavController(view).navigate(action);


    }

    /*private void openDialog(View viewMain) {


        DialogPlus bottomSheetDialog = DialogPlus.newDialog(getContext())
                .setContentHolder(new ViewHolder(R.layout.cooking_layout))
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setCancelable(false)
                .setContentHeight(1800)
                .create();

        View view = bottomSheetDialog.getHolderView();
        ProgressBar progressBar = view.findViewById(R.id.progressBar_cooking);
        TextView textView = view.findViewById(R.id.textView8);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        bottomSheetDialog.dismiss();


                    }
                }, 2500);


            }
        }, 2000);


        bottomSheetDialog.show();


    }*/

    @Override
    public void onListClick(CartModel cartModel) {

    }

    @Override
    public void onStart() {
        super.onStart();

    }
}