package Admin;

import android.content.DialogInterface;
import android.graphics.Path;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodhive.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import Adapter.ItemsAdapterAdmin;
import Model.FoodItems;


public class AdminFoodItems extends Fragment implements ItemsAdapterAdmin.ListClickListener {

    // ---- Ui --- //
    private RecyclerView recyclerView;
    private EditText foodname, foodcat, foodPrice, foodDes;
    private Button update;

    // -- VAR --- //
    private ItemsAdapterAdmin itemAdapterAdmin;
    private Query databaseReference;
    private List<FoodItems> foodCatList;
    NavController navController;
    private String FoodTYPE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_food_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.admin_food_items_recy_id);

        /// ---- getting Data --- -//
        AdminFoodItemsArgs args = AdminFoodItemsArgs.fromBundle(getArguments());
        FoodTYPE = args.getFoodtype();

        /// ---- FIerease --- ///

        if (FoodTYPE != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("FoodItems").child(FoodTYPE).orderByChild("time");

        }
        navController = Navigation.findNavController(view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemAdapterAdmin = new ItemsAdapterAdmin(getContext(), this::onListClick);
        foodCatList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodCatList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    foodCatList.add(d.getValue(FoodItems.class));
                }
                Collections.reverse(foodCatList);
                itemAdapterAdmin.setList(foodCatList);
                recyclerView.setAdapter(itemAdapterAdmin);
                itemAdapterAdmin.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        recyclerView.setAdapter(itemAdapterAdmin);

    }

    @Override
    public void onListClick(FoodItems foodItems) {

        AdminFoodItemsDirections.ActionAdminFoodItemsToSelectedItemEditAdmin actionAdminFoodItemsToSelectedItemEditAdmin = AdminFoodItemsDirections.actionAdminFoodItemsToSelectedItemEditAdmin(foodItems);
        Navigation.findNavController(getView()).navigate(actionAdminFoodItemsToSelectedItemEditAdmin);

    }


}