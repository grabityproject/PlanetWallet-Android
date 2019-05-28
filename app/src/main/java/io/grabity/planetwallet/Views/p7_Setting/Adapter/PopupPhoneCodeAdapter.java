package io.grabity.planetwallet.Views.p7_Setting.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.PhoneCode;
import io.grabity.planetwallet.Widgets.AdavanceRecyclerView.AdvanceArrayAdapter;

public class PopupPhoneCodeAdapter extends AdvanceArrayAdapter< PhoneCode > {

    public PopupPhoneCodeAdapter( Context context, ArrayList< PhoneCode > objects ) {
        super( context, objects );
    }

    @Override
    public ViewMapper viewMapping( int position ) {
        return new PhoneCodeItem( View.inflate( getContext( ), R.layout.item_popup_phone_code, null ) );
    }

    @Override
    public void bindData( ViewMapper viewMapper, PhoneCode item, int position ) {
        ( ( PhoneCodeItem ) viewMapper ).country.setText( item.getCountry( ) == null ? "Korea" : item.getCountry( ) );
        ( ( PhoneCodeItem ) viewMapper ).countryCode.setText( item.getCountryCode( ) == null ? "+ 82" : item.getCountryCode( ) );
    }


    class PhoneCodeItem extends ViewMapper {

        TextView country;
        TextView countryCode;

        public PhoneCodeItem( View itemView ) {
            super( itemView );

            country = findViewById( R.id.text_item_popup_phone_code_country );
            countryCode = findViewById( R.id.text_item_popup_phone_code_country_code );
        }
    }


}
