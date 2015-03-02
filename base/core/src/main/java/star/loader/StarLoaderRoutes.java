package star.loader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.bea.wlxt.WLXT;

@Configuration
@EnableConfigurationProperties(StarLoaderProperties.class)
public class StarLoaderRoutes {
	
	public static final String FILE_ITEM_PROPERTY = "FILE_ITEM_PROPERTY"; 
	
	@Autowired
	ApplicationContext applicationContext;
	
	@Bean
	ServletRegistrationBean vdmInputServlet(StarLoaderProperties properties) {
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new CamelHttpTransportServlet(), properties.getServletBaseInputPath()+"/*");
		servletRegistrationBean.setName(properties.getServletName());
		return servletRegistrationBean;
	}
	
	@Bean
	Processor fromMultipart() throws Exception {
		final org.apache.commons.fileupload.FileUpload fileUpload = new org.apache.commons.fileupload.FileUpload();
		
		return new Processor() {
			@Override
			public void process(final Exchange exchange) throws Exception {
				final Message in = exchange.getIn();
				final InputStream input = in.getMandatoryBody(InputStream.class);
				
				RequestContext ctx = new RequestContext() {
					
					@Override
					public InputStream getInputStream() throws IOException {
						return input;
					}
					
					@Override
					public String getContentType() {
						return in.getHeader(Exchange.CONTENT_TYPE, String.class);
					}
					
					@Override
					public int getContentLength() {
						return in.getHeader(Exchange.CONTENT_LENGTH, int.class);
					}
					
					@Override
					public String getCharacterEncoding() {
						return in.getHeader(Exchange.HTTP_CHARACTER_ENCODING, String.class);
					}
				};
				
				FileItemIterator iterator = fileUpload.getItemIterator(ctx);
				
				FileItemStream file = iterator.next();
				
				exchange.setProperty(FILE_ITEM_PROPERTY, file);
				in.setBody(file.openStream());
			}
		};
	}
	
	@Bean
	Processor mediterraneanMFL() throws Exception {
		final WLXT wlxt = new WLXT();
		final URL url = applicationContext.getResource("classpath:/mfl/mediterranean-registry.mfl").getURL();
		wlxt.addMFLDocument(url);
		
		return new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				//byte[] bytes = exchange.getIn().getMandatoryBody(byte[].class);
				//ByteArrayInputStream in = new ByteArrayInputStream(bytes);
				InputStream in = exchange.getIn().getMandatoryBody(InputStream.class);
				exchange.getIn().setBody(wlxt.parse(url, in, null));
			}
		};
	}
	
	@Component
	public static class StarLoaderRouteBuilder extends SpringRouteBuilder {

		@Autowired
		StarLoaderProperties properties;
		
		@Override
		public void configure() throws Exception {
			
			from("servlet://" + properties.getMediterraneanPath() + "?servletName="+properties.getServletName())
			.id("mediterraneanServlet")
			.processRef("fromMultipart")
			.processRef("mediterraneanMFL")
			.to("stream:out");
		}
		
		
		
	}

}
