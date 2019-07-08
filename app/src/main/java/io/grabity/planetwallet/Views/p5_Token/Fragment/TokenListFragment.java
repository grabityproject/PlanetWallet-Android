package io.grabity.planetwallet.Views.p5_Token.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;

import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.store.ERC20Store;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.Views.p5_Token.Activity.TokenAddActivity;
import io.grabity.planetwallet.Views.p5_Token.Adapter.TokenAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.OnInsideItemClickListener;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class TokenListFragment extends PlanetWalletFragment implements View.OnClickListener, TextWatcher, AdvanceRecyclerView.OnItemClickListener, OnInsideItemClickListener< ERC20 > {

    private ViewMapper viewMapper;
    private TokenAdapter adapter;
    private ArrayList< ERC20 > items;
    private ArrayList< ERC20 > filterItems;
    private Planet planet;

    public TokenListFragment( ) {

    }

    public static TokenListFragment newInstance( ) {
        return new TokenListFragment( );
    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        planet = ( ( TokenAddActivity ) getPlanetWalletActivity( ) ).getPlanet( );
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
        new Get( this ).action( Route.URL( "erc20" ), 0, 0, null );
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );
        if ( resultCode == 0 && statusCode == 200 ) {
            ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, ERC20.class );
            if ( returnVO.isSuccess( ) ) {
                items = new ArrayList<>( );
                ArrayList< ERC20 > erc20list = ( ArrayList< ERC20 > ) returnVO.getResult( );
                ArrayList< ERC20 > currentUseId = ERC20Store.getInstance( ).getTokenList( planet.getKeyId( ) );
                HashMap< String, ERC20 > currentMap = new HashMap<>( );

                for ( ERC20 erc20 : currentUseId ) {
                    currentMap.put( erc20.getContract( ), erc20 );
                }

                for ( int i = 0; i < erc20list.size( ); i++ ) {
                    if ( currentMap.containsKey( erc20list.get( i ).getContract( ) ) ) {
                        if ( currentMap.get( erc20list.get( i ).getContract( ) ).getHide( ).equals( "N" ) )
                            erc20list.get( i ).setCheck( true );
                    }
                    items.add( erc20list.get( i ) );
                }

                filterItems = new ArrayList<>( items );

                adapter = new TokenAdapter( getContext( ), filterItems );
                adapter.setOnInsideItemClickListener( this );

                viewMapper.listView.setAdapter( adapter );
                viewMapper.listView.setOnItemClickListener( this );
            }
        }
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
        viewMapper.imageNotSearch.setVisibility( viewMapper.etSearch.getText( ).length( ) == 0 ? View.VISIBLE : View.INVISIBLE );
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
    public void onInsideItemClick( ERC20 item, int position ) {
        try {
            ERC20 erc20 = new ERC20( );
            erc20.setKeyId( planet.getKeyId( ) );
            erc20.setContract( item.getContract( ) );
            erc20.setSymbol( item.getSymbol( ) );
            erc20.setDecimal( item.getDecimal( ) );
            erc20.setName( item.getName( ) );
            erc20.setImg_path( item.getImg_path( ) );
            erc20.setHide( item.isCheck( ) ? "N" : "Y" );
            erc20.setBalance( item.getBalance( ) );
            PLog.e( "erc20 : " + erc20 );
            PLog.e( "erc20.getBlance : " + erc20.getBalance( ) );
            ERC20Store.getInstance( ).save( erc20 );

        } catch ( ClassCastException e ) {

        }
    }

    public class ViewMapper {

        EditText etSearch;
        StretchImageView imageNotSearch;
        StretchImageView imageSearch;
        CircleImageView btnClear;

        AdvanceRecyclerView listView;

        View textNoItem;

        public ViewMapper( ) {

            etSearch = findViewById( R.id.et_tokenlist_search );
            imageNotSearch = findViewById( R.id.image_tokenlist_nosearch_icon );
            imageSearch = findViewById( R.id.image_tokenlist_search_icon );
            btnClear = findViewById( R.id.btn_tokenlist_clear );

            listView = findViewById( R.id.listView );

            textNoItem = findViewById( R.id.text_tokenlist_noitem );
        }
    }

}
