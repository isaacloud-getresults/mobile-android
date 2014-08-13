package getresultsapp.sointeractve.pl.getresultsapp.isaacloud.providers;

import java.util.concurrent.ExecutionException;

import getresultsapp.sointeractve.pl.getresultsapp.config.IsaaCloudSettings;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.UserIC;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.tasks.GetUserIdTask;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.tasks.GetUserTask;
import getresultsapp.sointeractve.pl.getresultsapp.socket.SocketIONotifier;

public class UserProvider {
    public static final UserProvider INSTANCE = new UserProvider();

    private UserIC userIC;

    private UserProvider() {
        // Exists only to defeat instantiation.
    }


    public UserIC getData() {
        if (userIC == null) {
            reload();
        }
        return userIC;
    }


    public UserIC getUpToDateData() {
        reload();
        return userIC;
    }

    private void reload() {
        try {
            final int userId = getId();
            if (userId < 0) {
                return;
            }

            final GetUserTask getUser = new GetUserTask();
            final UserIC newUserData = getUser.execute(userId).get();
            if (newUserData == null) {
                return;
            }

            if (!isLoaded()) {
                onLogInAction(userId);
            }
            logIn(newUserData);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } catch (final ExecutionException e) {
            e.printStackTrace();
        }
    }

    private int getId() throws ExecutionException, InterruptedException {
        if (isLoaded()) {
            return userIC.getId();
        } else {
            return getUserId();
        }
    }


    private Integer getUserId() throws ExecutionException, InterruptedException {
        final GetUserIdTask getUserId = new GetUserIdTask();
        return getUserId.execute(IsaaCloudSettings.LOGIN_EMAIL).get();
    }

    private boolean isLoaded() {
        return userIC != null;
    }

    private void onLogInAction(final int userId) {
        SocketIONotifier.INSTANCE.connect(userId);
    }

    private void logIn(final UserIC newUserData) {
        userIC = newUserData;
    }

    public void clear() {
        logIn(null);
    }
}
