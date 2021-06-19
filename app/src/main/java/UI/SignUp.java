package UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.foodhive.R;


public class SignUp extends Fragment {
    // --- UI --//
    private EditText username, email, pass, address, phone;
    private Button signUp;

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

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp.setEnabled(false);

                String nameText, emailtext, passtext, addresstext, phonetext;
                nameText = username.getText().toString();
                emailtext = email.getText().toString();
                passtext = pass.getText().toString();
                addresstext = address.getText().toString();
                phonetext = phone.getText().toString();

                if (nameText.isEmpty()) {
                    username.setError("Enter your name");
                    username.requestFocus();
                } else if (emailtext.isEmpty()) {
                    email.setError("Enter your email");
                    email.requestFocus();
                } else if (passtext.isEmpty()) {
                    pass.setError("Enter Password");
                    pass.requestFocus();
                } else if (addresstext.isEmpty()) {
                    address.setError("Enter your address");
                    address.requestFocus();
                } else if (phonetext.isEmpty()) {
                    phone.setError("Enter your phone");
                    phone.requestFocus();
                }


            }
        });

    }
}