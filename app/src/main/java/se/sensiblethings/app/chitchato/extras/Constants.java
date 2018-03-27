package se.sensiblethings.app.chitchato.extras;

public final class Constants {

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME
            + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME
            + ".LOCATION_DATA_EXTRA";
    public static final String TAG = "Contexter";

    public static int PREFFERED_MAX_THREAD_LINES = 100;
    public static int PREFFERED_MAX_THREAD_LINES_PRI = 10;
    public static int PREFFERED_MAX_THREAD_LINES_PUB = 10;
    public static final String TAG_1 = "REGISTER";
    public static final String TAG_2 = "SEARCH";
    public static final String TAG_3 = "GROUP";
    public static final String TAG_4 = "GROUPS";
    public static final String TAG_5 = "BOOTSTRAPS";
    public static final String TAG_6 = "UNREGISTER";

    //MESSAGE CAT ONE --beginss with 1
    public static int PUBLIC = 100;
    public static int PRIVATE = 101;
    public static int PUBLICIMAGEFILE = 102;
    public static int PRIVATEIMAGEFILE = 103;
    public static int PROFILEIMAGEFILE = 104;

    // MESSAGE CAT TWO -- begins with 2
    public static int SEARCH = 200;
    public static int REGISTER = 201;

    //MESSSAGE CAT THREE begins with 3
    public static int SEARCHED = 300;
    public static int REGISTERED = 301;
    public static int PROFILEIMAGEFILED = 302;

    //MESSSAGE CAT FOUR begins with 4
    public static int PASSWORD = 400;
    public static int DEBUG = 401;
    public static int STATUS = 402;

    //MESSAGE CAT FIVE begins with 5
    public static int NODES = 500;
    public static int GROUP = 501;
    public static int GROUPS = 502;

    //MESSAGE CAT SIX begins with 5
    public static int BOOTSTRAPS = 600;

    // MESSAGE CAT -- Special--Adverts for internal use
    public static int ADS = 800;
    public static int IMAGEMETADATA = 801;

    public static String getLuminanceDescription(float val) {
        String description = "Looks like unknown environment : ";
        if (val <= 0.0001) {
            description = "Looks like Moonless overcast night Sky : ";
        } else if (val > 0.0001 && val <= 0.002) {
            description = "Looks like Moonless clear night sky with airglow : ";
        } else if (val <= 0.27 && val >= 1.0) {
            description = "Looks like Full Moon on a clear night Sky : ";

        } else if (val <= 3.4 & val > 1.0) {
            description = "Looks like Civil twilight under clear sky : ";
        } else if (val < 50 && val >= 20) {
            description = "Looks like public areas with dark surroundings : ";
        } else if (val >= 50 && val <= 100) {
            description = "Looks like very dark overcast day : ";
        } else if (val > 100 & val <= 320) {
            description = "Looks like indoor : ";

        } else if (val < 400 && val > 320) {
            description = "Looks like inside the office  or outdoor in a day : ";

        } else if (val >= 400 && val < 1000) {
            description = "Looks like outside in a fullday light or inside very bright room : ";
        } else if (val >= 1000) {
            description = "Looks like outside in a fullday light : ";
        }

        return description;
    }


    public static String getSoundDescription(float val) {
        String description = "Looks like unknown environment : ";
        if (val <= 0) {
            description = "Looks like abnormally quiet place : ";
        } else if (val <= 10 && val > 0) {
            description = "Looks like around rustling leaves in a distance : ";
        } else if (val <= 20 && val > 10) {
            description = "Looks like quite office : ";

        } else if (val <= 30 & val > 20) {
            description = "Looks like quiet bedroom at night : ";
        } else if (val <= 40 && val > 30) {
            description = "Looks like quiet library : ";
        } else if (val > 40 && val <= 50) {
            description = "Looks like inside home : ";
        } else if (val > 50 & val <= 60) {
            description = "Looks like there is talking or a kind go low noise : ";
        } else if (val < 90 && val > 60) {
            description = "Looks like outdoor by the side of a busy traffic road : ";
        } else if (val >= 90 && val < 120) {
            description = "Looks like very noisy may be Disco : ";
        } else if (val >= 120) {
            description = "Looks like a place where big machines operate : ";
        }

        return description;
    }

    public static String getLocationDescription(float val) {
        String description = "Looks like unknown environment : ";

        return description;
    }
    public static String getAccDescription(float val) {
        String description = "Looks like unknown : ";

        return description;
    }

}
