package com.simbiosys.chapooapp;

public class Constants {
	final static int ACTION_LOGIN = 1;
	final static int ACTION_GET_PROJECTS = 2;
	
	final static String URL_DOMAIN = "https://my.chapoo.com/";
	final static String URL_REST_BASE = URL_DOMAIN + "rest/";
	final static String URL_PROJECTS = URL_REST_BASE + "Projects";
	final static String URL_LOGIN = URL_REST_BASE + "openid/Authenticate";
	final static String URL_LOGOFF = URL_REST_BASE + "Logoff";
	final static String URL_SIGNUP = URL_DOMAIN + "openid/SubscribeUser";
	final static String URL_FORGET_PASSWORD = URL_DOMAIN + "openid/ForgotPassword";
	final static String URL_CHAPOO_ACCOUNT = URL_REST_BASE +"ChapooAccount";
	
	public class File {
		
	}
	
	public class ObjectType{
		final static int OBJECT_TYPE_FOLDER = 1;
		final static int OBJECT_TYPE_DOCUMENT = 2;
		final static String TAG_OBJECT_TYPE = "objectType";
	}
	
	public class Project{
		final static String JSON_TAG_OBJECT_TYPE = "ObjectType";
		final static String JSON_TAG_ID = "ID";
		final static String JSON_TAG_NAME= "Name";
		final static String JSON_TAG_ADDRESS= "Address";
		final static String JSON_TAG_CITY = "City";
		final static String JSON_TAG_ZIP = "Zip";
		final static String JSON_TAG_COUNTRY_ID = "CountryID";
		final static String JSON_TAG_DETAILS = "Details";
		final static String JSON_TAG_TIMEZONE = "Timezone";
		final static String JSON_TAG_ADMINROLE_ID = "AdminRoleID";
		final static String JSON_TAG_CREATED_BY_ID = "CreatedByID";
		final static String JSON_TAG_IS_LOCKED = "IsLocked";
		final static String JSON_TAG_IS_ACTIVE = "IsActive";
		final static String JSON_TAG_IS_FREE = "IsFree";
		final static String JSON_TAG_START_DATE = "StartDate";
		final static String JSON_TAG_END_DATE = "EndDate";
	}
	
	
	public class Folder{
		final static String JSON_TAG_FOLDER_OBJECT_TYPE = "ObjectType";
		final static String JSON_TAG_FOLDER_ID = "ID";
		final static String JSON_TAG_FOLDER_NAME = "Name";
		final static String JSON_TAG_FOLDER_PATH = "Path";
		final static String JSON_TAG_FOLDER_TYPE = "Type";
		final static String JSON_TAG_FOLDER_PROJECT_ID = "ProjectID";
		final static String TAG_PARENT_FOLDER_ID = "parentID";
		
		public class Type {
			final static String FOLDER_TYPE_DOCUMENTS = "Documents";
		}
	}
	
	public class Document{
		final static String JSON_TAG_DOCUMENT_OBJECT_TYPE = "ObjectType";
		final static String JSON_TAG_DOCUMENT_ID = "ID";
		final static String JSON_TAG_DOCUMENT_NAME= "Name";
		final static String JSON_TAG_DOCUMENT_NR_REVISIONS= "NrRevisions";
		final static String JSON_TAG_DOCUMENT_LAST_REVISION = "LastRevision";
		final static String JSON_TAG_DOCUMENT_FOLDER_ID= "FolderID";
		final static String JSON_TAG_DOCUMENT_DESCRIPTION= "Description";
		final static String JSON_TAG_DOCUMENT_AUTHOR_ID = "AuthorID";
		final static String JSON_TAG_DOCUMENT_UPLOADED = "Uploaded";
		final static String JSON_TAG_DOCUMENT_REVISION_NR= "RevisionNr";
		final static String JSON_TAG_DOCUMENT_REVISION_NAME = "RevisionName";
		final static String JSON_TAG_DOCUMENT_REVISION_ID= "RevisionID";
		final static String JSON_TAG_DOCUMENT_SIZE = "Size";
		final static String JSON_TAG_DOCUMENT_STATUS_ID= "StatusID";
		final static String JSON_TAG_DOCUMENT_CONVERSION_STATUS = "ConversionStatus";
		final static String JSON_TAG_DOCUMENT_NR_VIEWS= "NrViews";
	}
	
	public class Action {
		final static int ACTION_DOCUMENT_DOWNLOAD = 1;
		final static int ACTION_DOCUMENT_DELETE = 2;
		final static int ACTION_DOCUMENT_RENAME = 3;
		final static int ACTION_DOCUMENT_UPLOAD = 9;
		
		final static int ACTION_FOLDER_GET_CHILD_FOLDERS = 4;
		final static int ACTION_FOLDER_CREATE_NEW = 5;
		final static int ACTION_FOLDER_DELETE = 6;
		final static int ACTION_FOLDER_RENAME = 7;
		final static int ACTION_FOLDER_GET_THE_FOLDER = 8;
	}
	
	public class Edit{
		final static String EDIT_DELETE = "Delete";
		final static String EDIT_RENAME = "Rename";
	}
	
	public class Network{
		final static int TIMEOUT_MILLISEC = 5000;
	}
	
	public static String getFolderURL(String prjectID){
		return URL_REST_BASE + "Folders?p=" + prjectID;
	}
	
	public static String getFolderURL(int ActionCode, String folderID) {
		switch(ActionCode) {
			case Action.ACTION_FOLDER_GET_CHILD_FOLDERS:
				return URL_REST_BASE + "Folders?parent=" + folderID;
			case Action.ACTION_FOLDER_GET_THE_FOLDER:
				return URL_REST_BASE + "Folders?f=" + folderID;
			case Action.ACTION_FOLDER_CREATE_NEW:
			case Action.ACTION_FOLDER_RENAME:
				return URL_REST_BASE + "Folders";
			case Action.ACTION_FOLDER_DELETE:
				return URL_REST_BASE + "Folders?f=" + folderID + "&a=hard";
			default:
				return null;
		}
	}
	
	//get list of documents in a folder
	public static String getDocumentURL(String folderID) {
		return URL_REST_BASE + "Documents?f=" + folderID;
	}
	
	public static String getDocumentURL(int ActionCode, String folderID, String documentID) {
		switch(ActionCode) {
			case Action.ACTION_DOCUMENT_DOWNLOAD:
				return URL_DOMAIN + "/protected/Document.download?f=" + folderID + "&r=" + documentID;
			case Action.ACTION_DOCUMENT_DELETE:
				return URL_REST_BASE + "Documents?r=" + documentID + "&f=" + folderID;
			case Action.ACTION_DOCUMENT_RENAME:
				return URL_REST_BASE + "Documents";
			default:
				return null;
		}
	}
	
	public static String getLoginURL(String username, String password) {
		return URL_REST_BASE + "Logon?u=" + username + "&p=" + password;
	}
}
