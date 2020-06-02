package com.appsworld.jsonparsingex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String getString;
    private ProgressDialog progressDialog;
    private List<Contact> contactList;
    private CustomAdapter customAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        contactList = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        new MyTasks().execute();
        GetAsynckJsonData();
    }

    private void GetAsynckJsonData() {
    }

    public class MyTasks extends AsyncTask<String, Void, String> {
        @Override
        public String doInBackground(String... args) {
            String json_url = "https://api.androidhive.info/contacts/";

            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                while ((getString = bufferedReader.readLine()) != null) {
                    stringBuffer.append(getString + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuffer.toString().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading Please Wait....");
            progressDialog.setCancelable(true);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                Log.d("Result", result);

                 JSONArray jsonArray = jsonObject.getJSONArray("contacts");
                 for (int i=0;i<jsonArray.length();i++){
                     JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                     Contact contact = new Contact();
                     contact.setId(jsonObject1.getString("id"));
                     contact.setName(jsonObject1.getString("name"));
                     contact.setEmail(jsonObject1.getString("email"));
                     contactList.add(contact);

                 }
                } catch (JSONException ex) {
                ex.printStackTrace();

        }
            customAdapter = new CustomAdapter(contactList, MainActivity.this);
            recyclerView.setAdapter(customAdapter);
            progressDialog.dismiss();
        }
    }
}