package com.afonso.raquel.books;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raquel on 12/09/2017.
 */

public class NewEntryActivity extends AppCompatActivity {

    public String authorRef;
    public String bookRef;
    private EditText mNameEditText;
    private EditText mTitleEditText;

    private DatabaseReference editDatabase;
    private DatabaseReference editAuthorDatabase;
    private DatabaseReference ref;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        //Examinar el intent usado para lanzar la actividad
        //para saber si es un una nueva entrada o una edición
        final Intent intent = getIntent();
        bookRef = getIntent().getStringExtra("itemRef");

        final Intent authorintent = getIntent();
        authorRef = getIntent().getStringExtra("authorRef");

        //Mostrar los valores de los campos
        mNameEditText = (EditText) findViewById(R.id.edit_name_author);
        mTitleEditText = (EditText) findViewById(R.id.edit_title_book);

        //Si el intent no contiene una referencia al database, sabes que es una nueva entrada
        if (bookRef == null && authorRef == null) {
            //Cambiamos el titulo de la app bar
            setTitle("Nuevo Libro");


        } else {
            //De otra manera es un libro existente, asi que cambiamos el titulo
            setTitle("Editar");

            if (authorRef == null){
                mNameEditText.setEnabled(false);
                mNameEditText.setFocusable(false);

                // Obtenemos la referencia a la base de datos
                editDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(bookRef).getParent().getParent().child("nombre");
                Log.i("Referencia", editDatabase.toString());

                //Obtenemos el nombre del autor seleccionado
                editDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String nombre = dataSnapshot.getValue().toString();

                        mNameEditText.setText(nombre);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                //Obtenemos el titulo del libro seleccionado
                editDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(bookRef).child("titulo");
                Log.i("Referencia", editDatabase.toString());

                if (bookRef != null){
                editDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String titulo = dataSnapshot.getValue().toString();

                        mTitleEditText.setText(titulo);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                }else {
                    mTitleEditText.setText("titulo desconocido");
                }

            } else {

                mTitleEditText.setEnabled(false);
                mTitleEditText.setFocusable(false);
                // Obtenemos la referencia a la base de datos
                editAuthorDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(authorRef).child("nombre");
                Log.i("Referencia", editAuthorDatabase.toString());


                    //Obtenemos el nombre del autor seleccionado
                    editAuthorDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                String nombre = dataSnapshot.getValue().toString();
                                mNameEditText.setText(nombre);

                            } else {
                                mNameEditText.setText("Autor desconocido");
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

            }
        }
    }

    private boolean saveNewEntry() {

        //Buscamos los editTextView
        mNameEditText = (EditText) findViewById(R.id.edit_name_author);
        mTitleEditText = (EditText) findViewById(R.id.edit_title_book);

        String nameString = mNameEditText.getText().toString();
        String titleString = mTitleEditText.getText().toString();

        //Si el intent no contiene una referencia al database, sabes que es una nueva entrada
        if (bookRef == null && authorRef == null) {

            mFirebaseInstance = FirebaseDatabase.getInstance();
            ref = mFirebaseInstance.getReference("Autor");

            DatabaseReference newAuthorRef = ref.push();
            newAuthorRef.setValue(new NewAuthorEntry(nameString));

            DatabaseReference newBookRef = newAuthorRef.child("libros").push();
            newBookRef.setValue(new NewBookEntry(titleString));

            Context context = getApplicationContext();
            Toast deleteToast = Toast.makeText(context, "libro añadido", Toast.LENGTH_SHORT);
            deleteToast.show();

        } else {

            if (authorRef == null){
            //Salvar cambios en el libro
            Map<String, Object> titleUpdate = new HashMap<>();
            titleUpdate.put("titulo", titleString);

            editAuthorDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(bookRef);
            editAuthorDatabase.updateChildren(titleUpdate);

            }else{

            Map<String, Object> nameUpdate = new HashMap<>();
            nameUpdate.put("nombre", nameString);
            // Obtenemos la referencia a la base de datos
            editAuthorDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(authorRef);
            Log.i("NOMBRE REF", editAuthorDatabase.toString());
            editAuthorDatabase.updateChildren(nameUpdate);

            Context context = getApplicationContext();
            Toast deleteToast = Toast.makeText(context, "libro editado", Toast.LENGTH_SHORT);
            deleteToast.show();
            }
        }

        return true;
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
        // El usuario hace click en el menu de la appbar
        switch (item.getItemId()) {

            case R.id.action_save:
                // salvar la nueva entrada en la basede datos
                if (saveNewEntry()) {
                    // Exit activity
                    finish();
                    return true;
                } else {
                    return false;
                }
        }
        return super.onOptionsItemSelected(item);
    }
}