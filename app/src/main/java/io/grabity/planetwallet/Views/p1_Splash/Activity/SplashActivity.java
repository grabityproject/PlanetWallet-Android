package io.grabity.planetwallet.Views.p1_Splash.Activity;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeRegistrationActivity;


public class SplashActivity extends PlanetWalletActivity implements Animator.AnimatorListener {

    private ViewMapper viewMapper;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );

        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        viewMapper.view.addAnimatorListener( this );
        viewMapper.view.setAnimation( "lottie/splash.json" );
        viewMapper.view.setRepeatCount( 0 );
        viewMapper.view.playAnimation( );

    }

    @Override
    protected void setData( ) {
        super.setData( );
    }

    @Override
    protected void onResume( ) {
        super.onResume( );
    }

    public void sendActionSwitch( ) {
        //Todo 쉐어드로 패스워드등록여부 체크 후 PinActivity 분기
        if ( Utils.getPreferenceData( this, C.pref.PASSWORD ).length( ) > 1 ) {
            new Handler( ).postDelayed( ( ) -> {
                sendAction( PinCodeCertificationActivity.class );
                finish( );
            }, 500 );
        } else {
            new Handler( ).postDelayed( ( ) -> {
                sendAction( PinCodeRegistrationActivity.class );
                finish( );
            }, 500 );
        }

    }

    @Override
    public void onAnimationStart( Animator animation ) {
    }

    @Override
    public void onAnimationEnd( Animator animation ) {
        sendActionSwitch( );
    }

    @Override
    public void onAnimationCancel( Animator animation ) {
    }

    @Override
    public void onAnimationRepeat( Animator animation ) {
    }

    public class ViewMapper {

        LottieAnimationView view;

        public ViewMapper( ) {
            view = findViewById( R.id.lottie_splash );
        }

    }


}