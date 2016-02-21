/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package gsc.profile_gw2mobile.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import gsc.profile_gw2mobile.R;
import gsc.profile_gw2mobile.app.AppConfig;
import gsc.profile_gw2mobile.app.AppController;
import gsc.profile_gw2mobile.helper.SQLiteHandler;
import gsc.profile_gw2mobile.helper.SessionManager;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputUsername;
    private EditText inputPassword;
    private EditText inputPassword_re;
    private EditText inputApi_key;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPassword_re = (EditText) findViewById(R.id.password_re);
        inputApi_key = (EditText) findViewById(R.id.api_key);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        if (session.isLoggedIn()) {
             Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String username = inputUsername.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String password_re = inputPassword_re.getText().toString().trim();
                String api_key = inputApi_key.getText().toString().trim();

                if (!name.isEmpty() && !api_key.isEmpty() && !password.isEmpty() && !password_re.isEmpty() && password_re.equals(password)) {
                    registerUser(name, username, api_key, password);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter your details correctly!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void registerUser(final String name, final String username, final String api_key, final String password) {
        String tag_string_req = "req_register";
        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String username = user.getString("username");
                        String api_key = user.getString("api_key");
                        String created_at = user.getString("created_at");
                        db.addUser(name, username, api_key, uid, created_at);
                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent( RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("username", username);
                params.put("api_key", api_key);
                params.put("password", password);
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    private void hideDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }
}
