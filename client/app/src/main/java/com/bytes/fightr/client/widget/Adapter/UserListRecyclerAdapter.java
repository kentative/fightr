package com.bytes.fightr.client.widget.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytes.fightr.R;
import com.bytes.fightr.client.controller.lobby.FightrLobbyActivity;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.util.DisplayUtil;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fmk.model.User;
import com.bytes.gamr.model.avatar.AvatarAttribute;
import com.bytes.gamr.model.avatar.AvatarPower;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Kent on 5/31/2017.
 *
 */
public class UserListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FightrLobbyActivity activity;
    private List<User> dataItems;

    public UserListRecyclerAdapter(FightrLobbyActivity activity, List<User> dataItems) {
        this.activity = activity;
        this.dataItems = dataItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_select_user, null);
        return new UserViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        UserViewHolder view = (UserViewHolder) holder;

        User user = dataItems.get(position);
        if (user == null) return;

        updateUserDetails(user, view);

        Fighter fighter = GameState.Instance.getFighter(user.getId());
        updateFighterDetails(fighter, view);
    }

    /**
     * Update User details
     *
     * @param user - the user containing the data
     * @param view - the view containing the detailed views to be updated
     */
    private void updateUserDetails(final User user, UserViewHolder view) {

        view.userImage.setImageResource(R.drawable.ow_icon_pharah); // hardcode this for now
        view.nameView.setText(user.getDisplayName());
        User.Status status = user.getStatus();
        switch (status) {

            case Available:
                view.statusImage.setImageResource(R.drawable.bg_user_online);
                break;

            case Offline:
                view.statusImage.setImageResource(R.drawable.bg_user_offline);
                break;

            case Searching:
                view.statusImage.setImageResource(R.drawable.bg_user_searching);
                break;

            case Ready:
                view.statusImage.setImageResource(R.drawable.bg_user_ready);
                break;

            case Battle:
                break;

            default:
                break;
        }

        view.attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getPartyProcessor().selectEnemyTarget(user.getId());
            }
        });

        view.joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getPartyProcessor().selectAllyTarget(user.getId());
            }
        });

    }


    /**
     * Update the Fighter details
     *
     * @param fighter the fighter containing the data
     * @param view    - the view holder
     */
    private void updateFighterDetails(Fighter fighter, UserViewHolder view) {
        if (fighter != null) {

            String hp = String.format("%1$02d HP", fighter.getAttributes(AvatarAttribute.Type.HEALTH).getValue());
            String ap = String.format("%1$02d AP", fighter.getPower(AvatarPower.Type.ENERGY_STAMINA).getValue());

            view.hpView.setText(hp);
            view.apView.setText(ap);
        }
    }


    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public void update(User user) {
        for (int i = 0; i < dataItems.size();i++) {
            User u = dataItems.get(i);
            if (u.getDisplayName().equals(user.getDisplayName())) {
                int random = ThreadLocalRandom.current().nextInt(0, 4);
                DisplayUtil.toast(activity, "Status: " + random);
                u.setStatus(User.Status.values()[random]);
                notifyItemChanged(i);
            }
        }
    }

    public List<User> getDataItems() {
        return dataItems;
    }

    /**
     * Views for out-going messages
     */
    class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        ImageView statusImage;
        TextView nameView;
        TextView hpView;
        TextView apView;

        ImageView attackButton;
        ImageView joinButton;



        UserViewHolder(View itemView) {
            super(itemView);

            userImage = (ImageView) itemView.findViewById(R.id.fightr_select_user_image);
            statusImage = (ImageView) itemView.findViewById(R.id.fightr_select_user_status);
            nameView = (TextView) itemView.findViewById(R.id.fightr_lobby_msg_user_name);
            attackButton = (ImageView) itemView.findViewById(R.id.fightr_select_user_attack);
            joinButton = (ImageView) itemView.findViewById(R.id.fightr_select_user_join);

            hpView = (TextView) itemView.findViewById(R.id.lobby_select_user_hp);
            apView = (TextView) itemView.findViewById(R.id.lobby_select_user_ap);
        }
    }
}
