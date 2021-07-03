package UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodhive.MainActivity;
import com.example.foodhive.R;
import com.google.firebase.auth.FirebaseAuth;


public class logout extends Fragment {

    private FirebaseAuth firebaseAuth ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance() ;
        firebaseAuth.signOut();
        getActivity().finish();
        startActivity(new Intent(getActivity(), MainActivity.class));

        getActivity().finish();
    }
}