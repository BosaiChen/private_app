package com.simbiosys.chapooapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import android.util.Log;

public class SingletonHttpClient {
	private static DefaultHttpClient httpClient;
	private static SingletonHttpClient _instance;
	private static HttpContext localContext;
	private static CookieStore cookieStore;
	private static Cookie CookieJSESSIONID;
	
	public SingletonHttpClient() {
		if(httpClient == null) {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, Constants.Network.TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, Constants.Network.TIMEOUT_MILLISEC);
			httpClient = new DefaultHttpClient(httpParams);
		}
		if(localContext == null) {
			localContext = new BasicHttpContext();
		}
		if(cookieStore == null) {
			cookieStore = new BasicCookieStore();
		}
	}
	
	public static SingletonHttpClient getInstance() {
		if(_instance == null ) {
			_instance = new SingletonHttpClient();
		}
		return _instance;
	}
	
	
	
	public HttpResponse executeGet(String url) {
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		HttpGet httpGet = new HttpGet(url);
		//set cookie
		//cookie format : "Cookie", "name1=value1; name2=value2"
		if(CookieJSESSIONID != null) {
			httpGet.addHeader("Cookie", CookieJSESSIONID.getName() + "=" + CookieJSESSIONID.getValue());
		}
		try {
			
			HttpResponse response = httpClient.execute(httpGet, localContext);
			//get the cookie if it is null
			if(CookieJSESSIONID == null) {
				Log.v("cookie", "Cookie1:= " + localContext.getAttribute(ClientContext.COOKIE_STORE).toString());
				List<Cookie> listCookie =  cookieStore.getCookies();
				for(int i=0; i<listCookie.size(); i++) {
					if(listCookie.get(i).getName().equals("JSESSIONID")) {
						CookieJSESSIONID = listCookie.get(i);
					}
				}
				Log.v("jsessionid", CookieJSESSIONID.getValue());
			}
			
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public HttpResponse executePost(String url, List<NameValuePair> postData) {
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		HttpPost httpPost = new HttpPost(url);
		//set cookie
		//cookie format : "Cookie", "name1=value1; name2=value2"
		if(CookieJSESSIONID != null) {
			httpPost.addHeader("Cookie", CookieJSESSIONID.getName() + "=" + CookieJSESSIONID.getValue());
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(postData));
			//get the cookie if it is null
			if(CookieJSESSIONID == null) {
				Log.v("cookie", "Cookie1:= " + localContext.getAttribute(ClientContext.COOKIE_STORE).toString());
				List<Cookie> listCookie =  cookieStore.getCookies();
				for(int i=0; i<listCookie.size(); i++) {
					if(listCookie.get(i).getName().equals("JSESSIONID")) {
						CookieJSESSIONID = listCookie.get(i);
					}
				}
			//	Log.v("jsessionid", CookieJSESSIONID.getValue());
			}
			return httpClient.execute(httpPost, localContext);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public HttpResponse executePost(String url, JSONObject json) {
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		HttpPost httpPost = new HttpPost(url);
		//set cookie
		//cookie format : "Cookie", "name1=value1; name2=value2"
		if(CookieJSESSIONID != null) {
			httpPost.addHeader("Cookie", CookieJSESSIONID.getName() + "=" + CookieJSESSIONID.getValue());
		}
		try {
			StringEntity se = new StringEntity( json.toString());  
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			httpPost.setEntity(se);
			//get the cookie if it is null
			if(CookieJSESSIONID == null) {
				Log.v("cookie", "Cookie1:= " + localContext.getAttribute(ClientContext.COOKIE_STORE).toString());
				List<Cookie> listCookie =  cookieStore.getCookies();
				for(int i=0; i<listCookie.size(); i++) {
					if(listCookie.get(i).getName().equals("JSESSIONID")) {
						CookieJSESSIONID = listCookie.get(i);
					}
				}
				Log.v("jsessionid", CookieJSESSIONID.getValue());
			}
			
			return httpClient.execute(httpPost, localContext);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public HttpResponse executeDelete(String url) {
		HttpDelete httpDelete = new HttpDelete(url); 
		if(CookieJSESSIONID != null) {
			httpDelete.addHeader("Cookie", CookieJSESSIONID.getName() + "=" + CookieJSESSIONID.getValue());
		}
		try { 
			if(CookieJSESSIONID == null) {
				Log.v("cookie", "Cookie1:= " + localContext.getAttribute(ClientContext.COOKIE_STORE).toString());
				List<Cookie> listCookie =  cookieStore.getCookies();
				for(int i=0; i<listCookie.size(); i++) {
					if(listCookie.get(i).getName().equals("JSESSIONID")) {
						CookieJSESSIONID = listCookie.get(i);
					}
				}
				Log.v("jsessionid", CookieJSESSIONID.getValue());
			}
			
			return httpClient.execute(httpDelete, localContext);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public HttpResponse executePut(String url, JSONObject json) {
		HttpPut httpPut = new HttpPut(url);
		if(CookieJSESSIONID != null) {
			httpPut.addHeader("Cookie", CookieJSESSIONID.getName() + "=" + CookieJSESSIONID.getValue());
		}
		try { 
			StringEntity se = new StringEntity( json.toString());  
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPut.setEntity(se);
			if(CookieJSESSIONID == null) {
				Log.v("cookie", "Cookie1:= " + localContext.getAttribute(ClientContext.COOKIE_STORE).toString());
				List<Cookie> listCookie =  cookieStore.getCookies();
				for(int i=0; i<listCookie.size(); i++) {
					if(listCookie.get(i).getName().equals("JSESSIONID")) {
						CookieJSESSIONID = listCookie.get(i);
					}
				}
				Log.v("jsessionid", CookieJSESSIONID.getValue());
			}
			
			return httpClient.execute(httpPut, localContext);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getResponseText(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		if(entity != null) {
			InputStream in;
			try {
				in = entity.getContent();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
				StringBuffer out = new StringBuffer();
				String line = null;
				while((line = bufferedReader.readLine()) != null) {
					out.append(line + "\n");
				}
				String responseText = out.toString();
				in.close();
				return responseText;
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		Log.v("folder", "entity null");
		return null;
	}
	
	public static int getStatusCode(HttpResponse response) {
		return response.getStatusLine().getStatusCode();
	}
	
	public static boolean isStatusOK(int code) {
		if(code == HttpStatus.SC_OK) {
			return true;
		}
		return false;
	}
	
	public static boolean isStatusUnauthorized(int code) {
		if(code == HttpStatus.SC_UNAUTHORIZED) {
			return true;
		}
		return false;
	}
	
	public static boolean isStatusForbidden(int code) {
		if(code == HttpStatus.SC_FORBIDDEN) {
			return true;
		}
		return false;
	}
	
	public Cookie getCookieJSEESIONID(){
		return CookieJSESSIONID;
	}
	
}
