package pl.rosiakit.px500;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import pl.rosiakit.px500.utils.NetworkRequest;

public class AccountActivity extends OnlineActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        prepareActionBar("You account");
        preferences = getSharedPreferences("api_settings", MODE_PRIVATE);
        findViewById(R.id.action_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        findViewById(R.id.action_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogout();
            }
        });
        showAppropriateViews();
    }

    private void showAppropriateViews() {
        String token = preferences.getString("auth_token", "");
        if(StringUtils.isNotBlank(token)) {
            findViewById(R.id.login_box).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.auth_token)).setText(token);
            findViewById(R.id.auth_info_box).setVisibility(View.VISIBLE);
        }
        else
        {
            findViewById(R.id.login_box).setVisibility(View.VISIBLE);
            findViewById(R.id.auth_info_box).setVisibility(View.GONE);
        }
    }

    private void doLogout() {
        saveUIDAndTokenToPreferences(0, "");
        showToast(R.string.logout_success);
        showAppropriateViews();
    }

    private void doLogin() {
        String username = ((EditText) findViewById(R.id.account_login)).getText().toString();
        String password = ((EditText) findViewById(R.id.account_password)).getText().toString();

        if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            doLoginCall(username, password);
        }
        else {
            showToast(R.string.fill_account_credentials);
        }
    }

    private void doLoginCall(final String username, final String password) {
        if(isOnline()) {
            Thread loginThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = "http://vps2.rosiak.it:8080/login";
                    NetworkRequest networkRequest = new NetworkRequest(url, "POST");
                    networkRequest.addPostParam("username", username);
                    networkRequest.addPostParam("password", password);
                    try {
                        String result = networkRequest.execute();
                        JSONObject photosObject = new JSONObject(result);
                        if(photosObject.getInt("code") == 200) {
                            String token = photosObject.getString("msg");
                            int uid = 1; //photosObject.getInt("uid");
                            saveUIDAndTokenToPreferences(uid, token);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showAppropriateViews();
                                }
                            });
                        }
                        else {
                            showToast(R.string.something_went_wrong);
                        }
                    } catch (Exception e) {
                        showToast(R.string.something_went_wrong);
                    }
                }
            });

            loginThread.start();
        }
        else{
            showToast(R.string.no_internet);
        }
    }

    private synchronized void saveUIDAndTokenToPreferences(int uid, String token) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("uid", uid);
        editor.putString("auth_token", token);
        editor.commit();
    }

    private void prepareActionBar(String title) {
        ActionBar bar = getSupportActionBar();
        if(bar != null) {
            bar.setTitle(title);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
