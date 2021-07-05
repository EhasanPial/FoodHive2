package Admin;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodhive.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.TopItemsAdapter;
import Model.FoodItems;


public class TopItemsFragment extends Fragment implements TopItemsAdapter.ListClickListener {

    /// --- UI ---- //
    private RecyclerView recyclerView;


    /// ------ Firebase ------ ///
    private Query databaseReference;
    private DatabaseReference databaseReferenceSetTop;

    // -- Var -- //
    private List<FoodItems> foodItemsList;
    private TopItemsAdapter topItemsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.top_items_recy);


        // --- firebase ---- //

        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllFood").orderByChild("istop");
        databaseReferenceSetTop = FirebaseDatabase.getInstance().getReference().child("AllFood");


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        foodItemsList = new ArrayList<>();
        topItemsAdapter = new TopItemsAdapter(getContext(), this);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodItemsList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    foodItemsList.add(d.getValue(FoodItems.class));
                }

                topItemsAdapter.setList(foodItemsList);
                recyclerView.setAdapter(topItemsAdapter);
                topItemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onListClick(FoodItems foodItems) {

        databaseReferenceSetTop.child(foodItems.getItemkey()).child("istop").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String istop = snapshot.getValue(String.class);
                if (istop.equals("false")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Delete item from top?");
                    builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            databaseReferenceSetTop.child(foodItems.getItemkey()).child("istop").setValue("true");
                            topItemsAdapter.notifyDataSetChanged();


                        }
                    });

                    builder.create().show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Add this item to top?");
                    builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            databaseReferenceSetTop.child(foodItems.getItemkey()).child("istop").setValue("false");
                            topItemsAdapter.notifyDataSetChanged();

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
}