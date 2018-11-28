package com.bytes.fightr.client.widget.Adapter;

/**
 * Created by Kent on 5/20/2017.
 */
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bytes.fightr.R;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fmk.model.Message;

import java.util.List;

public class UserMsgRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Message> dataItems;


    public UserMsgRecyclerAdapter(Activity activity, List<Message> dataItems) {
        this.activity = activity;
        this.dataItems = dataItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                itemView = inflater.inflate(R.layout.list_item_lobby_msg_right, null);
                viewHolder = new MessageInViewHolder(itemView);
                break;

            case 1:
                itemView = inflater.inflate(R.layout.list_item_lobby_msg_left, null);
                viewHolder = new MessageOutViewHolder(itemView);
                break;

            case 2:
                itemView = inflater.inflate(R.layout.list_item_lobby_msg_system, null);
                viewHolder = new SystemMessageViewHolder(itemView);
                break;

            default:
                itemView = inflater.inflate(R.layout.list_item_lobby_msg_left, null);
                viewHolder = new MessageOutViewHolder(itemView);
                break;
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Message message = dataItems.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                MessageInViewHolder in = (MessageInViewHolder) holder;
                in.nameView.setText(message.getUsername());
                in.msgView.setText(message.getMessage());
                in.timeView.setText(message.getTime());
                break;

            case 1:
                MessageOutViewHolder out = (MessageOutViewHolder) holder;
                out.nameView.setText(message.getUsername());
                out.msgView.setText(message.getMessage());
                out.timeView.setText(message.getTime());
                break;

            case 2:
                SystemMessageViewHolder systemMessage = (SystemMessageViewHolder) holder;
                systemMessage.nameView.setText(message.getUsername());
                systemMessage.msgView.setText(message.getMessage());
                systemMessage.timeView.setText(message.getTime());
                break;
        }

    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (dataItems.get(position)).getType();
    }

    public void addMessage(final Message message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataItems.add(message);
                notifyDataSetChanged();
            }
        });
    }

    /**
     * Views for system messages
     */
    private class SystemMessageViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;
        TextView msgView;
        TextView timeView;

        SystemMessageViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView) itemView.findViewById(R.id.fightr_lobby_msg_user_name);
            msgView = (TextView) itemView.findViewById(R.id.fightr_lobby_msg_user_msg);
            timeView = (TextView) itemView.findViewById(R.id.fightr_lobby_msg_user_msg_time);
        }
    }


    /**
     * Views for out-going messages
     */
    private class MessageOutViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;
        TextView msgView;
        TextView timeView;

        MessageOutViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView) itemView.findViewById(R.id.fightr_lobby_msg_user_name);
            msgView = (TextView) itemView.findViewById(R.id.fightr_lobby_msg_user_msg);
            timeView = (TextView) itemView.findViewById(R.id.fightr_lobby_msg_user_msg_time);
        }
    }

    /**
     * Views for incoming messages
     */
    class MessageInViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;
        TextView msgView;
        TextView timeView;

        MessageInViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView) itemView.findViewById(R.id.fightr_lobby_msg_user_name);
            msgView = (TextView) itemView.findViewById(R.id.fightr_lobby_msg_user_msg);
            timeView = (TextView) itemView.findViewById(R.id.fightr_lobby_msg_user_msg_time);
        }
    }

}