package io.grabity.planetwallet.Views.p4_Main.Components;

import android.graphics.Color;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Objects;

import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.Common.components.ViewComponent;
import io.grabity.planetwallet.MiniFramework.utils.BlurBuilder;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.StretchImageView;

public class BottomPanelComponent extends ViewComponent implements AdvanceRecyclerView.OnScrollListener {

    private ViewMapper viewMapper;
    private Planet planet;
    private MainItem mainItem;
    private OnMainItemChangeListener onMainItemChangeListener;

    public int tokenIndex = 0;

    public BottomPanelComponent( PlanetWalletActivity activity ) {
        super( activity );
        viewMapper = new ViewMapper( );
        viewInit( );
    }

    @Override
    public void viewInit( ) {
        super.viewInit( );
        viewMapper.btnNext.setOnClickListener( this );
        viewMapper.groupBottomPanel.getViewTreeObserver( ).addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener( ) {
            @Override
            public void onGlobalLayout( ) {
                viewMapper.groupBottomPanel.getViewTreeObserver( ).removeOnGlobalLayoutListener( this );
                viewMapper.groupBottomPanel.setY( viewMapper.groupBottomPanel.getY( ) + viewMapper.groupBottomPanel.getHeight( ) );
                viewMapper.groupBottomPanel.getLayoutParams( ).height = viewMapper.groupBottomPanel.getLayoutParams( ).height * 2;
                viewMapper.groupBottomPanel.requestLayout( );
            }
        } );

