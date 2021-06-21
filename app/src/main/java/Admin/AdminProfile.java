package Admin;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.foodhive.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.UsersModel;


public class AdminProfile extends Fragment {

    private EditText name, email, password, phone;
    private Button Update;

    /// --- Firebase ----///
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    // -- Variable --//
    public UsersModel usersModel;
    private String nameText, emailText, passwordText, phoneText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = view.findViewById(R.id.admin_profle_username);
        email = view.findViewById(R.id.admin_profle_email);
        password = view.findViewById(R.id.admin_profle_password);
        phone = view.findViewById(R.id.admin_profle_phone);
        Update = view.findViewById(R.id.admin_profle_update);



        NavController navController = Navigation.findNavController(view);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Info").child("Admin Info");
        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersModel = snapshot.getValue(UsersModel.class);
                name.setText(usersModel.getName());
                email.setText(usersModel.getEmail());
                password.setText(usersModel.getPass());
                phone.setText(usersModel.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameText = name.getText().toString();
                emailText = email.getText().toString();
                passwordText = password.getText().toString();
                phoneText = phone.getText().toString();

                UsersModel users = new UsersModel(nameText, emailText, passwordText, usersModel.getAddress(), phoneText);
                databaseReference.setValue(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        firebaseAuth.getCurrentUser().updateEmail(emailText);
                        firebaseAuth.getCurrentUser().updatePassword(passwordText);
                        Snackbar.make(getView(), "Your Profile is Updated", Snackbar.LENGTH_SHORT).show();

                        navController.navigate(R.id.action_adminProfile_to_adminFragment);
                    }
                });
            }
        });


    }
}