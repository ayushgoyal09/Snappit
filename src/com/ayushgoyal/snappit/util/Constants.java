package com.ayushgoyal.snappit.util;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;

import com.ayushgoyal.snappit.beans.AlbumBean;
import com.ayushgoyal.snappit.beans.UserBean;

public class Constants {

	public static ProgressDialog pDialog = null;
	public static UserBean currentUser = null;
	public static String CURRENT_ALBUM = null;
	public static String RENAME_ITEM = null;
	public static final String APP_NAME = "Snappit";
	public static final String URL = "http://www.ayushgoyal09.com/webservice/upload_image.php";
	public static final String URL_get_imagesList = "http://www.ayushgoyal09.com/webservice/get_all_images.php";
	public static final String UPLOADS_FOLDER = "http://www.ayushgoyal09.com/webservice/uploadss/";
	public static final String TAG_SUCCESS = "success";
	public static final String TAG_IMAGES = "images";
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

	public static final String GET_ALBUMS_URL = "http://www.ayushgoyal09.com/webservice/get_all_albums.php";
	public static final String ADD_ALBUM_URL = "http://www.ayushgoyal09.com/webservice/add_album.php";
	public static final String DELETE_ALBUM_URL = "http://www.ayushgoyal09.com/webservice/delete_album.php";
	public static final String RENAME_ALBUM_URL = "http://www.ayushgoyal09.com/webservice/rename_dir.php";
	public static final String TAG_RESULT = "result";
	
	public static final String CHANGE_PASSWORD_URL = "http://www.ayushgoyal09.com/webservice/change_password.php";
	public static final String DELETE_ACCOUNT_URL = "http://www.ayushgoyal09.com/webservice/delete_account.php";
	public static List<AlbumBean> ALBUM_LIST = new ArrayList<AlbumBean>();	


}
