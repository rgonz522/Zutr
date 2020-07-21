package com.example.zutr;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zutr.models.Message;
import com.example.zutr.models.Session;
import com.example.zutr.models.Student;
import com.example.zutr.models.Tutor;
import com.example.zutr.models.User;
import com.example.zutr.user_auth.LogInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {


    public static final String TAG = "ChatsAdapter";
    List<Message> messages;

    Context context;

    String currentUserID;

    public ChatsAdapter(Context context, List<Message> messages) {

        this.context = context;
        this.messages = messages;

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }


    @NonNull
    @Override
    public ChatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chats, parent, false);


        return new ChatsAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ChatsAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.bind(message);
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvAuthor;
        TextView tvLastMessage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tvAuthor = itemView.findViewById(R.id.tvContact);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);

        }

        public void bind(Message message) {


            tvAuthor.setText(getContactFullName(message.getAuthorID()));
            tvLastMessage.setText(message.getBody());

        }


        public String getContactFullName(String remotedID) {

            String collectionPath = "";
            if (LogInActivity.IS_TUTOR) {
                collectionPath = Student.PATH;
            } else {
                collectionPath = Tutor.PATH;
            }

            FirebaseFirestore database = FirebaseFirestore.getInstance();


            final StringBuilder userRealName = new StringBuilder("");
            //has to be an array in order to be changed within
            //inner CompleteListener Class

            database.collection(collectionPath)
                    .document(remotedID)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        userRealName.append(task.getResult().getString(User.KEY_FIRSTNAME) + task.getResult().getString(User.KEY_LASTNAME));
                        Log.i(TAG, "getUserRealName: " + userRealName);


                    }
                }
            });

            return userRealName.toString();


        }
    }


}


