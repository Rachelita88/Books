package com.afonso.raquel.books;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Raquel on 25/09/2017.
 */

public class BookHolder extends RecyclerView.ViewHolder
        implements View.OnCreateContextMenuListener, View.OnClickListener {

    private View mView;
    private View.OnClickListener listener;

    public BookHolder(View itemView) {
        super(itemView);
        mView = itemView;
        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }
    //Añadimos como atributos las referencias a los controladores del layout, nuestro caso
    // al elemento nombre

    public Void setTitulo(String titulo) {
        TextView field = (TextView) mView.findViewById(R.id.book_textView);
        field.setText(titulo);
        return null;
    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null)
            listener.onClick(view);
    }

}


