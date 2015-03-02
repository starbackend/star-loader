package star.loader;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "star-loader")
public class StarLoaderProperties {
	
	private String servletBaseInputPath = "/starupload";
	
	private String servletName = "uploadServlet";
	
	private String mediterraneanPath = "/mediterranean";

	public String getServletBaseInputPath() {
		return servletBaseInputPath;
	}

	public void setServletBaseInputPath(String servletBaseInputPath) {
		this.servletBaseInputPath = servletBaseInputPath;
	}

	public String getServletName() {
		return servletName;
	}

	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

	public String getMediterraneanPath() {
		return mediterraneanPath;
	}

	public void setMediterraneanPath(String mediterraneanPath) {
		this.mediterraneanPath = mediterraneanPath;
	}
	
}
