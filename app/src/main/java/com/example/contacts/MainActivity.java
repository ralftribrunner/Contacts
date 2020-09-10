package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    private List<Person> people=new ArrayList<Person>() ;
    RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
    private Handler async_handler=new Handler();
    private ProgressBar progressBar_async;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar_async=findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, people);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, BIND_IMPORTANT);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, BIND_IMPORTANT);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            new ContactTask().execute();
        }

    }




    @Override
    public void onItemClick(View view, int position) {
        Intent intent=new Intent(this,Activity2.class);
        intent.putExtra("id",adapter.getItem(position).id);
        intent.putExtra("name",adapter.getItem(position).name);
        intent.putExtra("email",adapter.getItem(position).email);
        intent.putExtra("number",adapter.getItem(position).number);
        startActivity(intent);
        Log.i("katt","valami");
    }


    private class ContactTask extends AsyncTask<Void, Integer, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ContentResolver cr = getContentResolver();
            final Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if (cur.getCount() > 0) {
                int counter=1;
                while (cur.moveToNext()) {

                    Person person=new Person();
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    person.id=id;
                    person.name=name;
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Log.i("username","name : " + name + ", ID : " + id);

                        // get the phone number
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phone = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            person.number=phone;
                            Log.i("userphone","phone" + phone);
                        }

                        pCur.close();

                        // get email and type

                        Cursor emailCur = cr.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (emailCur.moveToNext()) {
                            // This would allow you get several email addresses
                            // if the email addresses were stored in an array
                            String email = emailCur.getString(
                                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                           /* String emailType = emailCur.getString(
                                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));*/
                            person.email=email;
                            System.out.println("Email " + email );
                        }
                        emailCur.close();

                    }
                    people.add(person);
                    final int finalCounter = counter;
                    async_handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar_async.setProgress((100* finalCounter)/cur.getCount());
                        }
                    });
                    counter++;
                }
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            adapter.notifyDataSetChanged();
        }
    }
}
