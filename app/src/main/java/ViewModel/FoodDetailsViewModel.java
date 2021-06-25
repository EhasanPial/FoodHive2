package ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Model.FoodItems;

public class FoodDetailsViewModel extends AndroidViewModel {

    DatabaseReference databaseReference ;
    private MutableLiveData<List<FoodItems>> foodItemsList ;
    private List<FoodItems> foodItems ;

    public FoodDetailsViewModel(@NonNull Application application) {
        super(application);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("FoodItems");
        foodItemsList = new MutableLiveData<>() ;
        foodItems = new ArrayList<>() ;
    }


    public LiveData<List<FoodItems>>  getSimilarFood(String foodType)
    {
        databaseReference.child(foodType).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot d : snapshot.getChildren()) {
                    FoodItems food = d.getValue(FoodItems.class) ;
                    if(food.getItemkey()!=foodType)
                        foodItems.add(food) ;
                }
                foodItemsList.setValue(foodItems);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return  foodItemsList;
    }



}
