package io.grabity.planetwallet.Views.p4_Main.Components;

import android.view.View;
import android.widget.TextView;

import java.util.Objects;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.Common.components.ViewComponent;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p6_Transfer.Activity.TransferActivity;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.BarcodeView;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.SlideDrawerLayout;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class BottomLauncherComponent extends ViewComponent implements SlideDrawerLayout.OnSlideDrawerListener, BottomPanelComponent.LauncherChangeListener {

    private ViewMapper viewMapper;
    private BottomPanelComponent bottomPanelComponent;

    private SlideDrawerLayout slideDrawerLayout;

    private float dp20;
    private Planet planet;
    private ERC20 erc20;

    public BottomLauncherComponent( PlanetWalletActivity activity, SlideDrawerLayout slideDrawerLayout ) {
        super( activity );

        bottomPanelComponent = new BottomPanelComponent( getActivity( ) );
        bottomPanelComponent.setBottomNextClickListener( this );
        dp20 = Utils.dpToPx( getActivity( ), 20 );

        this.viewMapper = new ViewMapper( );
        this.slideDrawerLayout = slideDrawerLayout;

        slideDrawerLayout.addOnSlideDrawerListener( this );
        slideDrawerLayout.addBypassArea( viewMapper.bypassView );

        viewInit( );
    }

    @Override
    public void viewInit( ) {
        super.viewInit( );
        setTrigger( viewMapper.viewTrigger );
        viewMapper.btnCopy.setOnClickListener( this );
        viewMapper.btnTransfer.setOnClickListener( this );
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnCopy ) {

            Utils.copyToClipboard( getActivity( ), planet.getAddress( ) );
            CustomToast.makeText( getActivity( ), getActivity( ).localized( R.string.main_copy_to_clipboard ) ).show( );

        } else if ( v == viewMapper.btnTransfer ) {
            slideDrawerLayout.close( );
            if ( bottomPanelComponent.tokenIndex == 0 ) {
                Utils.postDelayed( ( ) -> getActivity( ).setTransition( PlanetWalletActivity.Transition.SLIDE_SIDE ).sendAction( TransferActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) ), 250 );
            } else {
                Utils.postDelayed( ( ) -> getActivity( ).setTransition( PlanetWalletActivity.Transition.SLIDE_SIDE ).sendAction( TransferActivity.class, Utils.mergeBundles( Utils.createSerializableBundle( C.bundleKey.PLANET, planet ), Utils.createSerializableBundle( C.bundleKey.MAIN_ITEM, erc20 ) ) ), 250 );
            }


        }
    }

    public void setPlanet( Planet planet ) {
        this.planet = planet;
        bottomPanelComponent.setPlanet( planet );
        viewMapper.barcodeView.setData( planet.getAddress( ) );
//        viewMapper.textCoinName.setText( CoinType.of( planet.getCoinType( ) ).getCoinName( ) );
        viewMapper.textPlanetName.setText( planet.getName( ) );
        viewMapper.textAddress.setText( planet.getAddress( ) );
    }

    private void setTrigger( View trigger ) {
        Objects.requireNonNull( slideDrawerLayout ).setTrigger( SlideDrawerLayout.Position.BOTTOM, trigger, false );
        Objects.requireNonNull( slideDrawerLayout ).getTrigger( SlideDrawerLayout.Position.BOTTOM ).setOffset( -dp20 * 5 );
    }

    public void updateBlurView( boolean theme ) {
        if ( bottomPanelComponent != null ) {
            bottomPanelComponent.updateBlurView( theme );
        }
    }


    @Override
    public void isChange( String coinName, MainItem item ) {
        if ( item == null ) {
            viewMapper.textCoinName.setText( coinName );
        } else {
            if ( Utils.equals( item.getClass( ), ERC20.class ) ) {
                erc20 = ( ERC20 ) item;
            }
            viewMapper.textCoinName.setText( coinName );
        }
    }

    @Override
    public void onSlide( int position, float percent, float x, float y ) {

        if ( position == SlideDrawerLayout.Position.BOTTOM ) {
            float blurTop = slideDrawerLayout.getHeight( ) - viewMapper.groupBottomPanel.getHeight( ) / 2.0f;
            float movePoint = ( ( ( slideDrawerLayout.getHeight( ) - viewMapper.groupBottomPanel.getHeight( ) / 2.0f - dp20 ) - y ) / ( ( float ) viewMapper.groupBottomPanel.getHeight( ) / 2.0f ) );

            viewMapper.groupBottom.setClickable( movePoint != 0 );

            viewMapper.textNotice.setAlpha( 1.0f - movePoint );
            viewMapper.groupBottomPanel.setAlpha( 1.0f - movePoint );

            viewMapper.groupBottom.setAlpha( movePoint * 1.2f );

            if ( ( y - ( slideDrawerLayout.getHeight( ) - viewMapper.groupBottomPanel.getHeight( ) / 2.0f ) + dp20 ) > 0 ) {

                viewMapper.textNotice.setY( slideDrawerLayout.getHeight( ) - viewMapper.groupBottomPanel.getHeight( ) / 2.0f - viewMapper.textNotice.getHeight( ) );
                viewMapper.groupBottomPanel.setY( blurTop );

            } else {

                viewMapper.textNotice.setY( y - viewMapper.textNotice.getHeight( ) + dp20 );
                viewMapper.groupBottomPanel.setY( y + dp20 );

//                bottomPanelComponent.getBlurImageView( ).setY(
//                        ( ( View ) bottomPanelComponent.getBlurImageView( ).getParent( ) ).getHeight( ) -
//                                viewMapper.listMain.getHeight( ) -
//                                ( scrollY > 0 ? scrollY : 0 ) - viewMapper.groupBottomPanel.getHeight( ) / 2.0f +
//                                ( ( slideDrawerLayout.getHeight( ) - viewMapper.groupBottomPanel.getHeight( ) / 2.0f ) - y ) );

            }

        }

    }


    class ViewMapper {

        View viewTrigger;
        View groupBottom;

        View btnCopy;
        View btnTransfer;
        BarcodeView barcodeView;

        TextView textCoinName;
        TextView textPlanetName;
        TextView textAddress;


        // View Control items
        TextView textNotice;
        AdvanceRecyclerView listMain;
        View bypassView;
        View groupBottomPanel;
        StretchImageView imageBlur;

        ViewMapper( ) {
            viewTrigger = findViewById( R.id.view_bottom_panel_trigger );
            groupBottom = findViewById( R.id.group_main_bottom );

            btnCopy = findViewById( R.id.btn_main_bottom_copy );
            btnTransfer = findViewById( R.id.btn_main_bottom_transfer );
            barcodeView = findViewById( R.id.barcode_main_bottom_barcodeview );

            textNotice = findViewById( R.id.text_main_notice );
            listMain = findViewById( R.id.list_main );
            bypassView = findViewById( R.id.btn_bottom_panel_next );
            groupBottomPanel = findViewById( R.id.group_bottom_panel );
            imageBlur = findViewById( R.id.image_main_blur );

            textCoinName = findViewById( R.id.text_main_bottom_coin_name );
            textPlanetName = findViewById( R.id.text_main_bottom_name );
            textAddress = findViewById( R.id.text_main_bottom_address );
        }
    }

}
