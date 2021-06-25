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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.foodhive.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.ChatterAdapter;
import Model.CartModel;
import Model.OrderList;


public class ChatterBox extends Fragment implements ChatterAdapter.ListClickListener {

    // UI //
    private TextView subtotal, total;
    private EditText phone, address;
    private Button placeoder;
    private RecyclerView recyclerView;

    // -- Firebase --//
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceOrder;

    //--Variable--//
    private List<CartModel> cartModelList;
    private String phonetext;
    private String addresstext;
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

        //-- Arguments--//
        if (getArguments() != null) {
            ChatterBoxArgs args = ChatterBoxArgs.fromBundle(getArguments());
            addresstext = args.getGetAddress();
            phonetext = args.getGetPhone();
            address.setText(addresstext);
            phone.setText(phonetext);
        }

        // --- RecycleView ----//
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatterAdapter = new ChatterAdapter(getContext(), this::onListClick);

        //-- FireabaseDatase--//
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart");
        databaseReferenceOrder = FirebaseDatabase.getInstance().getReference().child("Order");
        cartModelList = new ArrayList<>();

        //// Cart Items List Setup ////
        databaseReference.child(phonetext).child("CartItems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartModelList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {

                    CartModel cartModel = d.getValue(CartModel.class);
                    if (!cartModel.getQuantity().equals("0"))
                        cartModelList.add(cartModel);
                }

                chatterAdapter.setList(cartModelList);
                recyclerView.setAdapter(chatterAdapter);
                chatterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child(phonetext).child("TotalPrice").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String subtotalString = snapshot.getValue(String.class);
                subtotal.setText(subtotalString + " TK");
                total.setText(Integer.parseInt(subtotalString) + 30 + " TK");
                totalText = Integer.parseInt(subtotalString) + 30 + " TK";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        placeoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOder();
            }
        });


    }

    private void placeOder() {

        String orderId = databaseReferenceOrder.push().getKey();

        for (int i = 0; i < cartModelList.size(); i++) {

            databaseReferenceOrder.child(orderId).child("cartItems").child(cartModelList.get(i).getItemkey()).setValue(cartModelList.get(i));

        }

        OrderList orderList = new OrderList(addresstext, phonetext,"Uncomplete",totalText,orderId);


        databaseReferenceOrder.child(orderId).child("others").setValue(orderList);


    }

    @Override
    public void onListClick(CartModel cartModel) {

    }
}