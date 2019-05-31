package io.grabity.planetwallet.Views.p5_Token.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.ETH;
import io.grabity.planetwallet.Views.p5_Token.Adapter.TokenAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.OnInsideItemClickListener;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class TokenListFragment extends PlanetWalletFragment implements View.OnClickListener, TextWatcher, OnInsideItemClickListener, AdvanceRecyclerView.OnItemClickListener {

    private ViewMapper viewMapper;
    private TokenAdapter adapter;
    private ArrayList< ETH > items;
    private ArrayList< ETH > filterItems;

    public TokenListFragment( ) {
    }

    public static TokenListFragment newInstance( ) {
        TokenListFragment fragment = new TokenListFragment( );
        return fragment;
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.fragment_token_list );
        viewMapper = new ViewMapper( );

        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.etSearch.addTextChangedListener( this );
        viewMapper.btnClear.setOnClickListener( this );
    }

    @Override
    public void setData( ) {
        super.setData( );
        /**
         * 임시작업
         */
        items = new ArrayList<>( );

        {
            ETH item = new ETH( );
            item.setName( "iOTA" );
            item.setIconRes( R.drawable.icon_iota );
            items.add( item );
        }


        {
            ETH item = new ETH( );
            item.setName( "GBT" );
            item.setIconRes( R.drawable.icon_gbt );
            items.add( item );
        }


        {
            ETH item = new ETH( );
            item.setName( "Omisego" );
            item.setIconRes( R.drawable.icon_omg );
            items.add( item );
        }


        {
            ETH item = new ETH( );
            item.setName( "Ethereum" );
            item.setIconRes( R.drawable.icon_eth );
            items.add( item );
        }

        {
            ETH item = new ETH( );
            item.setName( "Bitcoin" );
            item.setIconRes( R.drawable.icon_bit );
            items.add( item );
        }

        filterItems = new ArrayList<>( items );
        adapter = new TokenAdapter( getContext( ), filterItems );
        adapter.setOnInsideItemClickListener( this );
        viewMapper.listView.setAdapter( adapter );
        viewMapper.listView.setOnItemClickListener( this );

    }

    @Override
    public void onClick( View v ) {
        if ( v == viewMapper.btnClear ) {
            viewMapper.etSearch.setText( "" );
            updateSearchView( );
        }
    }

    @Override
    public void onItemClick( AdvanceRecyclerView recyclerView, View view, int position ) {

    }

    @Override
    protected void onUpdateTheme( boolean theme ) {
        super.onUpdateTheme( theme );
        if ( theme ) {
            viewMapper.etSearch.setTextColor( Color.parseColor( "#000000" ) );
            viewMapper.etSearch.setHintTextColor( Color.parseColor( "#aaaaaa" ) );
            viewMapper.etSearch.setBackgroundColor( Color.parseColor( "#FCFCFC" ) );
        } else {
            viewMapper.etSearch.setTextColor( Color.parseColor( "#FFFFFF" ) );
            viewMapper.etSearch.setHintTextColor( Color.parseColor( "#5C5964" ) );
            viewMapper.etSearch.setBackgroundColor( Color.parseColor( "#111117" ) );
        }
    }

    private void updateSearchView( ) {
        viewMapper.ImageNotSearch.setVisibility( viewMapper.etSearch.getText( ).length( ) == 0 ? View.VISIBLE : View.INVISIBLE );
        viewMapper.btnClear.setVisibility( viewMapper.etSearch.getText( ).length( ) == 0 ? View.GONE : View.VISIBLE );
        viewMapper.imageSearch.setVisibility( viewMapper.etSearch.getText( ).length( ) >= 1 ? View.VISIBLE : View.INVISIBLE );
    }

    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {
        filterItems.clear( );
        for ( int i = 0; i < items.size( ); i++ ) {
            if ( items.get( i ).getName( ).toLowerCase( ).contains( viewMapper.etSearch.getText( ).toString( ).toLowerCase( ) ) ) {
                filterItems.add( items.get( i ) );
            }
        }

        if ( filterItems.size( ) == 0 ) {
            viewMapper.textNoItem.setVisibility( View.VISIBLE );
        } else {
            viewMapper.textNoItem.setVisibility( View.GONE );
        }
        adapter.notifyDataSetChanged( );
        updateSearchView( );
    }

    @Override
    public void afterTextChanged( Editable s ) {

    }


    @Override
    public void onInsideItemClick( int position ) {
        //Todo DB갱신
    }

    public ArrayList< ETH > getItems( ) {
        return items;
    }

    public class ViewMapper {

        EditText etSearch;
        StretchImageView ImageNotSearch;
        StretchImageView imageSearch;
        CircleImageView btnClear;

        AdvanceRecyclerView listView;

        View textNoItem;

        public ViewMapper( ) {

            etSearch = findViewById( R.id.et_tokenlist_search );
            ImageNotSearch = findViewById( R.id.image_tokenlist_nosearch_icon );
            imageSearch = findViewById( R.id.image_tokenlist_search_icon );
            btnClear = findViewById( R.id.btn_tokenlist_clear );

            listView = findViewById( R.id.listView );

            textNoItem = findViewById( R.id.text_tokenlist_noitem );
        }
    }

}
