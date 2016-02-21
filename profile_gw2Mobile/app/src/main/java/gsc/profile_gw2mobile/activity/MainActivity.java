package gsc.profile_gw2mobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;

import gsc.profile_gw2mobile.R;
import gsc.profile_gw2mobile.app.AppConfig;

import gsc.profile_gw2mobile.helper.SQLiteHandler;
import gsc.profile_gw2mobile.helper.SessionManager;
import gsc.profile_gw2mobile.helper.sellableIds_AsyncTask;
import gsc.profile_gw2mobile.helper.tokenInfo_root;


public class MainActivity extends Activity implements sellableIds_AsyncTask.sellableIdsFilter {

	private TextView txtName;
	private TextView txtEmail;
	private TextView txtApi_key;
	private Button btnLogout;

	ListView listView2;

	private SQLiteHandler db;
	private SessionManager session;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		txtName = (TextView) findViewById(R.id.name);
		txtEmail = (TextView) findViewById(R.id.username);
		txtApi_key = (TextView) findViewById(R.id.api_key);
		btnLogout = (Button) findViewById(R.id.btnLogout);
		db = new SQLiteHandler(getApplicationContext());

		session = new SessionManager(getApplicationContext());

		if (!session.isLoggedIn()) {
			logoutUser();
		}

		HashMap<String, String> user = db.getUserDetails();

		String name = user.get("name");
		String email = user.get("email");
		String api_key = user.get("api_key");

		txtName.setText(name);
		txtEmail.setText(email);
		txtApi_key.setText(api_key);

		btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});

		String sellableIdsUrl = AppConfig.URL_API_ACCOUNT + api_key;
		sellableIds_AsyncTask sellableIds_AsyncTask = new sellableIds_AsyncTask(this);
		sellableIds_AsyncTask.setSellableIdsFilter(this);
		sellableIds_AsyncTask.execute(sellableIdsUrl);
	}

	public void sellableIdsFilter(String allSellableIds) {
		Gson gson = new Gson();
		tokenInfo_root tokenInfo_root = gson.fromJson(allSellableIds, tokenInfo_root.class);
		System.out.println(allSellableIds);

		for(int i = 0; i < tokenInfo_root.permissions.size(); i++){
			System.out.println(tokenInfo_root.permissions.get(i));
		}
	}

	private void logoutUser() {
		session.setLogin(false);
		db.deleteUsers();

		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
}

