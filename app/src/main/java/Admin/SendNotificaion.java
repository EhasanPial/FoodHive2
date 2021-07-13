package Admin;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.foodhive.FcmNotificationsSender;
import com.example.foodhive.R;
import com.google.android.material.snackbar.Snackbar;


public class SendNotificaion extends Fragment {

    private EditText title, msg;
    private Button send;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_notificaion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.notify_title);
        msg = view.findViewById(R.id.notify_msg);
        send = view.findViewById(R.id.notify_button);

        NavController navController = Navigation.findNavController(view);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send.setEnabled(false);

                String titletext = title.getText().toString();
                String msgTet = msg.getText().toString();

                if (titletext.isEmpty()) {
                    title.requestFocus();
                    title.setError("Empty");
                } else if (msgTet.isEmpty()) {

                    msg.requestFocus();
                    msg.setError("Empty");
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Do you want to send this notification?");
                    builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender("/topics/newfood", titletext, msgTet, getContext(), getActivity());
                            fcmNotificationsSender.SendNotifications();

                            Snackbar.make(view, "Notification send to all users", Snackbar.LENGTH_SHORT).show();

                            navController.navigate(R.id.action_sendNotificaion_to_adminFragment);
                        }
                    });

                    builder.create().show();


                }

            }
        });
    }
}