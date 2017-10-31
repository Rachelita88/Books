package com.afonso.raquel.books;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ObbInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Raquel on 11/09/2017.
 */

public class BooksActivity extends AppCompatActivity{

    public String authorRef;
    public String removeRef;
    public String addbookRef;
    private DatabaseReference authorDatabase;
    private DatabaseReference removeDatabase;
    private FirebaseRecyclerAdapter bookAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        //Recibimos el intent y obtenemos el objeto con la referencia URL
        final Intent intent = getIntent();
        authorRef = getIntent().getStringExtra("itemRef");


            // Obtenemos la referencia a la base de datos
            authorDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(authorRef).child("nombre");

            // Añadimos el boton para añadir una nueva entrada, abre la actividad NewEntryActivity
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent bookintent = new Intent(BooksActivity.this, AddBookActivity.class);
                    Object authorRef = authorDatabase.getRef();
                    Log.i("authorRef ", authorRef.toString());
                    bookintent.putExtra("authorRef", authorRef.toString());
                    startActivity(bookintent);
                }
            });


            // Obtenemos la referencia a la base de datos
            authorDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(authorRef).child("nombre");

            //Obtenemos el nombre del autor seleccionado
            authorDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String nombre = dataSnapshot.getValue().toString();
                        setTitle(nombre);
                    } else {
                        setTitle("Autor desconocido");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


            //Referencia a la base de datos, al nodo libros
            authorDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(authorRef).child("libros");

            final RecyclerView recycler = (RecyclerView) findViewById(R.id.lstBooks);
            recycler.setHasFixedSize(true);
            recycler.setLayoutManager(new LinearLayoutManager(this));
            recycler.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recycler.setItemAnimator(new DefaultItemAnimator());

            bookAdapter = new FirebaseRecyclerAdapter<Book, BookHolder>(
                    Book.class, R.layout.list_books, BookHolder.class, authorDatabase) {

                @Override
                public void populateViewHolder(final BookHolder viewHolder, Book books, final int position) {
                    viewHolder.setTitulo(books.getTitulo());
                    viewHolder.getAdapterPosition();

                    // Añadimos el clickListener a los libros del recyclerView
                    viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {

                            //showRemoveConfirmationDialog();
                            Object itemClickRef = bookAdapter.getRef(position);
                            removeRef = itemClickRef.toString();
                            Log.i("Referencia del libro ", removeRef);

                            //Desplegamos el menú contextual al hacer click
                            registerForContextMenu(recycler);
                            openContextMenu(recycler);
                            return false;
                        }
                    });
                }
            };

            recycler.setAdapter(bookAdapter);
    }

    //Creamos el menú contextual
    @Override
    public void onCreateContextMenu(final ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_delete, menu);
    }

    //Determinamos que acción produce cada elemento del menú
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_edit:

                Intent intent = new Intent(BooksActivity.this, NewEntryActivity.class);
                intent.putExtra("itemRef", removeRef);
                startActivity(intent);

                return true;

            case R.id.menu_delete:
                showRemoveConfirmationDialog();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bookAdapter.cleanup();
     }

    private void showRemoveConfirmationDialog() {
        //Crearemos una alerta, con un boton para confirmar o cancelar
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Desea eliminar este libro " );
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                removeDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(removeRef);
                removeDatabase.removeValue();

                Context context = getApplicationContext();
                Toast deleteToast = Toast.makeText(context, "libro eliminado", Toast.LENGTH_SHORT);
                deleteToast.show();

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}