package Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.foodhive.R;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import Constants.ShimmerConstants;
import Model.FoodItems;


public class SelectedItemEditAdmin extends Fragment {

    private EditText name, price, type, des;
    private Button upload;
    private ImageView foodpic;


    ///--------- Firebase -----///
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceAllFood;
    private DatabaseReference databaseReferenceNEW;

    ///----- Variable --------///
    String nameText, priceText, typeText, desText, ratingText;
    public FoodItems foodItems;
    private Uri imageUri, imageUri2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_item_dialouge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /// ----- Finding --------//
        name = view.findViewById(R.id.admin_foodEdit_text);
        price = view.findViewById(R.id.admin_foodEdit_price);
        type = view.findViewById(R.id.admin_foodEdit_cat);
        des = view.findViewById(R.id.admin_foodEdit_des);
        upload = view.findViewById(R.id.admin_foodEdit_update);
        foodpic = view.findViewById(R.id.admin_foodEdit_pic);

        SelectedItemEditAdminArgs selectedItemEditAdminArgs = SelectedItemEditAdminArgs.fromBundle(getArguments());

        // -- Setting Edit Text --- ///
        foodItems = selectedItemEditAdminArgs.getFooditem();
        name.setText(foodItems.getName());
        price.setText(foodItems.getPrice());
        type.setText(foodItems.getType());
        des.setText(foodItems.getDes());
        imageUri2 = Uri.parse(foodItems.getImguri());
        imageUri = Uri.parse(foodItems.getImguri());
        Log.d("Image 1 ", imageUri.toString());

        //-------- Setting Pervious-----//
/*        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setDuration(1000)
                .setBaseAlpha(0.7f)
                .setHighlightAlpha(0.6f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);*/
        Picasso.with(getContext()).load(foodItems.getImguri()).placeholder(ShimmerConstants.getShimmer()).into(foodpic);


        // -------- Firebase ---//
        storageReference = FirebaseStorage.getInstance().getReference("FoodItems");
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodItems");
        databaseReferenceAllFood = FirebaseDatabase.getInstance().getReference("AllFood");


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameText = name.getText().toString();
                priceText = price.getText().toString();
                typeText = type.getText().toString();
                desText = des.getText().toString();
                ratingText = foodItems.getRating();

                databaseReference.child(foodItems.getType()).child(foodItems.getItemkey()).removeValue();
                if (imageUri.equals(imageUri2)) updateWithOutImage(foodItems.getItemkey());
                else
                    update(foodItems.getItemkey());


            }
        });


        foodpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 13);
            }
        });


    }

    private void updateWithOutImage(String key) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Adding new Food");
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        FoodItems foodItem = new FoodItems(nameText, priceText, foodItems.getImguri(), typeText, key, foodItems.getTime(), desText,getRatingText(), -1f*Float.parseFloat(getRatingText()));
        databaseReferenceAllFood.child(key).setValue(foodItem) ;
        databaseReference.child(typeText).child(key).setValue(foodItem).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Snackbar.make(getView(), "Update Successful", Snackbar.LENGTH_SHORT).show();
                Navigation.findNavController(getView()).navigate(R.id.action_selectedItemEditAdmin_to_editItems);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void update(String key) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Adding new Food");
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        storageReference.child(key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                storageReference.child(key).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.child(key).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri dwn = uri;
                                Log.d("Image 2", dwn.toString());
                                FoodItems foodItem = new FoodItems(nameText, priceText, dwn.toString(), typeText, key, foodItems.getTime(), desText, getRatingText());
                                databaseReferenceAllFood.child(key).setValue(foodItem) ;
                                databaseReference.child(typeText).child(key).setValue(foodItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Snackbar.make(getView(), "Update Successful", Snackbar.LENGTH_SHORT).show();
                                        Navigation.findNavController(getView()).navigate(R.id.action_selectedItemEditAdmin_to_editItems);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        ////// ------------------------PROGRESS BAR _____________////

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });

    }


    public String getRatingText()
    {
        databaseReference.child(typeText).child(foodItems.getItemkey()).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ratingText = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return ratingText ;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 13 && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.with(getContext()).load(imageUri).placeholder(ShimmerConstants.getShimmer()).into(foodpic);
        }
    }



}