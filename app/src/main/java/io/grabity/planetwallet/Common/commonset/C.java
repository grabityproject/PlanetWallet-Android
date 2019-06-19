package io.grabity.planetwallet.Common.commonset;


public class C {

    public static class url {
    }

    public static class requestCode {
        public final static int PLANET_RENAME = 200;
        public final static int PLANET_ADD = 201;
        public final static int PLANET_MNEMONIC_EXPORT = 202;
        public final static int PLANET_PRIVATEKEY_EXPORT = 203;

        public final static int ACCOUNT_RENAME = 210;
        public final static int ACCOUNT_REMAIL = 211;
        public final static int ACCOUNT_REPHONE_NUMBER = 212;

        public final static int WALLET_CREATE = 220;

        public final static int MAIN_TOKEN_ADD = 230;

        public final static int SETTING_CHANGE_PINCODE = 240;

        public final static int QR_CODE = 250;


    }

    public static class resultCode {

    }

    public static class bundleKey {
        public final static String PLANET = "PLANET";
        public final static String PINCODE = "PINCODE";

        public final static String PLANETADD = "PLANETADD";

        public final static String MNEMONIC = "MNEMONIC";
        public final static String PRIVATEKEY = "PRIVATEKEY";

        public final static String BTC = "BTC";
        public final static String ETH = "ETH";
        public final static String ERC20 = "ERC20";

        public final static String QRCODE = "QRCODE";
    }

    public static class theme {

        public final static String DARK = "DARK";
        public final static String LIGHT = "LIGHT";
    }

    public static class tag {

        public final static int TOOLBAR_BACK = 100;
        public final static int TOOLBAR_CLOSE = 101;
        public final static int TOOLBAR_ADD = 102;
        public final static int TOOLBAR_MENU = 103;
        public final static int TOOLBAR_MUTIUNIVERSE = 104;
        public final static int TOOLBAR_TRANSFER_QRCODE = 105;

    }

    public static class pref {

        public final static String PREF = "PlanetWallet";

        public final static String THEME = "theme";
        public final static String PASSWORD = "PinPassword";
        public final static String WALLET_GENERATE = "WalletGenerate";

    }

    public static class wallet {
        public final static String CREATE = "create";
    }

    public static class db {

    }

    public static class dateFormat {

    }


}
