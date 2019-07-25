package io.grabity.planetwallet.Views.p5_Token.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.MiniFramework.utils.ResizeAnimation;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.RoundEditText;

public class CustomTokenFragment extends PlanetWalletFragment implements View.OnClickListener, TextWatcher {

    private ViewMapper viewMapper;


    public CustomTokenFragment( ) {
    }

    public static CustomTokenFragment newInstance( ) {
        CustomTokenFragment fragment = new CustomTokenFragment( );
        return fragment;
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.fragment_custom_token );

        viewMapper = new ViewMapper( );

        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.btnSubmit.setOnClickListener( this );
        viewMapper.btnClear.setOnClickListener( this );
        viewMapper.etAddress.addTextChangedListener( this );

        viewMapper.btnSubmit.setEnabled( false );

        viewMapper.etAddress.requestFocus( );
    }

    @Override
    public void setData( ) {
        super.setData( );
    }

    @Override
    public void onClick( View v ) {
        if ( v == viewMapper.btnSubmit ) {
            if ( getActivity( ) == null ) return;
            getActivity( ).setResult( Activity.RESULT_OK );
            getActivity( ).onBackPressed( );
        } else if ( v == viewMapper.btnClear ) {
            viewMapper.etAddress.setText( "" );
        }
    }

    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {
        if ( viewMapper.etAddress.getText( ) == null || viewMapper.etSymbol.getText( ) == null )
            return;
        viewMapper.btnSubmit.setEnabled(
                viewMapper.etAddress.getText( ).toString( ).trim( ).length( ) != 0 ||
                        viewMapper.etSymbol.getText( ).toString( ).trim( ).length( ) != 0 );

        viewMapper.btnClear.setVisibility( viewMapper.etAddress.getText( ).length( ) == 0 ? View.GONE : View.VISIBLE );

        if ( viewMapper.etAddress.getText( ).length( ) >= 1 ) {
            if ( viewMapper.addressWaring.getHeight( ) == 0 ) {
                resizeAnimation( 0, 40 );
            }
        } else {
            resizeAnimation( 40, 0 );
        }
    }

    private void resizeAnimation( int startDP, int EndDP ) {
        new ResizeAnimation( ).init( viewMapper.addressWaring, 500, ( int ) Utils.dpToPx( getPlanetWalletActivity( ), startDP ), ( int ) Utils.dpToPx( getPlanetWalletActivity( ), EndDP ) ).start( );
    }

    @Override
    public void afterTextChanged( Editable s ) {

    }

    public class ViewMapper {

        RoundEditText etAddress;
        RoundEditText etSymbol;
        RoundEditText etDecimals;
        RelativeLayout addressWaring;

        View btnClear;
        View btnSubmit;

        public ViewMapper( ) {
            etAddress = findViewById( R.id.et_custom_token_address );
            etSymbol = findViewById( R.id.et_customToken_symbol );
            etDecimals = findViewById( R.id.edit_custom_token_decimals );
            addressWaring = findViewById( R.id.group_custom_token_waring );
            btnClear = findViewById( R.id.btn_custom_token_clear );
            btnSubmit = findViewById( R.id.btn_submit );
        }
    }

}
