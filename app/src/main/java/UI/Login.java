package UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.foodhive.MainActivity;
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

import Constants.BaseString;


public class Login extends Fragment {
    // --- Ui ---//
    private EditText email, pass;
    private Button signIn;
    public static boolean TYPE = false;

    // ---- Firebase ---- //
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //----- FIND ----//
        email = view.findViewById(R.id.emailSignIn);
        pass = view.findViewById(R.id.passwordSignIn);
        signIn = view.findViewById(R.id.sign_in_loginFragment);

        // ---- Fireabase ----//
        databaseReference = FirebaseDatabase.getInstance().getReference("Info");
        firebaseAuth = FirebaseAuth.getInstance();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn.setEnabled(false);

                String emailtext, passtext;
                emailtext = email.getText().toString();
                passtext = pass.getText().toString();

                if (emailtext.isEmpty()) {
                    email.setError("Enter your name");
                    email.requestFocus();
                    signIn.setEnabled(true);
                } else if (passtext.isEmpty()) {
                    pass.setError("Enter your email");
                    pass.requestFocus();
                    signIn.setEnabled(true);

                }



                NavController navController = Navigation.findNavController(v);
                databaseReference.child("Admin Info").child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String adminEmail = snapshot.getValue(String.class);
                        Log.d("Here", "if bahire") ;
                        if (adminEmail.toLowerCase().equals(emailtext.toLowerCase())) {
                            Log.d("Here", "if vitore") ;

                            TYPE = true;

                            firebaseAuth.signInWithEmailAndPassword(emailtext, passtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        Snackbar.make(view, "ADMIN", Snackbar.LENGTH_SHORT).show();
                                        navController.navigate(R.id.action_login2_to_adminFragment); /////////////////////////////////////////////////////////////////
                                    }
                                    else
                                    {
                                        Snackbar.make(view, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                                        signIn.setEnabled(true);

                                    }

                                }
                            }) ;

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        signIn.setEnabled(true);

                    }
                });






                databaseReference.child("Users Info").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                         for(DataSnapshot d: snapshot.getChildren())
                         {
                             if(d.child("email").getValue().toString().toLowerCase().equals(emailtext.toLowerCase()))
                             {
                                 BaseString.setUserPhone(d.child("phone").getValue().toString());
                                 firebaseAuth.signInWithEmailAndPassword(emailtext,passtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                     @Override
                                     public void onComplete(@NonNull Task<AuthResult> task) {
                                         if (task.isSuccessful())
                                         {
                                             FoodHive.logged = true ;
                                             startActivity(new Intent(getActivity(), MainActivity.class));
                                             getActivity().finish();
                                              navController.navigate(R.id.action_login2_to_homeFragment); /////////////////////////////////////////////////////////////////
                                         }
                                         else
                                         {
                                             Snackbar.make(view, task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                                             signIn.setEnabled(true);

                                         }
                                     }
                                 }) ;
                             }
                         }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

    }
}