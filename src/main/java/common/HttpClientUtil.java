package common;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpClientUtil {
	/** 键值对post发送 */
	public static String postKV(String url, Map<String, String> param, Charset charset, Header... headers)
			throws Exception {
		charset = charset == null ? Charset.forName("UTF-8") : charset;
		if (param != null) {
			List<NameValuePair> paramList = new ArrayList<>();
			for (Entry<String, String> en : param.entrySet()) {
				paramList.add(new BasicNameValuePair(en.getKey(), en.getValue()));
			}
			return basePostGetStr(url, new UrlEncodedFormEntity(paramList, charset), charset, headers);
		}
		return basePostGetStr(url, null, charset, headers);
	}

	/** 字符串post发送 */
	public static String postJson(String url, String param, Charset charset, Header... headers) throws Exception {
		charset = charset == null ? Charset.forName("UTF-8") : charset;
		if (param != null) {
			return basePostGetStr(url, new StringEntity(param, charset), charset, headers);
		}
		return basePostGetStr(url, null, charset, headers);
	}

	/** post 键值对，返回MyResponse */
	public static MyResponse postKVGetRes(String url, Map<String, String> param, Charset charset, Header... headers)
			throws Exception {
		charset = charset == null ? Charset.forName("UTF-8") : charset;
		if (param != null) {
			List<NameValuePair> paramList = new ArrayList<>();
			for (Entry<String, String> en : param.entrySet()) {
				paramList.add(new BasicNameValuePair(en.getKey(), en.getValue()));
			}
			return basePost(url, new UrlEncodedFormEntity(paramList, charset), charset, headers);
		}
		return basePost(url, null, charset, headers);
	}

	/** post 字符串，返回MyResponse */
	public static MyResponse postJsonGetRes(String url, String param, Charset charset, Header... headers)
			throws Exception {
		charset = charset == null ? Charset.forName("UTF-8") : charset;
		if (param != null) {
			return basePost(url, new StringEntity(param, charset), charset, headers);
		}
		return basePost(url, null, charset, headers);
	}

	/** 基础post发送返回string */
	private static String basePostGetStr(String url, HttpEntity param, Charset charset, Header... headers)
			throws Exception {
		return new String(basePost(url, param, charset, headers).getBody(), charset);
	}

	/** 基础post发送 */
	private static MyResponse basePost(String url, HttpEntity param, Charset charset, Header... headers)
			throws Exception {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(url);
			if (headers != null) {
				for (Header header : headers) {
					post.setHeader(header);
				}
			}
			if (param != null) {
				post.setEntity(param);
			}
			try (CloseableHttpResponse response = client.execute(post)) {
				Map<String, Header> headerMap = new HashMap<>();
				for (Header header : response.getAllHeaders()) {
					headerMap.put(header.getName().toLowerCase(), header);
				}
				return new MyResponse(headerMap, EntityUtils.toByteArray(response.getEntity()));
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/** 相应结果 */
	public static class MyResponse {
		private Map<String, Header> headers;
		private byte[] body;

		public MyResponse() {
			super();
		}

		public MyResponse(Map<String, Header> headers, byte[] body) {
			super();
			this.headers = headers;
			this.body = body;
		}

		public Map<String, Header> getHeaders() {
			return headers;
		}

		public void setHeaders(Map<String, Header> headers) {
			this.headers = headers;
		}

		public byte[] getBody() {
			return body;
		}

		public void setBody(byte[] body) {
			this.body = body;
		}

	}
}
