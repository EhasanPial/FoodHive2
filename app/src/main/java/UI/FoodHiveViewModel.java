package UI;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Model.CategoryModel;
import Model.FoodItems;

public class FoodHiveViewModel extends AndroidViewModel {

    public List<Model.FoodItems> foodItemsList;
    public List<Model.FoodItems> sliderList;
    public List<Model.CategoryModel> allCat;
    public MutableLiveData<List<Model.FoodItems>> listMutableLiveData;
    public MutableLiveData<List<Model.FoodItems>> listMutableLiveDataSlider;
    public MutableLiveData<List<Model.CategoryModel>> listMutableLiveDataCat;
    private Query databaseReference;
    private DatabaseReference databaseReferenceCat;

    public FoodHiveViewModel(@NonNull Application application) {
        super(application);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllFood").orderByChild("floatrating");
        databaseReferenceCat = FirebaseDatabase.getInstance().getReference("FoodItems");
        foodItemsList = new ArrayList<>();
        sliderList = new ArrayList<>();
        allCat = new ArrayList<>();
        listMutableLiveData = new MutableLiveData<>();
        listMutableLiveDataSlider = new MutableLiveData<>();
        listMutableLiveDataCat = new MutableLiveData<>();
    }


    public LiveData<List<FoodItems>> fetch() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                foodItemsList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {


                    //--- recycler food items  list --- //
                    foodItemsList.add(d.getValue(Model.FoodItems.class));


                }


                listMutableLiveData.setValue(foodItemsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return listMutableLiveData;
    }

    public LiveData<List<FoodItems>> fetchSliderItems() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sliderList.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    FoodItems foodItems = d.getValue(FoodItems.class);
                    assert foodItems != null;
                    if (foodItems.getIstop().equals("false")) {
                        sliderList.add(foodItems);
                        if (sliderList.size() >= 5) break;

                    }


                }
                listMutableLiveDataSlider.setValue(sliderList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return listMutableLiveDataSlider;
    }

    public LiveData<List<CategoryModel>> fetchCat() {
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
                listMutableLiveDataCat.setValue(allCat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listMutableLiveDataCat.setValue(allCat);
        return listMutableLiveDataCat;
    }

}
