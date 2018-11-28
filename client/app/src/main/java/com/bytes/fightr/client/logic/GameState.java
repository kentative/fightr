package com.bytes.fightr.client.logic;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.bytes.fightr.client.FightrApplication;
import com.bytes.fightr.common.model.Fighter;
import com.bytes.fightr.common.model.Match;
import com.bytes.fmk.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kent on 4/14/2017.
 * This singleton represents the game's state.
 * The User is the main entity. Other stateful entities such as:
 * <ul>
 * <li>match</li>
 * <li>active users</li
 * <li>active fighters</li>
 * </ul>
 * support the state of the <i>user</i>.
 * <p>
 */
public enum GameState {

    /**
     * Singleton instance
     */
    Instance;


    // Keys for storing state in the Bundle.
    private final static String REGISTRATION_STATE_KEY = "gameState.user.registration.state.key";
    private final static String USER_KEY = "gameState.user.key";
    private final static String MATCH_KEY = "gameState.match.key";
    private final static String ACTIVE_USERS_KEY = "gameState.active.user.key";
    private final static String ACTIVE_FIGHTERS_KEY = "gameState.active.fighter.key";

    /**
     * The registration state as reported from the server
     */
    private boolean userRegistered;

    /**
     * The User
     */
    private User user;

    /**
     * The user's match
     */
    private Match match;

    /**
     * The list of active users
     * key = userId
     * value = User
     */
    private Map<String, User> activeUsers;

    /**
     * The list of active fighters corresponding to the list of active users
     */
    private Map<String, Fighter> activeFighters;

    /**
     * Singleton constructor.
     */
    GameState() {
       init();
    }

    /**
     * This is for testing only!
     */
    public void init() {
        this.activeFighters = new HashMap<>();
        this.activeUsers = new HashMap<>();
        this.match = null;
    }

    /**
     * Request to add a user.
     *
     * @param user - the user to be added
     */
    public void addUser(@NonNull User user) {
        // addUser active user
        // TODO consider adding timestamp to last addUser and prune if older than 1 week
        activeUsers.put(user.getId(), user);
    }


    /**
     * Request to remove the specified user.
     * Also remove the corresponding fighter
     *
     * @param userId the user to be removed
     */
    public void removeUser(@NonNull String userId) {
        User user = activeUsers.remove(userId);
        activeFighters.remove(user.getAvatarId());
    }

    /**
     * Request to add the fighter
     *
     * @param fighter the fighter to be added
     */
    public void addFighter(@NonNull Fighter fighter) {
        activeFighters.put(fighter.getId(), fighter);
    }

    /**
     * Request to update data for the fighter
     *
     * @param fighter - the fighter to be updated
     */
    public void update(@NonNull Fighter fighter) {
        if (activeFighters.containsKey(fighter.getId())) {
            activeFighters.get(fighter.getId()).update(fighter);
        }
    }

    /**
     * Retrieve the fighter matching the specified id
     *
     * @param id
     * @return
     */
    public Fighter getFighter(@NonNull String id) {
        if (activeFighters.containsKey(id)) {
            return activeFighters.get(id);
        }
        return null;
    }

    /**
     * Get the user's fighter
     *
     * @return the User's Fighter
     */
    public Fighter getUserFighter() {
        String avatarId = user.getAvatarId();
        if (avatarId == null) {
            return null;
        }
        return activeFighters.get(avatarId);
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public User getUser(String userId) {
        return activeUsers.get(userId);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        addUser(user);
    }

    public List<User> getActiveUsers() {
        return new ArrayList<>(activeUsers.values());
    }

    public void clearActiveCaches() {
        activeUsers.clear();
        addUser(user);
    }

    public List<Fighter> getActiveFighters() {
        return new ArrayList<>(activeFighters.values());
    }

    public boolean isUserRegistered() {
        return userRegistered;
    }

    public void setUserRegistered(boolean userRegistered) {
        this.userRegistered = userRegistered;
    }


    /**
     * Request to load GameState from SharedPreferences
     */
    public void loadInstanceState() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FightrApplication.getInstance());

        if (preferences != null) {
            Gson gson = new Gson();
            if (preferences.contains(REGISTRATION_STATE_KEY)) {
                userRegistered = preferences.getBoolean(REGISTRATION_STATE_KEY, false);
            }

            if (preferences.contains(USER_KEY)) {
                user = gson.fromJson(preferences.getString(USER_KEY, null), User.class);
            }

            if (preferences.contains(MATCH_KEY)) {
                match = gson.fromJson(preferences.getString(MATCH_KEY, null), Match.class);
            }

            if (preferences.contains(ACTIVE_USERS_KEY)) {
                activeUsers = gson.fromJson(preferences.getString(ACTIVE_USERS_KEY, null),
                        new TypeToken<Map<String, User>>() {
                        }.getType());
            }

            if (preferences.contains(ACTIVE_FIGHTERS_KEY)) {
                activeFighters = gson.fromJson(preferences.getString(ACTIVE_FIGHTERS_KEY, null),
                        new TypeToken<Map<String, Fighter>>() {
                        }.getType());
            }
        }
    }


    /**
     * Request to save the state using SharedPreferences.
     * The states are serialized to json and stored as string
     */
    public void saveInstanceState() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FightrApplication.getInstance());
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        editor.putBoolean(REGISTRATION_STATE_KEY, userRegistered);
        if (user != null) editor.putString(USER_KEY, gson.toJson(user));
        if (match != null) editor.putString(MATCH_KEY, gson.toJson(match));
        if (activeFighters != null) editor.putString(ACTIVE_FIGHTERS_KEY, gson.toJson(activeFighters));
        if (activeUsers != null) editor.putString(ACTIVE_USERS_KEY, gson.toJson(activeUsers));

        editor.commit();
    }

    public Fighter getTargetFighter() {
        // todo
        return null;
    }

}
