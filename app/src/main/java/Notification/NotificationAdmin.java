package Notification;

import android.app.Notification;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.foodhive.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.foodhive.App.CHANNEL_ID;

public class NotificationAdmin {

    private DatabaseReference databaseReferenceChildEvent;
    private Context context;
    private NotificationManagerCompat notificationManagerCompat;
    private String message = "";

    public NotificationAdmin(Context context, String message) {
        this.context = context;
        this.message = message;
    }

    public void setNotificationOnNewOrder() {
        notificationManagerCompat = NotificationManagerCompat.from(context);
        databaseReferenceChildEvent = FirebaseDatabase.getInstance().getReference().child("Order");
        databaseReferenceChildEvent.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.d("Notification", previousChildName + "");

                if (snapshot.getKey().equals(previousChildName))
                    notificationManagerCompat.cancel(1);
                else
                {
                    setNotification(snapshot.getKey() + "");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void setNotification(String message) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_add_24)
                .setContentTitle("You Have new order")
                .setContentText("Order ID: " + message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .build();

        notificationManagerCompat.notify(1, notification);


    }


}
