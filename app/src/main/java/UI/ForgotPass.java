package UI;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.foodhive.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class ForgotPass extends Fragment {


    private TextView textView;
    private EditText email;
    private Button reset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_pass, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = view.findViewById(R.id.email_forgot_id);
        textView = view.findViewById(R.id.text_pas_reset);
        reset = view.findViewById(R.id.reset_pass_button);

        reset.setOnClickListener(v -> {
            reset.setEnabled(false);
            String emailstring = this.email.getText().toString();
            if (emailstring.isEmpty()) {
                reset.setEnabled(true);
                email.setError("Email a email");
                email.requestFocus();

            }

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.sendPasswordResetEmail(emailstring)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                 textView.setText("A link is send to your email");
                            } else {
                                reset.setEnabled(true);
                                Snackbar.make(view, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    reset.setEnabled(true);
                    Snackbar.make(view, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();

                }
            });

        });

    }
}