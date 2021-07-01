package Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.foodhive.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import Model.FoodItems;


public class AddNewFood extends Fragment {

    private EditText foodname, foodCat, foodPrice, fooddes;
    private ImageView foodImg;
    private Button uploadbutton;
    private Uri imageUri;


    String nametext, catText, pricetext, destext;

    // ---- Firebase --- //
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceAllFood;
    private DatabaseReference databaseReferenceRating;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_new_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        foodname = view.findViewById(R.id.admin_food_id);
        foodCat = view.findViewById(R.id.admin_food_cat);
        foodPrice = view.findViewById(R.id.admin_food_price);
        foodImg = view.findViewById(R.id.admin_food_pic);
        fooddes = view.findViewById(R.id.admin_food_des);
        uploadbutton = view.findViewById(R.id.admin_food_button_id);

        // --------- Firebae ------ //
        storageReference = FirebaseStorage.getInstance().getReference("FoodItems");
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodItems");
        databaseReferenceAllFood = FirebaseDatabase.getInstance().getReference("AllFood");
        databaseReferenceRating = FirebaseDatabase.getInstance().getReference("Rating");


        foodImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 12);
            }
        });

        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadbutton.setEnabled(false);


                nametext = foodname.getText().toString();
                catText = foodCat.getText().toString();
                pricetext = foodPrice.getText().toString();
                destext = fooddes.getText().toString();


                if (nametext.isEmpty()) {
                    foodname.setError("Enter your name");
                    foodname.requestFocus();
                    uploadbutton.setEnabled(true);
                } else if (catText.isEmpty()) {
                    foodCat.setError("Enter your email");
                    foodCat.requestFocus();
                    uploadbutton.setEnabled(true);

                } else if (pricetext.isEmpty()) {
                    foodPrice.setError("Enter your email");
                    foodPrice.requestFocus();
                    uploadbutton.setEnabled(true);

                } else if (destext.isEmpty()) {
                    fooddes.setError("Enter your email");
                    fooddes.requestFocus();
                    uploadbutton.setEnabled(true);

                } else if (imageUri == null) {
                    Snackbar.make(view, "Select an image", Snackbar.LENGTH_SHORT).show();
                    uploadbutton.setEnabled(true);
                } else {


                    String key = databaseReference.push().getKey();
                    uploadTask(key);
                }


            }
        });
    }

    private void uploadTask(String key) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Adding new Food");
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
        }catch (IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 15,byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();


        storageReference.child(key).putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(key).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri dwn = uri;
                        String time = String.valueOf(System.currentTimeMillis());
                        FoodItems foodItems = new FoodItems(nametext, pricetext, dwn.toString(), catText, key, time, destext, "0", 0f, "true");
                        databaseReferenceAllFood.child(key).setValue(foodItems) ;
                        databaseReference.child(catText).child(key).setValue(foodItems).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Snackbar.make(getView(), "Upload Successful", Snackbar.LENGTH_SHORT).show();
                                Navigation.findNavController(getView()).navigate(R.id.action_addNewFood_to_adminFragment);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        }) ;
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 12 && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.with(getContext()).load(imageUri).placeholder(R.drawable.pic_back).into(foodImg);
        }
    }
}