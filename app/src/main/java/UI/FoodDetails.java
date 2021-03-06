package UI;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhive.MainActivity;
import com.example.foodhive.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Adapter.ReviewAdapter;
import Adapter.SimilarItemsAdapter;
import Constants.ShimmerConstants;
import Model.CartModel;
import Model.ChatModel;
import Model.FoodItems;
import ViewModel.FoodDetailsViewModel;


public class FoodDetails extends Fragment implements SimilarItemsAdapter.ListClickListener {

    //--UI--//
    private ImageView foodimg, imgplus, imgminus;
    private TextView foodname, foodprice, fooddes, plusminusnumber, ratingtext, cartPrice;
    private RecyclerView recyclerView;
    private AppCompatRatingBar appCompatRatingBar;
    private FloatingActionButton floatingActionButton;
    private LinearLayout addToCartLayout;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private NavController navController;
    private LinearLayout reviewLayout;
    private ImageView spinner;


    // -------- For Review UI ------ //
    private RecyclerView review_recycler;
    private EditText revieweditext;
    private ImageView reviewSend;
    private TextView reviewCount, see_all_Review;
    private LinearLayout linear_review, linearLayout_review_1;

    // -- Firebase --//
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceAllFood;
    private DatabaseReference databaseReferenceRat;
    private DatabaseReference databaseReferenceCart;
    private DatabaseReference databaseReferenceCart2;
    private DatabaseReference databaseReferenceCart3;
    private FirebaseAuth firebaseAuth;

    // -- Var -- //

    private List<FoodItems> foodItemsList;
    private SimilarItemsAdapter SimilarItemsAdapter;
    private int number = 0;
    private FoodItems foodItems;
    String rating = "0.0";
    private String phoneno = "-1";
    private int TotalPrice = 0;
    private int apatoto = 0, minusApatoto = 0;
    private String address = "";
    int prevQuantity = 0;
    int prevTotal = 0;
    int plusCount = 0, minusCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food_details, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CollapsingToolbarLayout layout = view.findViewById(R.id.collapse_foodDeatils);
        Toolbar toolbar = view.findViewById(R.id.toolbar_foodDeatils);


        NavController navControllertest = NavHostFragment.findNavController(this);

        //    MainActivity mainActivity = new MainActivity();
        // NavigationView navigationView = mainActivity.findViewById(R.id.nav_view);
//        mainActivity.setSupportActionBar(toolbar);


        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navControllertest.getGraph()).build();
        NavigationUI.setupWithNavController(layout, toolbar, navControllertest, appBarConfiguration);
