package com.afonso.raquel.books;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Raquel on 04/09/2017.
 */
    //Definimos la clase AuthorHolder que extiende o hereda de la clase RecyclerView.ViewHolder
    // E implementamos la interfaz OnClickListener
public class AuthorHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener,View.OnCreateContextMenuListener {

    private View mView;
    private View.OnClickListener listener;

    public AuthorHolder(View itemView) {
        super(itemView);
        mView = itemView;
        itemView.setOnClickListener(this);
    }

    //Añadimos como atributos las referencias a los controladores del layout, nuestro caso
    // al elemento nombre

    public Void setNombre(String nombre) {
        TextView field = (TextView) mView.findViewById(R.id.text_name);
        field.setText(nombre);
        return null;
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null)
            listener.onClick(view);
    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

    }
}
