package io.grabity.planetwallet.Common.components;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by. JcobPark on 2018. 08. 29
 */

public class ViewPagerAdapter< T extends Fragment > extends FragmentStatePagerAdapter {
    ArrayList< T > fragments;

    public ViewPagerAdapter( FragmentManager fm, ArrayList< T > fragments ) {
        super( fm );
        this.fragments = fragments;
    }

    @Override
    public int getItemPosition( Object object ) {
        return this.fragments.indexOf( object );
    }


    @Override
    public int getCount( ) {
        return this.fragments.size( );
    }

    @Override
    public Fragment getItem( int position ) {
        return this.fragments.get( position );
    }

}
