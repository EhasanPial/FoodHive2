package UI;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.foodhive.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import Adapter.EditItemAdapter;
import Adapter.ItemsAdapterAdmin;
import Adapter.SliderAdapter;
import Constants.BaseString;
import Model.FoodItems;


public class FoodHive extends Fragment implements SliderAdapter.OnClick, EditItemAdapter.ListClickListener, ItemsAdapterAdmin.ListClickListener {

    // --- Var ---//
    private FirebaseAuth firebaseAuth;
    private NavController navController;
    private DatabaseReference databaseReference;
    String email;
    private List<String> allCat;
    private List<FoodItems> foodItemsList;

    private EditItemAdapter editItemAdapter;
    private ItemsAdapterAdmin itemsAdapterAdmin;
    private SliderAdapter sliderAdapter;

    //- ---- UI --- //
    private RecyclerView recyclerView;
    private com.smarteist.autoimageslider.SliderView sliderView;
    private SearchView searchView;

    // ---- Fireabase---//


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyler_items_home);
        sliderView = view.findViewById(R.id.imageSlider);
        searchView = view.findViewById(R.id.search_id);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("FoodItems");
        navController = Navigation.findNavController(view);

        // navController.navigate(R.id.action_homeFragment_to_adminFragment);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /// -------------- Adapters ------------ ///
        editItemAdapter = new EditItemAdapter(getContext(), this::onListClick);
        itemsAdapterAdmin = new ItemsAdapterAdmin(getContext(), false, this::onListClick);

        ///----------------------------------- Slider Adapter---------------------////
        sliderAdapter = new SliderAdapter(getContext(), this::onClick);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorMargins(0, 40, 0, 10);
        sliderView.setIndicatorSelectedColor(Color.parseColor("#fbc02d"));
        sliderView.setIndicatorUnselectedColor(Color.parseColor("#50FAFAFA"));
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();
        ///----------------------------------- Slider Adapter---------------------////


        allCat = new ArrayList<>();
        foodItemsList = new ArrayList<>();


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodItemsList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FoodItems").child(d.getKey());
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                foodItemsList.add(child.getValue(FoodItems.class));

                            }

                            Collections.sort(foodItemsList, Collections.reverseOrder());

                            itemsAdapterAdmin.setList(foodItemsList);
                            recyclerView.setAdapter(itemsAdapterAdmin);
                            itemsAdapterAdmin.notifyDataSetChanged();

                            List<FoodItems> sliderList = new ArrayList<>();
                            int size = foodItemsList.size();
                            for (int i = 0; i < size; i++) {
                                if (i > 4) break;
                                sliderList.add(foodItemsList.get(i));
                            }
                            sliderAdapter.addItem(sliderList);
                            sliderView.setSliderAdapter(sliderAdapter);
                            sliderAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // recyclerView.setAdapter(editItemAdapter);
    }


    @Override
    public void onClick(FoodItems foodItems) {

    }

    @Override
    public void onListClick(String s) {

    }

    @Override
    public void onListClick(FoodItems foodItems) {
            FoodHiveDirections.ActionHomeFragmentToFoodDetails aciton = FoodHiveDirections.actionHomeFragmentToFoodDetails(foodItems) ;
            Navigation.findNavController(getView()).navigate(aciton);
    }
}