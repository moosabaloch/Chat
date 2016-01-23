package pana.com.chat.ui.Fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import pana.com.chat.DataModel.DataModelCurrentGroupChat;
import pana.com.chat.DataModel.DataModelMeSingleton;
import pana.com.chat.DataModel.DataModelUser;
import pana.com.chat.Adaptor.GroupChatAdaptor;
import pana.com.chat.DataModel.GroupUsersDetailsHashMap;
import pana.com.chat.DataModel.Messages;
import pana.com.chat.R;
import pana.com.chat.Util.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupChatFragment extends Fragment {
    private Firebase firebaseURL;
    private ImageButton sendMessageButton;
    private EditText sendMessageText;
    private ListView chatListView;
    private TextView friendName, friendEmail;
    private Button logoutButton;
    private GroupChatAdaptor groupChatAdaptor;
    private ArrayList<Messages> messagesArrayList;
    private DataModelCurrentGroupChat groupChatDetail = DataModelCurrentGroupChat.getInstance();
private FloatingActionButton fab;
    public GroupChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        fab= (FloatingActionButton) getActivity().findViewById(R.id.fab);
        firebaseURL = new Firebase("https://pcchatapp.firebaseio.com/");
        sendMessageButton = (ImageButton) view.findViewById(R.id.chatFragmentButtonSendMessage);
        sendMessageText = (EditText) view.findViewById(R.id.chatFragmentEditTextWriteMessageHere);
        friendName = (TextView) view.findViewById(R.id.chatFragmentTextViewFriendName);
        friendEmail = (TextView) view.findViewById(R.id.chatFragmentTextViewFriendEmailView);
        chatListView = (ListView) view.findViewById(R.id.chatFragmentListViewChatMessages);
        messagesArrayList = new ArrayList<>();
        sendMessageButtonClickEvent();
        setDetails();
        loadGroupMembersData();
        //loadChatAdaptor();

        return view;
    }

    private void sendMessageButtonClickEvent() {
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseURL.child("conversation")
                        .child(DataModelCurrentGroupChat.getInstance()
                                .getGroupIDKEY()).push()
                        .setValue(new Messages(String.valueOf(System.currentTimeMillis()), sendMessageText.getText().toString(), DataModelMeSingleton.getInstance().getId()), new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                sendNotification();
                            }
                        });

                sendMessageText.setText("");
                refreshListView();
            }
        });
    }

    private void sendNotification() {
        /////////////////PENDING//////////////////
    }

    private void refreshListView() {
        groupChatAdaptor.notifyDataSetChanged();
        chatListView.setSelection(messagesArrayList.size());
    }

    private void loadGroupMembersData() {
        firebaseURL.child("groupusers").child(DataModelCurrentGroupChat.getInstance().getGroupIDKEY()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Utils.myFrindsId.clear();
                GroupUsersDetailsHashMap.getInstance().clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    final String UserID = data.getValue().toString();
                    if (!Utils.friendIdAddedToMap(UserID)) {
                        Utils.myFrindsId.add(UserID);
                        Log.d(DataModelCurrentGroupChat.getInstance().getGroupName(), "Users=" + UserID);
                        firebaseURL.child("users").child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshotForUser) {
                                GroupUsersDetailsHashMap.getInstance().put(UserID, dataSnapshotForUser.getValue(DataModelUser.class));
                                Log.d("User Added " + UserID, dataSnapshotForUser.getValue().toString());

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                Utils.ToastLong(getActivity(), "Error Loading Group Members : " + firebaseError.getMessage());

                            }
                        });
                    }
                }
                // loadAllMessages();
                loadChatAdaptor();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Utils.ToastLong(getActivity(), "Error Loading Group Members : " + firebaseError.getMessage());

            }
        });
    }
/*

    private void loadAllMessages() {
        firebaseURL.child("conversation")
                .child(DataModelCurrentGroupChat.getInstance().getGroupIDKEY())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        messagesArrayList.clear();
                        for (DataSnapshot messages : dataSnapshot.getChildren()) {
                            Messages msg = messages.getValue(Messages.class);
                            messagesArrayList.add(msg);
                            Log.d("Msg is ", msg.toString());
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
    }
*/

    private void loadChatAdaptor() {
        chatListView.setAdapter(groupChatAdaptor = new GroupChatAdaptor(getActivity(), messagesArrayList));
        firebaseURL.child("conversation")
                .child(DataModelCurrentGroupChat.getInstance().getGroupIDKEY()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Message is", "" + dataSnapshot.getValue());
                messagesArrayList.add(dataSnapshot.getValue(Messages.class));
                refreshListView();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Utils.ToastLong(getActivity(), "Error Loading Chat : " + firebaseError.getMessage());

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fab.setVisibility(View.GONE);
    }

    private void setDetails() {
        friendName.setText(groupChatDetail.getGroupName());
        friendEmail.setText(groupChatDetail.getGroupDescription());
    }


}
