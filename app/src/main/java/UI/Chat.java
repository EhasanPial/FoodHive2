package UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.foodhive.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Model.ChatModel;
import Model.OrderList;


public class Chat extends Fragment {

    /// --- UI --- //
    private RecyclerView recyclerView ;
    private FloatingActionButton floatingActionButton ;
    private EditText message ;

    // ------ Firebase ---------- //
    private DatabaseReference databaseReferenceChat;
    private DatabaseReference databaseReferenceInfo;
    private FirebaseAuth firebaseAuth ;


    // ---------- Var ----------- //
    private String userUID = "";
    private String adminUID;
    private List<ChatModel> chatModelList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.chat_recy_id);
        floatingActionButton = view.findViewById(R.id.chat_send_id) ;
        message = view.findViewById(R.id.chat_message_edittext) ;



        // ------ Arguments ------ //
        ChatArgs chatArgs = ChatArgs.fromBundle(getArguments());
        userUID= chatArgs.getUid();


        // ----------- Firebase --------------- //
        databaseReferenceChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        databaseReferenceInfo = FirebaseDatabase.getInstance().getReference().child("Info");
        firebaseAuth = FirebaseAuth.getInstance() ;

        /// --- getting admin uid ---- //
        databaseReferenceInfo.child("Admin Info").child("uid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminUID = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatModelList = new ArrayList<>() ;

        // -- generating unique chatroom ID --- //
        String chatRoomID = userUID + adminUID ;
        databaseReferenceChat.child("AdminChat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(userUID))
                {
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
                for(DataSnapshot d: snapshot.getChildren())
                {
                    chatModelList.add(d.getValue(ChatModel.class));
                }
                /////////////////////////////////////////////////
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!message.getText().toString().isEmpty()) {
                    String timestamp = System.currentTimeMillis() + "";
                    ChatModel chatModel = new ChatModel(message.getText().toString(), firebaseAuth.getCurrentUser().getUid(), timestamp);
                    databaseReferenceChat.child("ChatRoom").child(chatRoomID).child(timestamp).setValue(chatModel);
                }

            }
        });


    }
}