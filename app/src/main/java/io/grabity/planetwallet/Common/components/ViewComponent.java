package io.grabity.planetwallet.Common.components;

import android.view.View;

import androidx.annotation.IdRes;

public abstract class ViewComponent implements View.OnClickListener {

    private PlanetWalletActivity activity;
    private View view;

    public ViewComponent( PlanetWalletActivity activity ) {
        this.activity = activity;
    }

    public void viewInit( ) {

    }

    public PlanetWalletActivity getActivity( ) {
        return activity;
    }

    @Override
    public void onClick( View v ) {

    }

    public void setView( View view ) {
        this.view = view;
    }

    public < T extends View > T findViewById( @IdRes int id ) {
        if ( view != null ) return view.findViewById( id );
        if ( activity != null ) return activity.findViewById( id );
        return null;
    }

}
