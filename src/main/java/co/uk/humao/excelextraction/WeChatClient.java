package co.uk.humao.excelextraction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.google.gson.Gson;

public class WeChatClient {
	public static final String CORP_ID= "XXXXXXX";
	public static final String CORP_SECRETE= "XXXXXXX";
	public static final int AGENT_ID =  111111;
	
	String getTokenUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+CORP_ID+"&corpsecret="+CORP_SECRETE;
	
	String sendMessageUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";
	public String getToken() {
		return httpGet(getTokenUrl);
		//return sample:"{"errcode":0,"errmsg":"ok","access_token":"gZqsAzJlsSZ78WDDg9ITFFdAb2xvN2zcw4wk9Vr_XrtazOzgsmQYr_Yy7QNffIcshR28ROVKm5cBAorvEm7mU1jUFmHZVUQK0OMIuA1YJIK5BmiRNTuzq1Isf9yHmAJLnDfjT0mGQn9YVsGBl4zN4QNVkDEGe7OQTNnpwT8yFXrwSDSbmC5RdDj1NYrMHfu9tOOXtUiksxTW9bL2rcJmpg","expires_in":7200}"
	}
	
	public Map<String,Object> sendMessage(String message, String userId){
		
		String token = getToken();
		Map<String,Object> returnMap = new HashMap<>();

		Map<String,Object> paramMap = new HashMap<>();
		Map<String,Object> textMap = new HashMap<>();
		paramMap.put("touser",userId);
		paramMap.put("toparty","");
		paramMap.put("totag","");
		paramMap.put("msgtype","text");
		paramMap.put("agentid",AGENT_ID);
		textMap.put("content",message);
		
		paramMap.put("text",textMap);
		paramMap.put("safe",0);
		paramMap.put("enable_id_trans",0);
		paramMap.put("enable_duplicate_check",0);
		paramMap.put("duplicate_check_interval",1800);
		
		String result = httpPost(sendMessageUrl+token,paramMap);
		Gson gson = new Gson();
		WeChatMessage weChatMessage = gson.fromJson(result,WeChatMessage.class);
		if ("ok".equals(weChatMessage.getErrmsg())) {
			returnMap.put("isOK",1);
			if (!StringUtils.isEmpty(weChatMessage.getInvaliduser())) {
				returnMap.put("message","Failed to send message to UserId:"+weChatMessage.getInvaliduser());
				
			}else {
				returnMap.put("message","message went through successfully.");
			}
		}else {
			returnMap.put("isOK", 0);
			returnMap.put("message", "failed to post message.");
		}
		return returnMap;
		
		
	}
	
	
	public String httpGet(String requestUrl) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpGet request = new HttpGet(requestUrl);
		String result = null;

	    // add request headers
	    
		request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");

	        try (CloseableHttpResponse response = httpClient.execute(request)) {

	            // Get HttpResponse Status
	            System.out.println(response.getStatusLine().toString());

	            HttpEntity entity = response.getEntity();
	            Header headers = entity.getContentType();
	            System.out.println(headers);

	            if (entity != null) {
	                // return it as a String
	                result = EntityUtils.toString(entity);
	                
	            }

	        } catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if (httpClient!=null) {
					
					try {
						httpClient.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		
		return result;
		
		
	}
	
	
	public String httpPost(String requestUrl, Map<String,Object> paramsMap) {
		
		
		String result = "";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(requestUrl);
		
		httpPost.addHeader("Content-type","application/json; charset=utf-8");
		
		if (paramsMap != null && paramsMap.isEmpty()) {
			
			JSONObject jsonObject = new JSONObject(paramsMap);
			StringEntity params = new StringEntity(jsonObject.toString(),Consts.UTF_8);
			httpPost.setEntity(params);
		}
		
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);

				HttpEntity entity = response.getEntity();
				if (entity!=null) {
					result = EntityUtils.toString(entity,"UTF-8");
					
				}
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				if (httpClient!=null) {
					try {
						httpClient.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		
		return result;
		
		
		
	}

}
