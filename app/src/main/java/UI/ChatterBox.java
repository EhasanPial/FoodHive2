package UI;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import Adapter.ChatterAdapter;
import Model.CartModel;
import Model.OrderList;
import Model.UsersModel;


public class ChatterBox extends Fragment implements ChatterAdapter.ListClickListener {

    // UI //
    private TextView subtotal, total, deliveryFee;
    private EditText phone, address;
    private Button placeoder;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    // -- Firebase --//
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceOrder;
    private DatabaseReference databaseReferenceCart;
    private FirebaseAuth firebaseAuth ;

    //--Variable--//
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


        //-- FireabaseDatase--//


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart");
        databaseReferenceOrder = FirebaseDatabase.getInstance().getReference().child("Order");
        cartModelList = new ArrayList<>();
        databaseReferenceCart = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();


        // --- RecycleView ----//


        chatterAdapter = new ChatterAdapter(getContext(), this::onListClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        setUserInfo();
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
                if(snapshot.getChildrenCount() == 0)
                {
                    deliveryFee.setText("0 TK");
                }
                else
                {
                    deliveryFee.setText("30 TK");
                }

                for (DataSnapshot d : snapshot.getChildren()) {

                   Log.d("PHONE SIZE", snapshot.getChildrenCount()+"  "+phonetext);

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


      //  recyclerView.setAdapter(chatterAdapter);

        placeoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeoder.setEnabled(false);
                placeOder(view);
            }
        });


    }

    private void setUserInfo() {
       // String finalEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        databaseReferenceCart.child("Info").child("Users Info").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UsersModel usersModel = snapshot.getValue(UsersModel.class);
                phonetext = usersModel.getPhone() ;
                addresstext = usersModel.getAddress() ;
                phone.setText(phonetext);
                address.setText(addresstext);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //----------------- WASSS GETTINGGGGGGGGG USE INFO ----------------//

     /*   databaseReferenceCart.child("Info").child("Users Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    UsersModel usersModel = d.getValue(UsersModel.class);
                    // Log.d("EMAIL", usersModel.getPhone().toLowerCase() + "   " + finalEmail);
                    if (usersModel.getEmail().toLowerCase().equals(finalEmail.toLowerCase())) {
                        phonetext = usersModel.getPhone();
                        Log.d("PHONE", usersModel.getPhone().toLowerCase() + "   " + finalEmail);
                        addresstext = usersModel.getAddress();
                        address.setText(addresstext);
                        phone.setText(phonetext);
                        return;

                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        loadCartItems();

        Log.d("PHONE", phonetext);*/

    }




    private void placeOder(View view) {

        String orderId = databaseReferenceOrder.push().getKey();

        OrderList orderList = new OrderList(addresstext, phone.getText().toString(), "Uncomplete", totalText, orderId, firebaseAuth.getCurrentUser().getUid());
        databaseReferenceOrder.child(orderId).child("others").setValue(orderList);


        for (int i = 0; i < cartModelList.size(); i++) {

            databaseReferenceOrder.child(orderId).child("cartItems").child(cartModelList.get(i).getItemkey()).setValue(cartModelList.get(i));

        }


        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).removeValue();
        openDialog(view);


    }

    private void openDialog(View viewMain) {


        DialogPlus bottomSheetDialog = DialogPlus.newDialog(getContext())
                .setContentHolder(new ViewHolder(R.layout.cooking_layout))
                .setGravity(Gravity.BOTTOM)
                .setCancelable(true)
                .setCancelable(false)
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
                        Navigation.findNavController(viewMain).navigate(R.id.action_chatterBox_to_homeFragment);

                    }
                }, 2500);


            }
        }, 2000);


        bottomSheetDialog.show();


    }

    @Override
    public void onListClick(CartModel cartModel) {

    }

    @Override
    public void onStart() {
        super.onStart();
        setUserInfo();
    }
}