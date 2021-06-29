package UI;

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
import android.widget.Toast;

import com.example.foodhive.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.UsersModel;


public class SignUp extends Fragment {
    // --- UI --//
    private EditText username, email, pass, address, phone;
    private Button signUp;

    // --- Vairable --- //
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    String nameText, emailtext, passtext, addresstext, phonetext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //--------- Find -------//

        username = view.findViewById(R.id.userNameSignUp);
        email = view.findViewById(R.id.emailSignUp);
        pass = view.findViewById(R.id.passwordSignUp);
        address = view.findViewById(R.id.addressSignUp);
        phone = view.findViewById(R.id.phoneSignUp);
        signUp = view.findViewById(R.id.buttonSignUp);


        // ---------- Firebase --------- //
        firebaseAuth = FirebaseAuth.getInstance();


        databaseReference = FirebaseDatabase.getInstance().getReference("Info");

        NavController navController = Navigation.findNavController(view);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp.setEnabled(false);

                nameText = username.getText().toString().toUpperCase();

                emailtext = email.getText().toString();
                passtext = pass.getText().toString();
                addresstext = address.getText().toString();
                phonetext = phone.getText().toString();

                if (nameText.isEmpty()) {
                    username.setError("Enter your name");
                    username.requestFocus();
                    signUp.setEnabled(true);
                } else if (emailtext.isEmpty()) {
                    email.setError("Enter your email");
                    email.requestFocus();
                    signUp.setEnabled(true);

                } else if (passtext.isEmpty()) {
                    pass.setError("Enter Password");
                    pass.requestFocus();
                    signUp.setEnabled(true);

                } else if (addresstext.isEmpty()) {
                    address.setError("Enter your address");
                    address.requestFocus();
                    signUp.setEnabled(true);

                } else if (phonetext.isEmpty()) {

                    phone.setError("Enter your phone");
                    phone.requestFocus();
                    signUp.setEnabled(true);

                }

                databaseReference.child("Users Info").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.hasChild(phonetext)) {
                            firebaseAuth.createUserWithEmailAndPassword(emailtext, passtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        UsersModel usersModel = new UsersModel(nameText, emailtext, passtext, addresstext, phonetext, "0");
                                        databaseReference.child("Users Info").child(firebaseAuth.getCurrentUser().getUid()).setValue(usersModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                FoodHive.logged = true;
                                                navController.navigate(R.id.action_signUp_to_homeFragment);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                signUp.setEnabled(true);
                                                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Snackbar.make(view, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            phone.setError("Already Used");
                            phone.requestFocus();
                            signUp.setEnabled(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_SHORT).show();

                    }
                });


            }
        });

    }

}

