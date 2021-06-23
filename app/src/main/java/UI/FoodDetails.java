package UI;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.foodhive.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Adapter.SimilarItemsAdapter;
import Model.FoodItems;


public class FoodDetails extends Fragment implements SimilarItemsAdapter.ListClickListener {

    //--UI--//
    private ImageView foodimg, imgplus, imgminus;
    private TextView foodname, foodprice, fooddes, plusminusnumber, ratingtext;
    private RecyclerView recyclerView;
    private AppCompatRatingBar appCompatRatingBar;
    private FloatingActionButton floatingActionButton;

    // -- Firebase --//
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceRat;
    private FirebaseAuth firebaseAuth;

    // -- Var -- //
    private List<FoodItems> foodItemsList;
    private SimilarItemsAdapter SimilarItemsAdapter;
    private int number = 0;
    private FoodItems foodItems;
    String rating = "0.0";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        foodimg = view.findViewById(R.id.food_details_image);
        imgminus = view.findViewById(R.id.imgMinus);
        imgplus = view.findViewById(R.id.imgPlus);
        foodname = view.findViewById(R.id.food_details_name);
        foodprice = view.findViewById(R.id.food_details_price);
        fooddes = view.findViewById(R.id.food_details_des);
        plusminusnumber = view.findViewById(R.id.number_counter);
        recyclerView = view.findViewById(R.id.food_details_recy);
        appCompatRatingBar = view.findViewById(R.id.food_details_ratingbar);
        ratingtext = view.findViewById(R.id.food_details_ratingText);
        floatingActionButton = view.findViewById(R.id.floating_id);

        FoodDetailsArgs foodDetailsArgs = FoodDetailsArgs.fromBundle(getArguments());

        foodItems = foodDetailsArgs.getFooditem();
        if (foodItems != null) {
            Picasso.with(getContext()).load(foodItems.getImguri()).placeholder(R.drawable.pic_back).into(foodimg);
            foodname.setText(foodItems.getName());
            foodprice.setText(foodItems.getPrice() + "TK");
            fooddes.setText(foodItems.getDes());
            ratingtext.setText(foodItems.getRating());
            appCompatRatingBar.setRating(foodItems.getFloatRating());

        }

        NavController navController = Navigation.findNavController(view);

        //--- Firebase ---//

        databaseReference = FirebaseDatabase.getInstance().getReference().child("FoodItems");
        databaseReferenceRat = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        /// Recycler ///

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        SimilarItemsAdapter = new SimilarItemsAdapter(getContext(), false, this);
        foodItemsList = new ArrayList<>();


        databaseReferenceRat.child("Rating").child(foodItems.getItemkey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float one = 0, two = 0, three = 0, four = 0, five = 0;
                for (DataSnapshot d : snapshot.getChildren()) {
                    float rat = Float.parseFloat(d.getValue(String.class));
                    if (rat == 1f) ++one;
                    else if (rat == 2f) ++two;
                    else if (rat == 3f) ++three;
                    else if (rat == 4f) ++four;
                    else ++five;
                }

                float total = (one * 1f + two * 2f + three * 3f + four * 4f + 5f * five) / (one + two + three + four + five);
                ratingtext.setText(total + "");
                appCompatRatingBar.setRating(total);
                foodItems.setRating(total + "");
                databaseReference.child(foodItems.getType()).child(foodItems.getItemkey()).child("rating").setValue(String.valueOf(total));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child(foodItems.getType()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodItemsList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    foodItemsList.add(d.getValue(FoodItems.class));
                }

                SimilarItemsAdapter.setList(foodItemsList);
                recyclerView.setAdapter(SimilarItemsAdapter);
                SimilarItemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        /// --- Plus Minus --- ///

        imgplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++number;
                plusminusnumber.setText(number + "");
            }
        });
        imgminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number > 0) --number;
                plusminusnumber.setText(number + "");
                plusminusnumber.setAnimation(new Animation() {
                    @Override
                    public void start() {
                        super.start();
                    }
                });
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Snackbar.make(getView(), "Please Login", Snackbar.LENGTH_SHORT).show();
                } else {
                    setUpRatingDialouge();
                }
            }
        });


    }

    private void setUpRatingDialouge() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_rating);
        dialog.setCancelable(true);
        dialog.setTitle("Let us know your taste");
        AppCompatRatingBar ratingBarDialog = dialog.findViewById(R.id.dialog_ratingBar);
        Button submit = dialog.findViewById(R.id.rating_submit);
        dialog.show();


        String uid = firebaseAuth.getCurrentUser().getUid();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReferenceRat.child("Rating").child(foodItems.getItemkey()).child(uid).setValue(ratingBarDialog.getRating()+"").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        dialog.dismiss();
                        Log.d("Rating", rating);

                    }
                });
            }
        });


    }

    @Override
    public void onListClick(FoodItems foodItems) {

    }


}