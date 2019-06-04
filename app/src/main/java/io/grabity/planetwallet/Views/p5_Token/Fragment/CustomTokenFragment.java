package io.grabity.planetwallet.Views.p5_Token.Fragment;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RelativeLayout;

import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
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
        viewMapper.etAddress.addTextChangedListener( this );

        viewMapper.btnSubmit.setEnabled( false );
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
            //애니메이션 테스트

//            PLog.e( "not ani viewHeight : " + viewMapper.addressWaring.getHeight( ) );
//            new ResizeAnimation( ).init( viewMapper.addressWaring, 500, ( int ) Utils.dpToPx( getPlanetWalletActivity( ), 0 ), ( int ) Utils.dpToPx( getPlanetWalletActivity( ), 40 ) ).start( );
//
//            ValueAnimator animator = ValueAnimator.ofInt( ( int ) Utils.dpToPx( getPlanetWalletActivity( ), 0 ), ( int ) Utils.dpToPx( getPlanetWalletActivity( ), 40 ) );
//            animator.addUpdateListener( new ValueAnimator.AnimatorUpdateListener( ) {
//                @Override
//                public void onAnimationUpdate( ValueAnimator animation ) {
//                    int val = ( int ) animation.getAnimatedValue( );
//
//                    //같은기능
//                    viewMapper.addressWaring.getLayoutParams( ).height = val;
//                    viewMapper.addressWaring.requestLayout( );
//
//                    //같은기능
////                    ViewGroup.LayoutParams params = viewMapper.addressWaring.getLayoutParams();
////                    params.height = val;
////                    viewMapper.addressWaring.setLayoutParams( params );
//                }
//            } );
//            animator.setDuration( 500 );
//            animator.start( );

        }
    }

    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {
        if ( viewMapper.etAddress.getText( ) == null || viewMapper.etSymbol.getText( ) == null )
            return;
        viewMapper.btnSubmit.setEnabled( viewMapper.etAddress.getText( ).toString( ).trim( ).length( ) == 0 &&
                viewMapper.etSymbol.getText( ).toString( ).trim( ).length( ) == 0 ? false : true );

        if ( viewMapper.etAddress.getText( ).length( ) >= 1 ) {
            if ( viewMapper.addressWaring.getHeight( ) == 0 ) {
                new ResizeAnimation( ).init( viewMapper.addressWaring, 500, ( int ) Utils.dpToPx( getPlanetWalletActivity( ), 0 ), ( int ) Utils.dpToPx( getPlanetWalletActivity( ), 40 ) ).start( );
            }
        } else {
            new ResizeAnimation( ).init( viewMapper.addressWaring, 500, ( int ) Utils.dpToPx( getPlanetWalletActivity( ), 40 ), ( int ) Utils.dpToPx( getPlanetWalletActivity( ), 0 ) ).start( );
        }

    }


    @Override
    public void afterTextChanged( Editable s ) {

    }


    public class ViewMapper {

        RoundEditText etAddress;
        RoundEditText etSymbol;
        RoundEditText etDecimals;
        RelativeLayout addressWaring;
        View btnSubmit;

        public ViewMapper( ) {
            etAddress = findViewById( R.id.et_custom_token_address );
            etSymbol = findViewById( R.id.et_customToken_symbol );
            etDecimals = findViewById( R.id.edit_custom_token_decimals );
            addressWaring = findViewById( R.id.group_custom_token_waring );
            btnSubmit = findViewById( R.id.btn_submit );
        }
    }

}
