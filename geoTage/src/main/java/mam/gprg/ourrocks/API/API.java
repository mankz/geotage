package mam.gprg.ourrocks.API;

public class API {

	public static int STATUS_OK = 200;
	public static int STATUS_BAD_REQUEST = 400;
	public static int STATUS_UNAUTHORIZED = 401;
	public static int STATUS_PAYMENT_REQUIRED = 402;
	public static int STATUS_FORBIDDEN = 403;
	public static int STATUS_NOT_FOUND = 404;
	public static int STATUS_INTERNAL_ERROR = 500;
	public static int STATUS_NOT_IMPLEMENTED = 501;

	public static String DEV_URL = "http://10.0.3.2/geotage/index.php/api/";
	public static String PRODUCTION_URL = "http://geotage.com/index.php/api/";
	public static String BASE_URL = PRODUCTION_URL;
	public static String DEV_IMAGE_BASE_URL = "http://10.0.3.2/geotage/images/";
	public static String PRODUCTION_IMAGE_BASE_URL = "http://geotage.com/images/";
	public static String IMAGE_BASE_URL = BASE_URL.equals(DEV_URL)?DEV_IMAGE_BASE_URL:PRODUCTION_IMAGE_BASE_URL;
	
	public static String CLIENTS_ALLOW = BASE_URL + "clients/allowclient";
	public static String CLIENTS_UPDATE_AVATAR = BASE_URL + "clients/update_avatar";
	public static String CLIENTS_UPLOAD_IMAGE = BASE_URL  + "clients/upload_image";
	public static String CLIENTS_EDIT_IMAGE = BASE_URL +"clients/edit_existing_image";
	public static String CLIENTS_EDIT_NEW_IMAGE = BASE_URL+"clients/edit_add_new_image";
	public static String CLIENTS_CHANGE_PASSWORD = BASE_URL +"clients/change_password";
	public static String CLIENTS_LOGIN = BASE_URL + "clients/login";
	public static String CLIENTS_CHANGE_PRIVACY = BASE_URL + "clients/change_privacy";
	public static String CLIENTS_FORGOT_PASSWORD = BASE_URL + "clients/forgot_password";
	public static String CLIENTS_CHANGE_GCM_ID = BASE_URL + "clients/change_gcm_id";
	public static String CLIENTS_DELETE_GCM_ID = BASE_URL + "clients/delete_gcm_id";
	public static String CLIENTS_NEW_COMMENT_NOTIFICATION = BASE_URL+"clients/send_new_comment_notification";
	public static String ROCK_ADD_POST = BASE_URL + "create/rock";
	public static String ROCK_EDIT_PUT = BASE_URL + "update/rock/";
	public static String ROCK_DELETE = BASE_URL + "delete/rock/";

 	public static String ROCKS_GET = BASE_URL +"list/rocks/";
	public static String ROCKS_GET_BY_USER = BASE_URL +"list/user_rocks/";
	public static String ROCK_DETAIL_GET = BASE_URL + "view/rock/";
	
	public static String CATEGORIES_GET = BASE_URL +"list/rockcategories/";
	public static String TYPES_GET = BASE_URL + "list/rocktypes/";

	public static String ARTICLES = BASE_URL + "list/articles/";
	
	public static String COMMENTS_BY_ROCK = BASE_URL + "list/rockcomments/";
	public static String COMMENTS_ADD_POST = BASE_URL+"create/rockcomment";

	public static String USER_ADD_POST = BASE_URL + "create/user";
	public static String USER_DETAIL_GET = BASE_URL +"view/user/";
	public static String USER_UPDATE_PUT = BASE_URL + "update/user/";
	
	
	
	
	
	
}
