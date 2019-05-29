package io.grabity.planetwallet.Views.p5_Token.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Eth;
import io.grabity.planetwallet.Views.p5_Token.Adapter.TokenAdapter;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.OnInsideItemClickListener;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class TokenListFragmentListener extends PlanetWalletFragment implements View.OnClickListener , TextWatcher , OnInsideItemClickListener, AdvanceRecyclerView.OnItemClickListener {

    private ViewMapper viewMapper;
    private TokenAdapter adapter;
    private ArrayList< Eth > items;
//    private ArrayList< Eth > allItems;

    private ArrayList<Eth> filterItems;

    public TokenListFragmentListener( ) {
    }

    public static TokenListFragmentListener newInstance( ) {
        TokenListFragmentListener fragment = new TokenListFragmentListener( );
        return fragment;
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.fragment_token_list );
        viewMapper = new ViewMapper( );

        viewInit();
        setData();
    }

    @Override
    protected void viewInit ( ) {
        super.viewInit( );
        viewMapper.etSearch.addTextChangedListener( this );
        viewMapper.searchClear.setOnClickListener( this );

    }

    @Override
    public void setData ( ) {
        super.setData( );
        /**
         * 임시작업
         */
        items = new ArrayList<>(  );
        items.add( new Eth( R.drawable.icon_eth, "1" , false) );
        items.add( new Eth( R.drawable.icon_gbt, "2" , false) );
        items.add( new Eth( R.drawable.icon_omg, "3" , false) );
        items.add( new Eth( R.drawable.icon_iota, "4" , false) );
        items.add( new Eth( R.drawable.icon_eth, "5" , false) );
        items.add( new Eth( R.drawable.icon_gbt, "6" , false) );
        items.add( new Eth( R.drawable.icon_omg, "7" , false) );
        items.add( new Eth( R.drawable.icon_iota, "8" , false) );
        items.add( new Eth( R.drawable.icon_eth, "9" , false) );
        items.add( new Eth( R.drawable.icon_gbt, "10" , false) );


        filterItems = new ArrayList<>( items );

        adapter = new TokenAdapter( getContext() , filterItems );
        adapter.setOnInsideItemClickListener( this );
        viewMapper.listview.setAdapter( adapter );
        viewMapper.listview.setOnItemClickListener( this );

    }

    @Override
    public void onClick ( View v ) {
        if( v == viewMapper.searchClear ){
            viewMapper.etSearch.setText( "" );
            setView();
        }
    }

    @Override
    public void onItemClick ( AdvanceRecyclerView recyclerView, View view, int position ) {

    }



    @Override
    public void beforeTextChanged ( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged ( CharSequence s, int start, int before, int count ) {
        filterItems.clear();
        for ( int i = 0; i< items.size(); i++ ){
            if( items.get( i ).getName( ).toLowerCase( ).contains( viewMapper.etSearch.getText( ).toString( ).toLowerCase( ) ) ){
                filterItems.add( items.get( i ) );
            }
        }

        if( filterItems.size() == 0 ){
            PLog.e( "검색결과 없습니다." );
        }
        adapter.notifyDataSetChanged();
        setView();
    }

    @Override
    public void afterTextChanged ( Editable s ) {

    }

    void setView( ){
        viewMapper.notSearchIcon.setVisibility( viewMapper.etSearch.getText().length() == 0 ? View.VISIBLE : View.INVISIBLE );
        viewMapper.searchClear.setVisibility( viewMapper.etSearch.getText().length() == 0 ? View.GONE : View.VISIBLE );
        viewMapper.searchIcon.setVisibility( viewMapper.etSearch.getText().length() >= 1 ? View.VISIBLE : View.INVISIBLE );
    }

    @Override
    public void onInsideItemClick( int position ) {
            //Todo DB갱신
    }


    public ArrayList< Eth > getItems( ) {
        return items;
    }

    public class ViewMapper {

        EditText etSearch;
        StretchImageView notSearchIcon;
        StretchImageView searchIcon;
        CircleImageView searchClear;
        AdvanceRecyclerView listview;


        public ViewMapper ( ) {
            etSearch = findViewById( R.id.et_tokenlist_search );
            notSearchIcon = findViewById( R.id.image_tokenlist_nosearch_icon );
            searchIcon = findViewById( R.id.image_tokenlist_search_icon );
            searchClear = findViewById( R.id.btn_tokenlist_clear );
            listview = findViewById( R.id.listView );
        }
    }

}
