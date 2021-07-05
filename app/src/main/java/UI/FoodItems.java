package UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.service.autofill.Dataset;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodhive.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.ItemsAdapterAdmin;


public class FoodItems extends Fragment implements ItemsAdapterAdmin.ListClickListener {

    // ----- UI --------- ///
    private RecyclerView recyclerView;

    //--- Firebase Database ---///
    private DatabaseReference databaseReference;

    /// ------ Var ---- //
    private List<Model.FoodItems> foodItemsList;
    private String foodType = "";
    private ItemsAdapterAdmin itemsAdapterAdmin;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_food_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.fooditems_recy);

        //  ARGS //
        FoodItemsArgs args = FoodItemsArgs.fromBundle(getArguments());
        foodType = args.getFoodtype();

        //-- recy --//
        foodItemsList = new ArrayList<>();
        itemsAdapterAdmin = new ItemsAdapterAdmin(getContext(), false, this::onListClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // --Firebase Databse --//
        navController = Navigation.findNavController(view);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("FoodItems");

        databaseReference.child(foodType).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodItemsList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    if (!d.getKey().equals("catImage"))
                        foodItemsList.add(d.getValue(Model.FoodItems.class));
                }

                itemsAdapterAdmin.setList(foodItemsList);
                recyclerView.setAdapter(itemsAdapterAdmin);
                itemsAdapterAdmin.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onListClick(Model.FoodItems foodItems) {
        FoodItemsDirections.ActionFoodItemsToFoodDetails action = FoodItemsDirections.actionFoodItemsToFoodDetails(foodItems);
        navController.navigate(action);
    }
}