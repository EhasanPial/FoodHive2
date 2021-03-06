package Admin;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;

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

    public void setDatabaseForChatNotificationDelete(String uid) {
        DatabaseReference databaseReferenceNotification = FirebaseDatabase.getInstance().getReference().child("Notification").child("Message").child("Admin Messages");
        databaseReferenceNotification.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(uid)) {

                    // setNotificationForChat("");
                    databaseReferenceNotification.child(uid).removeValue();

                }


                databaseReferenceNotification.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void setDatabaseForChatNotification() {
        DatabaseReference databaseReferenceNotification1 = FirebaseDatabase.getInstance().getReference().child("Notification").child("Message").child("Admin Messages");
        databaseReferenceNotification1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot d : snapshot.getChildren()) {
                    if (Objects.equals(d.getValue(String.class), "false")) {
                        setNotificationForChat("You have a new messages");
                        databaseReferenceNotification1.child(Objects.requireNonNull(d.getKey())).setValue("true");
                    }
                    //databaseReferenceNotification1.removeValue();
                }


                // databaseReferenceNotification.removeEventListener(this);
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
                .setContentIntent(getPendingIntent(2))             /////////////////// pending intent
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)

                // .setVibrate(new long[]{0, 1000, 500, 1000})
                .build();

        notificationManagerCompat.notify(1, notification);


    }

    public void setNotificationForChat(String message) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_add_24)
                .setContentTitle("Message")
                .setContentText("You have new message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(getPendingIntent(1))     /////////////////// pending intent
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .build();

        notificationManagerCompat.notify(1, notification);


    }

    private PendingIntent getPendingIntent(int type) {


        if (type == 1) {
            return new NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.usersAdmin)
                    .createPendingIntent();
        } else if (type == 2) {
            return new NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.orderTest)
                    .createPendingIntent();
        }


        return new NavDeepLinkBuilder(context)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.adminFragment)
                .createPendingIntent();

    }


}
