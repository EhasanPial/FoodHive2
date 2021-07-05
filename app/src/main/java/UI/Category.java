package UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import Adapter.CategoryAdapter;
import Model.CategoryModel;


public class Category extends Fragment implements CategoryAdapter.OnCatClick {

    // --- UI --- //
    private RecyclerView recyclerView;

    // --- Firebase --- //
    private DatabaseReference databaseReference;


    // ------- Var -------- //
    private NavController navController;
    private List<CategoryModel> categoryModelList;
    private CategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.foodcategory_recy);


        ///// -- recyceler ----/////
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3) ;
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        categoryModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), this);

        // -- Firebase - //

        navController = Navigation.findNavController(view);
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodItems");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot d : snapshot.getChildren()) {
                    CategoryModel categoryModel = new CategoryModel();
                    categoryModel.setCatname(d.getKey());
                    categoryModel.setCatimage(d.child("catImage").getValue(String.class));

                    categoryModelList.add(categoryModel);
                }


                categoryAdapter.setCategoryModelList(categoryModelList);
                recyclerView.setAdapter(categoryAdapter);
                categoryAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onCatClick(CategoryModel categoryModel) {
         CategoryDirections.ActionCategoryToFoodItems action = CategoryDirections.actionCategoryToFoodItems(categoryModel.getCatname()) ;
         navController.navigate(action);

    }
}