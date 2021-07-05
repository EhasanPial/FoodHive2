package UI;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;

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

import Model.ChatModel;

import static com.example.foodhive.App.CHANNEL_ID;

public class NotificationUser {
    private DatabaseReference databaseReferenceChildEvent;
    private final Context context;
    private final NotificationManagerCompat notificationManagerCompat;

    private String uid = "";

    public NotificationUser(Context context, String uid) {
        this.context = context;
        notificationManagerCompat = NotificationManagerCompat.from(context);
        this.uid = uid;

    }


    public void setNotificationOnNewOrder() {
        DatabaseReference databaseReferenceNotification = FirebaseDatabase.getInstance().getReference().child("Notification").child("Order Status");
        databaseReferenceNotification.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(uid)) {
                    databaseReferenceNotification.child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot d : snapshot.getChildren()) {
                                if (d.getValue() != null) {
                                    String statusNotify = d.getValue(String.class);
                                    assert statusNotify != null;
                                    if ((statusNotify.equals(context.getString(R.string.cooking_))) || (statusNotify.equals(context.getString(R.string.accepted)))) {
                                        setNotification(statusNotify);

                                    } else {
                                        if (statusNotify.equals(context.getString(R.string.ready_for_delivery))) {
                                            setNotification(statusNotify);

                                        }


                                    }

                                    databaseReferenceNotification.child(uid).child(Objects.requireNonNull(d.getKey())).removeValue();

                                }


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setDatabaseForChatNotification() {
        DatabaseReference databaseReferenceNotification = FirebaseDatabase.getInstance().getReference().child("Notification").child("Message").child("Users Messages");
        databaseReferenceNotification.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(uid)) {

                    databaseReferenceNotification.child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            setNotificationForChat("");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setDatabaseForChatNotificationDelete() {
        DatabaseReference databaseReferenceNotification = FirebaseDatabase.getInstance().getReference().child("Notification").child("Message").child("Users Messages");
        databaseReferenceNotification.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(uid)) {

                    setNotificationForChat("");
                    databaseReferenceNotification.child(uid).removeValue();
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
                .setContentTitle("Your Food is")
                .setContentText(message)
                .setContentIntent(getPendingIntent(2))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(new long[]{0, 1000, 500, 1000})
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
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setContentIntent(getPendingIntent(1))
                .build();


        notificationManagerCompat.notify(1, notification);


    }

    private PendingIntent getPendingIntent(int type) {


            if (type== 1)
            {
                return new NavDeepLinkBuilder(context)
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.chat)
                        .createPendingIntent();
            }

            else if(type == 2)
            {
                return new NavDeepLinkBuilder(context)
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.usersOrder)
                        .createPendingIntent();
            }




           return  new NavDeepLinkBuilder(context)
                   .setGraph(R.navigation.nav_graph)
                   .setDestination(R.id.homeFragment)
                   .createPendingIntent();
    }
}
