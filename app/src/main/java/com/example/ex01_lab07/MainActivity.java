package com.example.ex01_lab07;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentProvider;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    EditText edtSearch;

    Cursor data;

    CursorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        edtSearch = findViewById(R.id.edtSearch);

        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
        checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
            }, 1234);
        return;
        } else {
            readContact();
        }

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();

                performSearch(query);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void performSearch(String query) {
        String selection = null;
        String[] selectionArgs = null;

        if (!query.isEmpty()) {
            selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
            selectionArgs = new String[]{"%" + query + "%"};
        }

        data = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null
        );

        // Cập nhật dữ liệu trong CursorAdapter
        adapter.changeCursor(data);
    }

    private void readContact() {
        String selection = null;
        String keyword = edtSearch.getText().toString();

        if(!keyword.equals("")){
            selection = ContactsContract.Contacts.DISPLAY_NAME  +" LIKE "+"\"%"+ keyword+ "%\"";
        }

        data = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                selection,
                null,
                null
        );
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                data,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                new int[]{android.R.id.text1}
        );
        listView.setAdapter(adapter);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            readContact();
        }else {
            Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
        }
    }

}