        viewMapper.listView.getViewTreeObserver( ).addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener( ) {
            @Override
            public void onGlobalLayout( ) {
                viewMapper.listView.getViewTreeObserver( ).removeOnGlobalLayoutListener( this );
                updateBlurView( getActivity( ).getCurrentTheme( ) );
                viewMapper.imageBlurView.setY( ( ( View ) viewMapper.imageBlurView.getParent( ) ).getHeight( ) - viewMapper.listView.getHeight( ) );

                viewMapper.textNotice.setY( viewMapper.groupBottomPanel.getY( ) + viewMapper.groupBottomPanel.getHeight( ) - viewMapper.textNotice.getHeight( ) );
            }
        } );
        viewMapper.listView.addOnScrollListener( this );
    }

    public void setPlanet( Planet planet ) {
        if ( planet != null ) {

            if ( Utils.equals( CoinType.of( planet.getCoinType( ) ), CoinType.BTC ) ) { // BTC Panel Data mapping

                if ( planet.getMainItem( ) != null ) {
                    setMainItem( planet.getMainItem( ) );
                }

            } else if ( Utils.equals( CoinType.of( planet.getCoinType( ) ), CoinType.ETH ) ) { // ETH Panel Data mapping

                if ( this.planet == null ) { // preset Planet is null

                    this.planet = planet;
                    setMainItem( planet.getMainItem( ) );

                } else {

                    if ( !Utils.equals( this.planet.getKeyId( ), planet.getKeyId( ) ) ) { // ETH Planet changed
                        tokenIndex = 0;
                    }

                    try {
                        setMainItem( planet.getItems( ).get( tokenIndex ) );
                    } catch ( IndexOutOfBoundsException | NullPointerException e ) {
                        setMainItem( planet.getMainItem( ) );
                    }
                }
            }
        }

        this.planet = planet;
    }

    private void setMainItem( MainItem mainItem ) {
        this.mainItem = mainItem;

        if ( mainItem != null ) {

            Objects.requireNonNull( onMainItemChangeListener ).onMainItemChange( mainItem );

            if ( Utils.equals( CoinType.BTC.getCoinType( ), mainItem.getCoinType( ) ) ) {

                viewMapper.imageIcon.setImageResource( R.drawable.icon_btc );

                viewMapper.textTokenType.setVisibility( View.GONE );
                viewMapper.textBalance.setText( Utils.balanceReduction( Utils.toMaxUnit( mainItem, mainItem.getBalance( ) ) ) );
                viewMapper.textName.setText( CoinType.BTC.getCoinName( ) );
                viewMapper.textUnit.setText( CoinType.BTC.name( ) );

                viewMapper.btnNext.setVisibility( View.GONE );

            } else if ( Utils.equals( CoinType.ETH.getCoinType( ), mainItem.getCoinType( ) ) ) {

                viewMapper.imageIcon.setImageResource( R.drawable.icon_eth );

                viewMapper.textTokenType.setVisibility( View.GONE );
                viewMapper.textBalance.setText( Utils.balanceReduction( Utils.toMaxUnit( mainItem, mainItem.getBalance( ) ) ) );
                viewMapper.textName.setText( CoinType.ETH.getCoinName( ) );
                viewMapper.textUnit.setText( CoinType.ETH.name( ) );

                viewMapper.btnNext.setVisibility( View.VISIBLE );

                tokenIndex = 0;

            } else if ( Utils.equals( CoinType.ERC20.getCoinType( ), mainItem.getCoinType( ) ) ) {

                ImageLoader.getInstance( ).displayImage( Route.URL( mainItem.getImg_path( ) ), viewMapper.imageIcon );

                viewMapper.textTokenType.setVisibility( View.VISIBLE );
                viewMapper.textTokenType.setText( CoinType.of( mainItem.getCoinType( ) ).name( ) );
                viewMapper.textBalance.setText( Utils.balanceReduction( Utils.toMaxUnit( mainItem, mainItem.getBalance( ) ) ) );
                viewMapper.textName.setText( mainItem.getName( ) );
                viewMapper.textUnit.setText( mainItem.getSymbol( ) );

                viewMapper.btnNext.setVisibility( View.VISIBLE );

            }

        }

    }


    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnNext ) {
            if ( planet != null ) {
                if ( Utils.equals( CoinType.of( planet.getCoinType( ) ), CoinType.ETH ) ) {
                    if ( mainItem != null && planet.getItems( ) != null ) {
                        switchMainItemForETH( );
                    }

                }
            }
        }
    }

    private void switchMainItemForETH( ) {

        boolean changed = false;

        tokenIndex++;
        if ( tokenIndex >= planet.getItems( ).size( ) ) {
            tokenIndex = 0;
        }

        for ( int i = tokenIndex; i < planet.getItems( ).size( ); i++ ) {

            if ( Utils.equals( CoinType.of( planet.getItems( ).get( i ).getCoinType( ) ), CoinType.ETH ) ) {
                setMainItem( planet.getItems( ).get( i ) );
                changed = true;
                break;

            } else if ( Utils.equals( CoinType.of( planet.getItems( ).get( i ).getCoinType( ) ), CoinType.ERC20 ) ) {

                tokenIndex = i;
                if ( !( planet.getItems( ).get( i ) ).getBalance( ).equals( "0" ) ) {
                    setMainItem( planet.getItems( ).get( i ) );
                    changed = true;
                    break;
                }
            }

        }

        if ( changed ) {
            return;
        }
        switchMainItemForETH( );

    }


    @Override
    public void onScrolled( RecyclerView recyclerView, int dx, int dy, float scrollX, float scrollY ) {
        viewMapper.imageBlurView.setY(
                ( ( View ) viewMapper.imageBlurView.getParent( ) ).getHeight( ) -
                        viewMapper.listView.getHeight( ) - ( scrollY > 0 ? scrollY : 0 ) - viewMapper.groupBottomPanel.getHeight( ) / 2.0f );
    }


    public void updateBlurView( boolean theme ) {
        if ( viewMapper.listView.getAdapter( ) != null ) {
            try {
                viewMapper.imageBlurView.setImageBitmap( BlurBuilder.blur( getActivity( ), viewMapper.listView.getScreenshot( Color.parseColor( theme ? "#FFFFFF" : "#111117" ) ), 0.25f, 25 ) );
            } catch ( IllegalArgumentException e ) {
                PLog.e( "image width or height is 0" );
            }
        }
    }


    public MainItem getMainItem( ) {
        return mainItem;
    }

    public OnMainItemChangeListener getOnMainItemChangeListener( ) {
        return onMainItemChangeListener;
    }

    public void setOnMainItemChangeListener( OnMainItemChangeListener onMainItemChangeListener ) {
        this.onMainItemChangeListener = onMainItemChangeListener;
    }

    class ViewMapper {

        View groupBottomPanel;

        StretchImageView imageBlurView;

        CircleImageView imageIcon;
        TextView textName;
        TextView textTokenType;
        TextView textBalance;
        TextView textUnit;
        TextView textNotice;
        View btnNext;

        AdvanceRecyclerView listView;

        public ViewMapper( ) {

            groupBottomPanel = findViewById( R.id.group_bottom_panel );

            imageBlurView = findViewById( R.id.image_main_blur );

            imageIcon = findViewById( R.id.image_bottom_panel_icon );
            textName = findViewById( R.id.text_bottom_panel_name );
            textTokenType = findViewById( R.id.text_bottom_panel_token_type );
            textBalance = findViewById( R.id.text_bottom_panel_balance );
            textUnit = findViewById( R.id.text_bottom_panel_unit );
            textNotice = findViewById( R.id.text_main_notice );
            btnNext = findViewById( R.id.btn_bottom_panel_next );

            listView = findViewById( R.id.list_main );

        }

    }

    public interface OnMainItemChangeListener {
        void onMainItemChange( MainItem mainItem );
    }

}
