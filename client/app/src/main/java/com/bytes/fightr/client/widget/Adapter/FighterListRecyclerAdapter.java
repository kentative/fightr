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
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fmk.model.User;
import com.bytes.gamr.model.avatar.AvatarAttribute;
import com.bytes.gamr.model.avatar.AvatarPower;

import java.util.List;

/**
 * Created by Kent on 5/31/2017.
 */
public class FighterListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private FightrLobbyActivity activity;
    private List<Fighter> dataItems;

    public FighterListRecyclerAdapter(FightrLobbyActivity activity, List<Fighter> dataItems) {
        this.activity = activity;
        this.dataItems = dataItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fighter_left, null);
        return new UserViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        UserViewHolder view = (UserViewHolder) holder;

        Fighter data = dataItems.get(position);
        User user = GameState.Instance.getUser(data.getUserId());
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

    public List<Fighter> getDataItems() {
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

        UserViewHolder(View itemView) {
            super(itemView);

            userImage = (ImageView) itemView.findViewById(R.id.fightr_select_user_image);
            statusImage = (ImageView) itemView.findViewById(R.id.fightr_select_user_status);
            nameView = (TextView) itemView.findViewById(R.id.fightr_lobby_msg_user_name);

            hpView = (TextView) itemView.findViewById(R.id.lobby_select_user_hp);
            apView = (TextView) itemView.findViewById(R.id.lobby_select_user_ap);
        }
    }
}
