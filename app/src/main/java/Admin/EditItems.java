package Admin;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

import Adapter.EditItemAdapter;


public class EditItems extends Fragment implements EditItemAdapter.ListClickListener {

    // ---- Ui --- //
    private RecyclerView recyclerView;

    // -- VAR --- //
    private EditItemAdapter editItemAdapter;
    private DatabaseReference databaseReference;
    private List<String> foodCatList;
    NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.admin_editItem_id);

        /// ---- FIerease --- ///

        databaseReference = FirebaseDatabase.getInstance().getReference("FoodItems");
        navController = Navigation.findNavController(view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        editItemAdapter = new EditItemAdapter(getContext(), this::onListClick);
        foodCatList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodCatList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    foodCatList.add(d.getKey());
                }

                editItemAdapter.setList(foodCatList);
                recyclerView.setAdapter(editItemAdapter);
                editItemAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        recyclerView.setAdapter(editItemAdapter);

    }

    @Override
    public void onListClick(String s) {
        Log.d("fra","clicked") ;

        EditItemsDirections.ActionEditItemsToAdminFoodItems actionEditItemsToAdminFoodItems = EditItemsDirections.actionEditItemsToAdminFoodItems(s);
        Navigation.findNavController(getView()).navigate(actionEditItemsToAdminFoodItems);
    }


}