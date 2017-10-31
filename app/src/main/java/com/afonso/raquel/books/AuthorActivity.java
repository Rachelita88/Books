package com.afonso.raquel.books;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AuthorActivity extends AppCompatActivity {

    private static final String TAGLOG = "firebase-db";
    private static final String TAG = "info";
    public static String FIREBASE_URL = "https://books-96ef3.firebaseio.com/";

    public String authorRef;
    private DatabaseReference mRef;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference removeDatabase;
    private FirebaseRecyclerAdapter mAdapter;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        setTitle("Biblioteca");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        // Añadimos el boton para añadir una nueva entrada, abre la actividad NewEntryActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthorActivity.this, NewEntryActivity.class);
                startActivity(intent);
            }
        });

        // Obtener el intent de la busqueda, obtner el query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);

        }else {
            //Obtenemos la referencia a la base de datos de Firebase, en concreto al nodo Author
            mFirebaseInstance = FirebaseDatabase.getInstance();
            mRef = mFirebaseInstance.getReference("Autor");

            final RecyclerView recycler = (RecyclerView) findViewById(R.id.lstAuthors);
            recycler.setHasFixedSize(true);
            recycler.setLayoutManager(new LinearLayoutManager(this));
            recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recycler.setItemAnimator(new DefaultItemAnimator());
            Query orderRef = mRef.orderByChild("nombre");

            //Definimos el adapter
            mAdapter = new FirebaseRecyclerAdapter<Author, AuthorHolder>(
                    Author.class, R.layout.list_author, AuthorHolder.class, orderRef) {

                //Llenamos el ViewHolder con los datos, a través del método populateViewHolder
                @Override
                public void populateViewHolder(final AuthorHolder librosviewHolder, Author autores, final int position) {
                    librosviewHolder.setNombre(autores.getNombre());
                    librosviewHolder.getAdapterPosition();

                    // Añadimos un clickListener en los elementos de la lista
                    // Y obtenemos de este elemento presionado un Objeto con la referencia a la base de datos
                    // en una URL
                    librosviewHolder.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {Object itemRef = mAdapter.getRef(position);
                                Log.i("Item reference ", itemRef.toString());

                                // Abrimos una nueva actividad, BooksActivity, y enviamos la URL
                                Intent intent = new Intent(AuthorActivity.this, BooksActivity.class);
                                intent.putExtra("itemRef", itemRef.toString());
                                startActivity(intent);
                        }
                    });

                    // Añadimos el longclickListener a los autores del recyclerView
                    librosviewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {

                            Object itemClickRef = mAdapter.getRef(position);
                            authorRef = itemClickRef.toString();
                            Log.i("Item reference ", authorRef);

                            //Desplegamos el menú contextual al hacer click
                            registerForContextMenu(recycler);
                            openContextMenu(recycler);
                            return false;
                        }
                    });

                }
            };

            recycler.setAdapter(mAdapter);
        }
    }

    private void doMySearch(String query) {
        //Obtenemos la referencia a la base de datos de Firebase, en concreto al nodo Author
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mRef = mFirebaseInstance.getReference("Autor");

        // Implementamos el RecyclerView con sus componentes: el Adapter, el ViewHolder
        // el LayoutManager, y el ItemDecoration

        final RecyclerView recycler = (RecyclerView) findViewById(R.id.lstAuthors);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycler.setItemAnimator(new DefaultItemAnimator());

        Query queryRef = mRef.orderByChild("nombre").startAt(query).endAt(query + "\uf8ff");

        //Definimos el adapter
        mAdapter = new FirebaseRecyclerAdapter<Author, AuthorHolder>(
                Author.class, R.layout.list_author, AuthorHolder.class, queryRef) {

            //Llenamos el ViewHolder con los datos, a través del método populateViewHolder
            @Override
            public void populateViewHolder(final AuthorHolder librosviewHolder, Author autores, final int position) {
                librosviewHolder.setNombre(autores.getNombre());
                librosviewHolder.getAdapterPosition();

                // Añadimos un clickListener en los elementos de la lista
                // Y obtenemos de este elemento presionado un Objeto con la referencia a la base de datos
                // en una URL
                librosviewHolder.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Object itemRef = mAdapter.getRef(position);
                        Log.i("Item reference ", itemRef.toString());

                        // Abrimos una nueva actividad, BooksActivity, y enviamos la URL
                        Intent intent = new Intent(AuthorActivity.this, BooksActivity.class);
                        intent.putExtra("itemRef", itemRef.toString());
                        startActivity(intent);

                    }
                });

                // Añadimos el longclickListener a los autores del recyclerView
                librosviewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        Object itemClickRef = mAdapter.getRef(position);
                        authorRef = itemClickRef.toString();
                        Log.i("Item reference ", authorRef);

                        //Desplegamos el menú contextual al hacer click
                        registerForContextMenu(recycler);
                        openContextMenu(recycler);
                        return false;
                    }
                });

            }
        };

        recycler.setAdapter(mAdapter);
    }


    //Creamos el menú contextual
    @Override
    public void onCreateContextMenu(final ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_delete, menu);
    }

    //Determinamos que acción produce cada elemento del menú
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_edit:

                Intent intent = new Intent(AuthorActivity.this, NewEntryActivity.class);
                intent.putExtra("authorRef", authorRef);
                Log.i("REFERENCIA ", authorRef);
                startActivity(intent);

                Toast editToast = Toast.makeText(this, "opcion editar pulsada", Toast.LENGTH_SHORT);
                editToast.show();
                return true;

            case R.id.menu_delete:
                showRemoveConfirmationDialog();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showRemoveConfirmationDialog() {
        //Crearemos una alerta, con un boton para confirmar o cancelar
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea eliminar este autor y todos sus libros? " );
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                removeDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl(authorRef);
                removeDatabase.child("nombre").removeValue();
                removeDatabase.child("libros").removeValue();

                Context context = getApplicationContext();
                Toast deleteToast = Toast.makeText(context, "Autor eliminado", Toast.LENGTH_SHORT);
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

    //Creamos el menu de la app bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_autor_activity, menu);

        // Obtener SearchView y establecer la configuración de búsqueda
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Asume que la actividad actual es la actividad que se puede buscar
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        searchView.setIconifiedByDefault(true); // iconifique el widget

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Obtenemos la referencia a la base de datos de Firebase, en concreto al nodo Author
                mFirebaseInstance = FirebaseDatabase.getInstance();
                mRef = mFirebaseInstance.getReference("Autor");

                // Implementamos el RecyclerView con sus componentes: el Adapter, el ViewHolder
                // el LayoutManager, y el ItemDecoration

                final RecyclerView recycler = (RecyclerView) findViewById(R.id.lstAuthors);
                recycler.setHasFixedSize(true);
                recycler.setItemAnimator(new DefaultItemAnimator());

                Query queryRef = mRef.orderByChild("nombre").startAt(newText).endAt(newText + "\uf8ff");

                //Definimos el adapter
                mAdapter = new FirebaseRecyclerAdapter<Author, AuthorHolder>(
                        Author.class, R.layout.list_author, AuthorHolder.class, queryRef) {

                    //Llenamos el ViewHolder con los datos, a través del método populateViewHolder
                    @Override
                    public void populateViewHolder(final AuthorHolder librosviewHolder, Author autores, final int position) {
                        librosviewHolder.setNombre(autores.getNombre());
                        librosviewHolder.getAdapterPosition();

                        // Añadimos un clickListener en los elementos de la lista
                        // Y obtenemos de este elemento presionado un Objeto con la referencia a la base de datos
                        // en una URL
                        librosviewHolder.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Object itemRef = mAdapter.getRef(position);
                                Log.i("Item reference ", itemRef.toString());

                                // Abrimos una nueva actividad, BooksActivity, y enviamos la URL
                                Intent intent = new Intent(AuthorActivity.this, BooksActivity.class);
                                intent.putExtra("itemRef", itemRef.toString());
                                startActivity(intent);

                            }
                        });

                        // Añadimos el longclickListener a los autores del recyclerView
                        librosviewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {

                                Object itemClickRef = mAdapter.getRef(position);
                                authorRef = itemClickRef.toString();
                                Log.i("Item reference ", authorRef);

                                //Desplegamos el menú contextual al hacer click
                                registerForContextMenu(recycler);
                                openContextMenu(recycler);
                                return false;
                            }
                        });

                    }
                };

                recycler.setAdapter(mAdapter);

                return false;
            }


        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //Borrar todos los autores
            case R.id.action_delete:
                showDeleteAllConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllConfirmationDialog() {
        //Crearemos una alerta, con un boton para confirmar o cancelar
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea eliminar toda su biblioteca?");
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //Click "Eliminar" y borrar toda la bibliteca
                mRef.removeValue();

                Context context = getApplicationContext();
                Toast deleteToast = Toast.makeText(context, "Biblioteca eliminada", Toast.LENGTH_SHORT);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}