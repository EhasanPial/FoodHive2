package UI;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.foodhive.FcmNotificationsSender;
import com.example.foodhive.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.ChatAdapter;
import Admin.NotificationAdmin;
import Model.ChatModel;
import Model.OrderList;


public class Chat extends Fragment {

    /// --- UI --- //
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private EditText message;
    private RelativeLayout relativeLayout;
    private TextView pleaseLogin;
    private Toolbar toolbar;

    // ------ Firebase ---------- //
    private DatabaseReference databaseReferenceChat;
    private DatabaseReference databaseReferenceInfo;
    private DatabaseReference databaseReferenceNotification;
    private FirebaseAuth firebaseAuth;


    // ---------- Var ----------- //
    private String userUID = "";
    private String adminUID;
    private List<ChatModel> chatModelList;
    private ChatAdapter chatAdapter;
    private Boolean setValueEvent = false;
    String chatRoomID = "";
    private boolean seen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.chat_recy_id);
        floatingActionButton = view.findViewById(R.id.chat_send_id);
        message = view.findViewById(R.id.chat_message_edittext);
        relativeLayout = view.findViewById(R.id.chat_relative_layout_id);
        pleaseLogin = view.findViewById(R.id.chat_please_login);
        toolbar = view.findViewById(R.id.toolbar_chat);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(getContext());
        chatModelList = new ArrayList<>();


        // ----------- Firebase --------------- //
        databaseReferenceChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        databaseReferenceInfo = FirebaseDatabase.getInstance().getReference().child("Info");
        databaseReferenceNotification = FirebaseDatabase.getInstance().getReference().child("Notification").child("Message");


        // ------ Arguments ------ //
        firebaseAuth = FirebaseAuth.getInstance();

        // -- generating Admin chatroom ID --- //
        databaseReferenceInfo.child("Admin Info").child("uid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminUID = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ChatArgs chatArgs = ChatArgs.fromBundle(getArguments());
        userUID = chatArgs.getUid();
        Log.d("Chat", userUID + "");
        // ----------------- Notification --------------------- //


        NavController navController = Navigation.findNavController(view);
        if (firebaseAuth.getCurrentUser() == null) {
            relativeLayout.setVisibility(View.INVISIBLE);
            pleaseLogin.setVisibility(View.VISIBLE);
            pleaseLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navController.navigate(R.id.action_chat_to_login2);
                }
            });
            return;
        } else {
            relativeLayout.setVisibility(View.VISIBLE);
            pleaseLogin.setVisibility(View.INVISIBLE);
            if (userUID != null) {

                chatRoomID = userUID + adminUID;
                loadAll();
            } else {
                userUID = firebaseAuth.getCurrentUser().getUid();
                Log.d("userid", userUID + "");
                chatRoomID = userUID + adminUID;
                if (!setValueEvent)
                    loadAllWithUID();
            }

            loadNotification();
        }

        loadAll();
        loadAllWithUID();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message.getText().toString().isEmpty()) {
                    String timestamp = System.currentTimeMillis() + "";
                    ChatModel chatModel = new ChatModel(message.getText().toString(), firebaseAuth.getCurrentUser().getUid(), timestamp, "false");
                    databaseReferenceChat.child("ChatRoom").child(chatRoomID).child(timestamp).setValue(chatModel);
                    DatabaseReference databaseReferenceNotification = FirebaseDatabase.getInstance().getReference().child("Notification").child("Message");
                    if (firebaseAuth.getCurrentUser().getUid().equals(adminUID)) {
                        // ---------------------- messaging notificatin is sending to user -----------------///////////
                        NotificationUser notificationUser = new NotificationUser(getContext(), userUID, getActivity());
                        notificationUser.setFirebaseOrderNotification(getString(R.string.You_have_new_Message), message.getText().toString());
                        // databaseReferenceNotification.child("Users Messages").child(userUID).setValue(adminUID);
                    } else {
                        // ---------------------- messaging notificatin is sending to user -----------------///////////

                        databaseReferenceNotification.child("Admin Messages").child(userUID).setValue("false");
                        FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender("/topics/Admin", "You have new message", message.getText().toString(), getContext(), getActivity());
                        fcmNotificationsSender.SendNotifications();
                    }
                    message.setText("");
                }

            }
        });


    }

    private void loadNotification() {
        databaseReferenceInfo.child("Admin Info").child("uid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminUID = snapshot.getValue(String.class);
                if (firebaseAuth.getCurrentUser().getUid().equals(adminUID)) {

                    if (userUID != null) {
                        Log.d("Chat", userUID + "");
                        NotificationAdmin notificationAdmin = new NotificationAdmin(getContext());
                        notificationAdmin.setDatabaseForChatNotificationDelete(userUID);
                    }

                    databaseReferenceInfo.removeEventListener(this);
                } else {
                    // NotificationUser notificationUser = new NotificationUser(getContext(), firebaseAuth.getCurrentUser().getUid());
                    //  notificationUser.setDatabaseForChatNotificationDelete();
                }
                Log.d("Chat", adminUID + "");
                databaseReferenceInfo.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadAllWithUID() {
        databaseReferenceChat.child("AdminChat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("userid3", userUID + "");
                if (!snapshot.hasChild(firebaseAuth.getCurrentUser().getUid()) && userUID != null) {
                    databaseReferenceChat.child("AdminChat").child(userUID).setValue(chatRoomID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceChat.child("ChatRoom").child(chatRoomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatModelList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    chatModelList.add(d.getValue(ChatModel.class));
                }


                chatAdapter.setList(chatModelList);
                recyclerView.setAdapter(chatAdapter);
                recyclerView.smoothScrollToPosition(chatModelList.size());
                chatAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadAll() {

        Log.d("userid2", userUID + "");

        databaseReferenceChat.child("AdminChat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("userid3", userUID + "");
                if (!snapshot.hasChild(userUID) && userUID != null && userUID != null) {
                    databaseReferenceChat.child("AdminChat").child(userUID).setValue(chatRoomID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceChat.child("ChatRoom").child(chatRoomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatModelList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    chatModelList.add(d.getValue(ChatModel.class));
                }


                chatAdapter.setList(chatModelList);
                recyclerView.setAdapter(chatAdapter);
                recyclerView.smoothScrollToPosition(chatModelList.size());
                chatAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}