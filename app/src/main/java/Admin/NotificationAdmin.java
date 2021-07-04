package Admin;

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
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.example.foodhive.App.CHANNEL_ID;

public class NotificationAdmin {

    private DatabaseReference databaseReferenceChildEvent;
    private Context context;
    private NotificationManagerCompat notificationManagerCompat;
    private String message = "";

    public NotificationAdmin(Context context) {
        this.context = context;
        notificationManagerCompat = NotificationManagerCompat.from(context);

    }

    public void setNotificationOnNewOrder() {
        DatabaseReference databaseReferenceNotification = FirebaseDatabase.getInstance().getReference().child("Notification").child("Order Notify");
        databaseReferenceNotification.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    if (d.getValue() != null) {
                        Boolean mayINotify = d.getValue(Boolean.class);
                        if (mayINotify == Boolean.FALSE) {
                            setNotification(d.getKey() + "");
                            databaseReferenceNotification.child(Objects.requireNonNull(d.getKey())).removeValue();
                        }

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setNotification(String message) {
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
