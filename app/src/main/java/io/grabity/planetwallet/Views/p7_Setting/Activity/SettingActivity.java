package io.grabity.planetwallet.Views.p7_Setting.Activity;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.AbsPopupView.PopupView;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Board;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Board.BoardActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Planet.DetailPlanetActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Planet.PlanetManagementActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Setting.SecurityActivity;
import io.grabity.planetwallet.Views.p7_Setting.Adapter.PopupCurrencyAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.ListPopupView.ListPopup;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.RoundButton.RoundButton;
import io.grabity.planetwallet.Widgets.ToolBar;


public class SettingActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, ListPopup.OnListPopupItemClickListener {

    private ViewMapper viewMapper;

    private AdvanceArrayAdapter adapter;
    private ArrayList< String > items;

    private Planet planet;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_setting, true );
        viewMapper = new ViewMapper( );
        viewInit( );
    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        setData( );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null ) {
            finish( );
        } else {
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            planet = PlanetStore.getInstance( ).getPlanet( planet.getKeyId( ) );
//            viewMapper.textName.setText( localized( R.string.setting_planet_main_title, planet.getName( ) ) );
            viewMapper.textName.setText( localized( R.string.setting_planet_main_title, Utils.planetNameForm( planet.getName( ) ) ) );
            viewMapper.planetView.setData( planet.getAddress( ) );
        }

        items = new ArrayList<>( Arrays.asList( "USD", "KRW", "CNY" ) );
        adapter = new PopupCurrencyAdapter( this, items );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );


        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnPlanet.setOnClickListener( this );
        viewMapper.btnUniverse.setOnClickListener( this );
        viewMapper.btnSecurity.setOnClickListener( this );
        viewMapper.btnAnnouncements.setOnClickListener( this );
        viewMapper.btnFaq.setOnClickListener( this );
        viewMapper.btnThemeWhite.setOnClickListener( this );
        viewMapper.btnThemeBlack.setOnClickListener( this );
        viewMapper.btnCurrency.setOnClickListener( this );

        btnThemeSetting( );
    }

    void btnThemeSetting( ) {
        viewMapper.btnThemeBlack.setBorderColorNormal( !getPlanetWalletApplication( ).getCurrentTheme( ) ? Color.parseColor( "#FF0050" ) : Color.parseColor( "#5C5964" ) );
        viewMapper.btnThemeWhite.setBorderColorNormal( !getPlanetWalletApplication( ).getCurrentTheme( ) ? Color.parseColor( "#BCBDD5" ) : Color.parseColor( "#FF0050" ) );
    }


    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnPlanet ) {
            sendAction( DetailPlanetActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
        } else if ( v == viewMapper.btnUniverse ) {
            //Todo 자기자신 제거한 리스트출력
            sendAction( PlanetManagementActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
        } else if ( v == viewMapper.btnSecurity ) {
            sendAction( SecurityActivity.class );
        } else if ( v == viewMapper.btnAnnouncements ) {
//            sendAction( BoardActivity.class, Utils.createSerializableBundle( C.bundleKey.BOARD, new Board( "Announcements" ) ) );
//            viewMapper.textName.setText( localized( R.string.setting_planet_main_title, Utils.planetNameForm( planet.getName( ) ) ) );
            sendAction( BoardActivity.class, Utils.createSerializableBundle( C.bundleKey.BOARD, new Board( localized( R.string.setting_announcements_title ) ) ) );
        } else if ( v == viewMapper.btnFaq ) {
            sendAction( BoardActivity.class, Utils.createSerializableBundle( C.bundleKey.BOARD, new Board( localized( R.string.setting_faq_title ) ) ) );
        } else if ( v == viewMapper.btnThemeBlack ) {

            getPlanetWalletApplication( ).setTheme( false );
            setTheme( false );

            btnThemeSetting( );

        } else if ( v == viewMapper.btnThemeWhite ) {

            getPlanetWalletApplication( ).setTheme( true );
            setTheme( true );

            btnThemeSetting( );

        } else if ( v == viewMapper.btnCurrency ) {

            ListPopup.newInstance( this )
                    .setAdapter( adapter )
                    .setOnListPopupItemClickListener( this )
                    .show( );
        }

    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            super.onBackPressed( );
        }
    }


    @Override
    public void onListPopupItemClick( PopupView popup, View view, int position ) {
        viewMapper.textCurrency.setText( items.get( position ) );
        super.onBackPressed( );
    }

    public class ViewMapper {

        ToolBar toolBar;

        TextView textName;
        TextView textCurrency;

        ViewGroup btnPlanet;
        ViewGroup btnUniverse;
        ViewGroup btnSecurity;
        ViewGroup btnAnnouncements;
        ViewGroup btnFaq;
        ViewGroup btnCurrency;


        RoundButton btnThemeWhite;
        RoundButton btnThemeBlack;

        PlanetView planetView;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            textName = findViewById( R.id.text_setting_name );
            btnPlanet = findViewById( R.id.group_setting_planet );
            btnUniverse = findViewById( R.id.group_setting_universe );
            btnSecurity = findViewById( R.id.group_setting_security );
            btnAnnouncements = findViewById( R.id.group_setting_announcements );
            btnFaq = findViewById( R.id.group_setting_faq );

            btnThemeBlack = findViewById( R.id.btn_setting_theme_black );
            btnThemeWhite = findViewById( R.id.btn_setting_theme_white );

            textCurrency = findViewById( R.id.text_setting_currency );
            btnCurrency = findViewById( R.id.group_setting_currency );

            planetView = findViewById( R.id.planet_setting_planetview );

        }

    }
}