package Admin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.foodhive.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Model.FoodItems;


public class AddNewFood extends Fragment {

    private EditText foodname, foodPrice, fooddes;
    private AppCompatAutoCompleteTextView foodCat;
    private ImageView foodImg;
    private Button uploadbutton;
    private Uri imageUri;


    String nametext, catText, pricetext, destext;
    private List<String> AllcatList;

    // ---- Firebase --- //
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceAllFood;

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


        // ------------- Notification for new order --------------- //
        NotificationAdmin notificationAdmin = new NotificationAdmin(getContext());
        notificationAdmin.setNotificationOnNewOrder();


        AllcatList = new ArrayList<>();
        foodCat.setThreshold(1);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren())
                    AllcatList.add(d.getKey());
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, AllcatList);
                foodCat.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        foodImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 12);
        });

        uploadbutton.setOnClickListener(v -> {
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
                uploadTask(key, view);
            }


        });
    }

    private void uploadTask(String key, View view) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Adding new Food");
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        assert bitmap != null;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();


        storageReference.child(key).putBytes(data).addOnSuccessListener(taskSnapshot -> storageReference.child(key).getDownloadUrl().addOnSuccessListener(uri -> {
            String time = String.valueOf(System.currentTimeMillis());
            FoodItems foodItems = new FoodItems(nametext, pricetext, uri.toString(), catText, key, time, destext, "0", 0f, "true");
            databaseReferenceAllFood.child(key).setValue(foodItems);
            databaseReference.child(catText).child("catImage").setValue(uri.toString());
            databaseReference.child(catText).child(key).setValue(foodItems).addOnSuccessListener(aVoid -> {
                progressDialog.dismiss();
                Snackbar.make(view, "Upload Successful", Snackbar.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.action_addNewFood_to_adminFragment);

            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Snackbar.make(view, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
            });
        })).addOnProgressListener(snapshot -> {
            ////// ------------------------PROGRESS BAR _____________////

        }).addOnFailureListener(e -> {
            Snackbar.make(view, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 12 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.with(getContext()).load(imageUri).placeholder(R.drawable.pic_back).into(foodImg);
        }
    }
}