package com.afonso.raquel.books;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Raquel on 26/09/2017.
 */

public class AddBookActivity extends AppCompatActivity{

    private EditText mTitleEditText;
    private DatabaseReference ref;
    private FirebaseDatabase mFirebaseInstance;
    public String currentAuthorRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        //Recibimos el intent y obtenemos el objeto con la referencia URL
        Intent intent = getIntent();
        currentAuthorRef = getIntent().getStringExtra("authorRef");
    }

    private boolean saveNewEntry() {

        //Buscamos los editTextView
        mTitleEditText = (EditText) findViewById(R.id.add_new_book);

        String titleString = mTitleEditText.getText().toString();

        if (mTitleEditText == null && TextUtils.isEmpty(titleString)) {
                return false;

        } else {

            mFirebaseInstance = FirebaseDatabase.getInstance();
            ref = mFirebaseInstance.getReferenceFromUrl(currentAuthorRef).push();
            ref.setValue(new NewBookEntry(titleString));

            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_new_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                if (saveNewEntry()) {
                    Context context = getApplicationContext();
                    Toast deleteToast = Toast.makeText(context, "libro añadido", Toast.LENGTH_SHORT);
                    deleteToast.show();
                    // Exit activity
                    finish();
                    return true;
                } else {
                    return false;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
