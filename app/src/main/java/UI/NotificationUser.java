package UI;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.example.foodhive.FcmNotificationsSender;
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
    private Context context;
    private NotificationManagerCompat notificationManagerCompat;
    Activity mActivity;
    private String uid = "";

    public NotificationUser(Context context) {
        this.context = context;
        notificationManagerCompat = NotificationManagerCompat.from(context);
    }

    public NotificationUser(Context context, String uid, Activity mActivity) {
        this.context = context;
        notificationManagerCompat = NotificationManagerCompat.from(context);
        this.uid = uid;
        this.mActivity = mActivity;
    }

    public void setFirebaseOrderNotification(String title, String msg) {
        Log.d("FCM", title + "");
        FirebaseDatabase.getInstance().getReference("Info").child("Users Info").child(uid).child("totalOrders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.getValue(String.class);
                Log.d("FCM", token + "");
                FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(token, title, msg, context, mActivity);
                fcmNotificationsSender.SendNotifications();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setFirebaseNotifyAdmin(String title, String msg) {
        FirebaseDatabase.getInstance().getReference("Info").child("Admin Info").child(uid).child("totalOrders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.getValue(String.class);
                Log.d("FCM", token + "");
                FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(token, title, msg, context, mActivity);
                fcmNotificationsSender.SendNotifications();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void setNotificationOnNewOrder() {
        DatabaseReference databaseReferenceNotification = FirebaseDatabase.getInstance().getReference().child("Notification").child("Order Status");
    /*    databaseReferenceNotification.addValueEventListener(new ValueEventListener() {
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
        });*/
    }

    public void setDatabaseForChatNotification() {
        /*DatabaseReference databaseReferenceNotification = FirebaseDatabase.getInstance().getReference().child("Notification").child("Message").child("Users Messages");
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
        });*/

    }

    public void setDatabaseForChatNotificationDelete() {
      /*  DatabaseReference databaseReferenceNotification = FirebaseDatabase.getInstance().getReference().child("Notification").child("Message").child("Users Messages");
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
        });*/


    }


    public void setNotification(String title, String message) {
      //  Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_add_24)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(getPendingIntent(title))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
             // .setVibrate(new long[]{0, 1000, 800, 1000})
                .build();


        notificationManagerCompat.notify(1, notification);


    }

    public void setNotificationForChat(String title, String message) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_add_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(new long[]{0, 1000, 600, 1000})
                .setContentIntent(getPendingIntent(title))
                .build();


        notificationManagerCompat.notify(1, notification);


    }

    private PendingIntent getPendingIntent(String type) {


        if (type.equals(context.getString(R.string.You_have_new_Message))) {
            return new NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.chat)
                    .createPendingIntent();
        } else if (type.equals(context.getString(R.string.Your_order_is))) {
            return new NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.usersOrder)
                    .createPendingIntent();
        } else if (type.equals(context.getString(R.string.You_have_new_order))) {
            return new NavDeepLinkBuilder(context)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.orderTest)
                    .createPendingIntent();
        }


        return new NavDeepLinkBuilder(context)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.homeFragment)
                .createPendingIntent();
    }
}
