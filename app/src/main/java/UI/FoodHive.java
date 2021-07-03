package UI;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.foodhive.MainActivity;
import com.example.foodhive.R;
import com.google.android.material.navigation.NavigationView;
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

import Adapter.CategoryAdapterHome;
import Adapter.EditItemAdapter;
import Adapter.ItemsAdapterAdmin;
import Adapter.SliderAdapter;
import Constants.BaseString;
import Model.CategoryModel;
import Model.FoodItems;


public class FoodHive extends Fragment implements SliderAdapter.OnClick, EditItemAdapter.ListClickListener, ItemsAdapterAdmin.ListClickListener, CategoryAdapterHome.OnCatClick {

    // --- Var ---//

    private NavController navController;
    private List<CategoryModel> allCat;
    private List<FoodItems> foodItemsList;
    public static boolean logged = false;
    private String AdminUI = "5lUy85NSOTgiLEXqpN0cGaji6tx2";

     /// --- Adapters --- //
    private EditItemAdapter editItemAdapter ;
    private ItemsAdapterAdmin itemsAdapterAdmin;
    private SliderAdapter sliderAdapter;
    private CategoryAdapterHome categoryAdapterHome;

    // --- Firebase --- //
    private Query databaseReference;
    private DatabaseReference databaseReferenceAdmin;
    private DatabaseReference databaseReferenceTop;
    private DatabaseReference databaseReferenceCat;
    private DatabaseReference databaseReferenceOpenClose;
    private FirebaseAuth firebaseAuth;

    //- ---- UI --- //

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewCat;
    private com.smarteist.autoimageslider.SliderView sliderView;
    private SearchView searchView;
    private TextView open_res;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // --------------------------------------------------------------*****Firebase*****--------------------------------------------------- //

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllFood").orderByChild("floatrating");
        databaseReferenceCat = FirebaseDatabase.getInstance().getReference("FoodItems");
        databaseReferenceAdmin = FirebaseDatabase.getInstance().getReference().child("Info").child("Admin Info");
        databaseReferenceOpenClose = FirebaseDatabase.getInstance().getReference().child("Open Close");
        navController = Navigation.findNavController(view);


        if (firebaseAuth.getCurrentUser() != null) {

            String uid = firebaseAuth.getCurrentUser().getUid();

            if (uid.equals(AdminUI)) {
                navController.navigate(R.id.action_homeFragment_to_adminFragment);
                return;
            }

            databaseReferenceAdmin.child("uid").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String AdminUIDBackUp = snapshot.getValue(String.class).toLowerCase();

                    if (uid.toLowerCase().equals(AdminUIDBackUp)) {
                        navController.navigate(R.id.action_homeFragment_to_adminFragment);
                        return;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        // ------------------------------------------------------------------ Find View by ID --------------------------------------------------------------- //
        recyclerView = view.findViewById(R.id.recyler_items_home);
        sliderView = view.findViewById(R.id.imageSlider);
        searchView = view.findViewById(R.id.search_id);
        open_res = view.findViewById(R.id.open_res_user);
        recyclerViewCat = view.findViewById(R.id.cat_recy_items_home);

        databaseReferenceOpenClose.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String isOpen = snapshot.getValue(String.class);
                if (isOpen.equals("false")) {
                    open_res.setVisibility(View.VISIBLE);
                    open_res.setText("Restaurant is closed now");
                    open_res.setTextColor(getActivity().getResources().getColor(R.color.white));
                    open_res.setBackground(getActivity().getResources().getDrawable(R.drawable.res_closed_back));
                } else {
                    open_res.setVisibility(View.GONE);
                }
                Log.d("isopen user", isOpen);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCat.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        /// -------------- Adapters ------------ ///
        editItemAdapter = new EditItemAdapter(getContext(), this::onListClick);
        itemsAdapterAdmin = new ItemsAdapterAdmin(getContext(), false, this::onListClick);
        categoryAdapterHome = new CategoryAdapterHome(getContext(), this::onCatClick);

        ///----------------------------------- Slider Adapter---------------------////
        sliderAdapter = new SliderAdapter(getContext(), this::onSlideClick);
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

        List<FoodItems> sliderList = new ArrayList<>();

        // ------------------------------------------------------------------------------ Getting Food First Slider then Recycler ------------------------------------------  //

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sliderList.clear();
                int count = 1;
                for (DataSnapshot d : snapshot.getChildren()) {
                    FoodItems foodItems = d.getValue(FoodItems.class);
                    if (foodItems.getIstop().equals("false")) {
                        sliderList.add(foodItems);
                        sliderAdapter.addItem(sliderList);
                    }

                    if (count > 7) break;
                    ;
                }

                sliderView.setSliderAdapter(sliderAdapter);
                sliderAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceCat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allCat.clear();
                for (DataSnapshot d : snapshot.getChildren()) {

                    // ----------------- Setting up category List ----------------- //
                    CategoryModel categoryModel = new CategoryModel();
                    categoryModel.setCatname(d.getKey());
                    categoryModel.setCatimage(d.child("catImage").getValue(String.class));
                    allCat.add(categoryModel);


                }

                categoryAdapterHome.setCategoryModelList(allCat);
                recyclerViewCat.setAdapter(categoryAdapterHome);
                categoryAdapterHome.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // -------- getting recycler ------- //
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodItemsList.clear();

                for (DataSnapshot d : snapshot.getChildren()) {


                    //--- recycler food items  list --- //
                    foodItemsList.add(d.getValue(FoodItems.class));
                    itemsAdapterAdmin.setList(foodItemsList);
                    recyclerView.setAdapter(itemsAdapterAdmin);


                }


                // ---- recycler item set -----//
                itemsAdapterAdmin.setList(foodItemsList);
                recyclerView.setAdapter(itemsAdapterAdmin);
                itemsAdapterAdmin.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                itemsAdapterAdmin.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemsAdapterAdmin.getFilter().filter(newText);
                return false;
            }
        });
    }


    @Override
    public void onSlideClick(FoodItems foodItems) {
        FoodHiveDirections.ActionHomeFragmentToFoodDetails aciton = FoodHiveDirections.actionHomeFragmentToFoodDetails(foodItems);
        Navigation.findNavController(getView()).navigate(aciton);
    }

    @Override
    public void onListClick(String s) {

    }

    @Override
    public void onListClick(FoodItems foodItems) {
        FoodHiveDirections.ActionHomeFragmentToFoodDetails aciton = FoodHiveDirections.actionHomeFragmentToFoodDetails(foodItems);
        Navigation.findNavController(getView()).navigate(aciton);
    }

    @Override
    public void onCatClick(CategoryModel categoryModel) {
        // cathome click
        FoodHiveDirections.ActionHomeFragmentToFoodItems action = FoodHiveDirections.actionHomeFragmentToFoodItems(categoryModel.getCatname());
        navController.navigate(action);

    }
}