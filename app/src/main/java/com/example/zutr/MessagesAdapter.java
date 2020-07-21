package com.example.zutr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zutr.models.Message;
import com.example.zutr.models.Session;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Map;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {


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
                tvMsgBody.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                tvMsgTime.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            }
            tvMsgTime.setText(message.getRelativeTimeAgo());
            tvMsgBody.setText(message.getBody());

        }
    }
}