//        NavigationUI.setupActionBarWithNavController(mainActivity, navControllertest);


        setHasOptionsMenu(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navControllertest.navigateUp();
            }
        });


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
        cartPrice = view.findViewById(R.id.food_details_cartprice);
        addToCartLayout = view.findViewById(R.id.food_details_addToCart);
        progressBar = view.findViewById(R.id.food_details_progressBar);
        nestedScrollView = view.findViewById(R.id.food_details_nestedScroll);
        reviewLayout = view.findViewById(R.id.review_dialog_linear_layout);


        // --------------- For Review ------------------- //
        review_recycler = getView().findViewById(R.id.review_recy_id);
        revieweditext = getView().findViewById(R.id.review_message_edittext);
        reviewSend = getView().findViewById(R.id.review_send_id);
        reviewCount = getView().findViewById(R.id.food_details_review_count);
        linear_review = getView().findViewById(R.id.linear_review);
        linearLayout_review_1 = getView().findViewById(R.id.bottomsheet1);
        spinner = view.findViewById(R.id.spinner);
        see_all_Review = view.findViewById(R.id.see_all_review);


        FoodDetailsArgs foodDetailsArgs = FoodDetailsArgs.fromBundle(getArguments());
        foodItems = foodDetailsArgs.getFooditem();

        /// Recycler ///

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        SimilarItemsAdapter = new SimilarItemsAdapter(getContext(), false, this);
        foodItemsList = new ArrayList<>();

        FoodDetailsViewModel foodDetailsViewModel = new ViewModelProvider(this).get(FoodDetailsViewModel.class);
        foodDetailsViewModel.getSimilarFood(foodItems.getType(), foodItems.getItemkey()).observe(getViewLifecycleOwner(), new Observer<List<FoodItems>>() {
            @Override
            public void onChanged(List<FoodItems> foodItems) {
                Log.d("list", foodItems.size() + "");

                SimilarItemsAdapter.setList(foodItems);

            }
        });
        recyclerView.setAdapter(SimilarItemsAdapter);
        //SimilarItemsAdapter.notifyDataSetChanged();


        if (foodItems != null) {

            Picasso.with(getContext()).load(foodItems.getImguri()).fit().centerCrop().placeholder(ShimmerConstants.getShimmer()).into(foodimg);
            foodname.setText(foodItems.getName());
            foodprice.setText(foodItems.getPrice() + "TK");
            fooddes.setText(foodItems.getDes());
            ratingtext.setText(foodItems.getRating());
            appCompatRatingBar.setRating(foodItems.getFloatrating());

        }

        navController = Navigation.findNavController(view);

        //--- Firebase ---//

        databaseReference = FirebaseDatabase.getInstance().getReference().child("FoodItems");
        databaseReferenceAllFood = FirebaseDatabase.getInstance().getReference().child("AllFood");
        databaseReferenceRat = FirebaseDatabase.getInstance().getReference();
        databaseReferenceCart = FirebaseDatabase.getInstance().getReference();
        databaseReferenceCart2 = FirebaseDatabase.getInstance().getReference();
        databaseReferenceCart3 = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();


        // All Value Event //

        String email = "";
        if (firebaseAuth.getCurrentUser() != null) {
            email = firebaseAuth.getCurrentUser().getEmail();
            String finalEmail1 = email;
            databaseReferenceCart.child("Info").child("Admin Info").child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String adminEmail = snapshot.getValue(String.class).toLowerCase();
                    if (adminEmail.equals(finalEmail1.toLowerCase())) {
                        firebaseAuth.signOut();
                        progressBar.setVisibility(View.INVISIBLE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.INVISIBLE);
                }
            });
        }


        if (firebaseAuth.getCurrentUser() != null) {

            // ------------- Notification for order status--------------- //
            // NotificationUser notificationUser = new NotificationUser(getContext(), firebaseAuth.getCurrentUser().getUid());
            //notificationUser.setNotificationOnNewOrder();
            // ------------ Notification for chat --------------------- //
            ;

            getQuantity();

            databaseReferenceCart.child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(firebaseAuth.getCurrentUser().getUid())) {
                        databaseReferenceCart.child("Cart").child(firebaseAuth.getCurrentUser().getUid()).child("TotalPrice").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Log.d("Price", snapshot.getValue(String.class) + "");
                                TotalPrice = Integer.parseInt(Objects.requireNonNull(snapshot.getValue(String.class)));
                                cartPrice.setText(TotalPrice + "TK");

                               /* progressBar.setVisibility(View.INVISIBLE);
                                nestedScrollView.setVisibility(View.VISIBLE);*/
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                              /*  progressBar.setVisibility(View.VISIBLE);
                                nestedScrollView.setVisibility(View.INVISIBLE);*/
                            }
                        });
                    } else {
                        TotalPrice = 0;
                      /*  progressBar.setVisibility(View.INVISIBLE);
                        nestedScrollView.setVisibility(View.VISIBLE);*/
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                   /* progressBar.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.INVISIBLE);*/
                }
            });

            progressBar.setVisibility(View.INVISIBLE);
            nestedScrollView.setVisibility(View.VISIBLE);

        } else {
            progressBar.setVisibility(View.INVISIBLE);
            nestedScrollView.setVisibility(View.VISIBLE);
        }


        databaseReference.child(foodItems.getType()).child(foodItems.getItemkey()).child("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String prevRating = snapshot.getValue(String.class);

                ratingtext.setText(prevRating);
                assert prevRating != null;
                appCompatRatingBar.setRating(Float.parseFloat(prevRating));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //--------- SimilarFoods ----------//


        /// --- Plus Minus & Listeners  --- ///


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


        addToCartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {
                    cartAddFirebase();
                    Log.d("phone", phoneno);

                    navController.navigate(R.id.action_foodDetails_to_chatterBox);
                } else
                    Snackbar.make(getView(), "Please Login", Snackbar.LENGTH_SHORT).show();
            }
        });


        imgplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ++plusCount;
                plusminusnumber.setText(number + plusCount + "");
                apatoto = plusCount * Integer.parseInt(foodItems.getPrice());

                cartPrice.setText(apatoto + TotalPrice + "TK");


            }
        });

        imgminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (plusCount + number > 0) { // number = 5 , plus = 0 ;
                    --plusCount; // 5 ;

                    plusminusnumber.setText(number + plusCount + "");
                    apatoto = plusCount * Integer.parseInt(foodItems.getPrice());

                    cartPrice.setText(TotalPrice + apatoto + "TK");

                }


            }
        });


        // ----------------------- Set up Review ---------------------- //

        setUpReview(); // ----------------------- Setup -------------------------//


        reviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayout_review_1.getVisibility() == View.VISIBLE) {
                    linearLayout_review_1.setVisibility(View.GONE);

                    spinner.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_down_24));
                } else {
                    linearLayout_review_1.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slider_slow_top);
                    linearLayout_review_1.startAnimation(animation);
                    spinner.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_up_24));
                    openReviewDialog(view);

                }
                Log.d("Review Clicked", "true");
            }
        });

        see_all_Review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayout_review_1.getVisibility() == View.VISIBLE) {
                    linearLayout_review_1.setVisibility(View.GONE);

                    spinner.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_down_24));
                } else {
                    linearLayout_review_1.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slider_slow_top);
                    linearLayout_review_1.startAnimation(animation);
                    spinner.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_up_24));
                    openReviewDialog(view);

                }
                Log.d("Review Clicked", "true");
            }
        });
        spinner.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {

                if (linearLayout_review_1.getVisibility() == View.VISIBLE) {
                    linearLayout_review_1.setVisibility(View.GONE);

                    spinner.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_down_24));
                } else {
                    linearLayout_review_1.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slider_slow_top);
                    linearLayout_review_1.startAnimation(animation);
                    spinner.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_drop_up_24));
                    openReviewDialog(view);

                }
                Log.d("Review Clicked", "true");
            }
        });


    }


    private void getQuantity() {

        databaseReferenceCart.child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())) {
                    databaseReferenceCart2.child("Cart").child(firebaseAuth.getCurrentUser().getUid()).child("CartItems").addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            if (snapshot2.hasChild(foodItems.getItemkey())) {

                                number = Integer.parseInt(Objects.requireNonNull(snapshot2.child(foodItems.getItemkey()).child("quantity").getValue()).toString());
                                plusminusnumber.setText(number + "");

                              /*  progressBar.setVisibility(View.INVISIBLE);
                                nestedScrollView.setVisibility(View.VISIBLE);*/
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                     /*       progressBar.setVisibility(View.INVISIBLE);
                            nestedScrollView.setVisibility(View.VISIBLE);*/
                        }
                    });
                }

                /*progressBar.setVisibility(View.INVISIBLE);
                nestedScrollView.setVisibility(View.VISIBLE);*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
       /*         progressBar.setVisibility(View.INVISIBLE);
                nestedScrollView.setVisibility(View.VISIBLE);*/
            }
        });

    }

    private void setUpReview() {
        // --------------  Recycler View  -------------------- //
        List<ChatModel> chatModels = new ArrayList<>();
        review_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ReviewAdapter reviewAdapter = new ReviewAdapter(getContext());

        // --------------  Firebase -------------------- //
        DatabaseReference databaseReferenceReview = FirebaseDatabase.getInstance().getReference().child("Reviews");


        databaseReferenceReview.child(foodItems.getItemkey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                reviewCount.setText(snapshot.getChildrenCount() + "");


                chatModels.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    chatModels.add(d.getValue(ChatModel.class));
                }

                reviewAdapter.setList(chatModels);
                review_recycler.setAdapter(reviewAdapter);
                reviewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openReviewDialog(View view) {


        // --------------  Firebase -------------------- //
        DatabaseReference databaseReferenceReview = FirebaseDatabase.getInstance().getReference().child("Reviews");
        DatabaseReference databaseReferenceUserName = FirebaseDatabase.getInstance().getReference().child("Info").child("Users Info");


        reviewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Snackbar.make(view, "Please Login", Snackbar.LENGTH_SHORT).show();

                } else {
                    String msg = revieweditext.getText().toString();

                    if (!msg.isEmpty()) {
                        revieweditext.setText("");
                        databaseReferenceUserName.child(firebaseAuth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String name = snapshot.getValue(String.class);
                                String time = System.currentTimeMillis() + "";
                                ChatModel chatModel = new ChatModel(msg, name, time, "false");
                                databaseReferenceReview.child(foodItems.getItemkey()).child(time).setValue(chatModel);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
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
                databaseReferenceAllFood.child(foodItems.getItemkey()).child("floatrating").setValue(-1f * ratingBarDialog.getRating());
                databaseReferenceAllFood.child(foodItems.getItemkey()).child("rating").setValue(ratingBarDialog.getRating() + "");
                databaseReferenceRat.child("Rating").child(foodItems.getItemkey()).child(uid).setValue(ratingBarDialog.getRating() + "").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        setUpNewRating();
                        dialog.dismiss();

                        Log.d("Rating", rating);

                    }
                });
            }
        });


    }

    private void setUpNewRating() {
        // Setting New Rating after user given a new rating ///

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
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                float twoDigitsF = Float.parseFloat(decimalFormat.format(total));
                ratingtext.setText(twoDigitsF + "");
                appCompatRatingBar.setRating(twoDigitsF);
                foodItems.setRating(twoDigitsF + "");
                databaseReference.child(foodItems.getType()).child(foodItems.getItemkey()).child("rating").setValue(String.valueOf(twoDigitsF));
                databaseReferenceAllFood.child(foodItems.getItemkey()).child("floatrating").setValue(-1f * twoDigitsF);
                databaseReferenceAllFood.child(foodItems.getItemkey()).child("rating").setValue(String.valueOf(twoDigitsF));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void cartAddFirebase() {


        TotalPrice += apatoto;
        number += plusCount;
        CartModel cartModel = new CartModel(foodItems.getItemkey(), foodItems.getName(), foodItems.getType(), number + "", foodItems.getPrice());
        number = 0;
        minusCount = 0;
        plusCount = 0;
        apatoto = 0;
        databaseReferenceCart.child("Cart").child(firebaseAuth.getCurrentUser().getUid()).child("TotalPrice").setValue(TotalPrice + "");
        databaseReferenceCart3.child("Cart").child(firebaseAuth.getCurrentUser().getUid()).child("CartItems").child(foodItems.getItemkey()).setValue(cartModel);
        TotalPrice = 0;

    }


    @Override
    public void onListClick(FoodItems foodItems) {
        FoodDetailsDirections.ActionFoodDetailsSelf action = FoodDetailsDirections.actionFoodDetailsSelf(foodItems);
        navController.navigate(action);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.toolbar_menu, menu);


    }
}