package gsc.profile_gw2mobile.app;

public class AppConfig {
	// Server user login url
	public static String URL_LOGIN = "http://192.168.1.12:1337/profile_gw2Authentication/login.php";

	// Server user register url
	public static String URL_REGISTER = "http://192.168.1.12:1337/profile_gw2Authentication/register.php";

	//public static String URL_API_ACCOUNT = "https://api.guildwars2.com/v2/account?access_token=";
	public static String URL_API_ACCOUNT = "https://api.guildwars2.com/v2/tokeninfo?access_token=";
}
