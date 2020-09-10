package com.example.contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import javax.xml.datatype.Duration;

import static android.app.PendingIntent.getActivity;


public class Activity2 extends AppCompatActivity implements View.OnClickListener{

    EditText edit_name;
    EditText edit_phone;
    EditText edit_email;
    boolean is_edit_enabled;
    Person person=new Person();
    private Handler thread_handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        Intent intent=getIntent();
        person.id=intent.getStringExtra("id");
        person.email=intent.getStringExtra("email");
        if (person.email==null) person.email="";
        person.number=intent.getStringExtra("number");
        if (person.number==null) person.number="";
        person.name=intent.getStringExtra("name");
        if (person.name==null) person.name="";
        edit_name=findViewById(R.id.edit_name);
        edit_name.setText(person.name);
        edit_phone=findViewById(R.id.edit_phone);
        edit_phone.setText(person.number);
        edit_email=findViewById(R.id.edit_email);
        edit_email.setText(person.email);
        setEdit_text_enable(false);
        Button btn_edit=findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(this);
        Button btn_save=findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit:
               if (is_edit_enabled) setEdit_text_enable(false);
               else setEdit_text_enable(true);
                break;
            case R.id.btn_save:
                    new Thread(runnable).start();
                break;
        }
    }

    public void setEdit_text_enable(boolean bool) {
        edit_phone.setEnabled(bool);
        edit_name.setEnabled(bool);
        edit_email.setEnabled(bool);
        is_edit_enabled=bool;
    }



    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            intentupdate();
        }
    };

    public void intentupdate() {
        Intent intent=new Intent(Intent.ACTION_EDIT);

        intent.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(person.id)));
        boolean is_changed=false;
        if (!String.valueOf(edit_name.getText()).equals(person.name)) {

            Log.i("name____",String.valueOf(edit_name.getText()));
            intent.putExtra(ContactsContract.Intents.Insert.NAME, edit_name.getText());
            person.name= String.valueOf(edit_name.getText());
            is_changed=true;
        }
        if (!String.valueOf(edit_phone.getText()).equals(person.number)) {
            Log.i("phone____",String.valueOf(edit_phone.getText()) +"---"+person.number);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, edit_phone.getText());
            person.number=String.valueOf(edit_phone.getText());
            is_changed=true;
        }
        if (!String.valueOf(edit_email.getText()).equals(person.email) && null!=person.email) {
            Log.i("email____",String.valueOf(edit_email.getText()) +"---"+person.email);
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, edit_email.getText());
            person.email=String.valueOf(edit_email.getText());
            is_changed=true;
        }
        if (is_changed) {
            startActivity(intent);
        }
        else {
            thread_handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"Nothing changed!",Toast.LENGTH_SHORT).show();
                }
            });

        }

    }


}
