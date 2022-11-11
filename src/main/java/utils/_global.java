package utils;

public class _global {

    public static final String PREFIX_FILE_CONFIG = "SMLConfig";

    public static final boolean USE_LOCAL = false; // true = use config, false = use provider file config

    public static String _providerCode = "";
    public static String _databaseServer = "192.168.64.47"; //64.47 , 65.19
    public static String _databaseName = "";
    public static String _databaseUserCode = "postgres";
    public static String _databaseUserPassword = "sml";

    public static String FILE_CONFIG(String providerCode) {
        if (USE_LOCAL) {
            return "";
        }
        return PREFIX_FILE_CONFIG + providerCode.toUpperCase() + ".xml";
    }
}
