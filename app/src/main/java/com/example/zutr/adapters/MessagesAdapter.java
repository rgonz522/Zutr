package com.example.zutr.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zutr.R;
import com.example.zutr.models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {


    public static final String TAG = "MessagesAdapter";
    List<Message> messages;

    Context context;

    String currentUserID;

    public MessagesAdapter(Context context, List<Message> messages) {

        this.context = context;
        this.messages = messages;

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }


    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);


        return new MessagesAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.bind(message);
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMsgBody;
        TextView tvMsgTime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tvMsgBody = itemView.findViewById(R.id.tvChatMsg);
            tvMsgTime = itemView.findViewById(R.id.tvMsgTime);

        }

        public void bind(Message message) {

            if (message.getAuthorID().equals(currentUserID)) {
                Log.i(TAG, "bind: " + true);
                tvMsgBody.setGravity(Gravity.RIGHT);
                tvMsgTime.setGravity(Gravity.RIGHT);
            }
            tvMsgTime.setText(message.getRelativeTimeAgo());
            tvMsgBody.setText(message.getBody());


        }


    }
}
