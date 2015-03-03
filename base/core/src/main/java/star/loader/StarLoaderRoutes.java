package star.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.activation.DataHandler;
import javax.servlet.MultipartConfigElement;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.spring.SpringRouteBuilder;
import org.cwatch.boot.camel.MultipartHttpBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

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
		servletRegistrationBean.setMultipartConfig(new MultipartConfigElement(""));
		return servletRegistrationBean;
	}
	
//	@Bean
//	Processor fromMultipart() throws Exception {
//		final org.apache.commons.fileupload.FileUpload fileUpload = new org.apache.commons.fileupload.FileUpload(new DiskFileItemFactory());
//		
//		return new Processor() {
//			@Override
//			public void process(final Exchange exchange) throws Exception {
//				final Message in = exchange.getIn();
//				final InputStream input = in.getMandatoryBody(InputStream.class);
//				
//				RequestContext ctx = new RequestContext() {
//					
//					@Override
//					public InputStream getInputStream() throws IOException {
//						return input;
//					}
//					
//					@Override
//					public String getContentType() {
//						return in.getHeader(Exchange.CONTENT_TYPE, String.class);
//					}
//					
//					@Override
//					public int getContentLength() {
//						return in.getHeader(Exchange.CONTENT_LENGTH, int.class);
//					}
//					
//					@Override
//					public String getCharacterEncoding() {
//						return in.getHeader(Exchange.HTTP_CHARACTER_ENCODING, String.class);
//					}
//				};
//				
//				
//				in.setBody(fileUpload.parseRequest(ctx));
//			}
//		};
//	}
	
	@Bean
	Processor mediterraneanMFL() throws Exception {
		final URL url = applicationContext.getResource("classpath:/mfl/mediterranean-registry.mfl").getURL();
		wlxt.addMFLDocument(url);
		
		return new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				//byte[] bytes = exchange.getIn().getMandatoryBody(byte[].class);
				//ByteArrayInputStream in = new ByteArrayInputStream(bytes);
				//DataHandler in = exchange.getIn().getMandatoryBody(DataHandler.class);
				//exchange.getIn().setBody(wlxt.parse(url, in.getInputStream(), null));
				exchange.getIn().setBody(wlxt.parse(url, exchange.getIn().getMandatoryBody(DataHandler.class).getInputStream(), null));
			}
		};
	}
	
	@Component
	@Converter
	public static class StarLoaderRouteBuilder extends SpringRouteBuilder {

		@Autowired
		StarLoaderProperties properties;
		
		@Override
		public void configure() throws Exception {
			//getContext().getTypeConverterRegistry().addTypeConverter(DataHandler.class, InputStream.class, new DataHandlerTypeConverter());
			
			
			from("servlet://" + properties.getMediterraneanPath() + "?httpBindingRef=multipartHttpBinding&servletName="+properties.getServletName())
			.id("mediterraneanServlet")
			.split().attachments()
			.processRef("mediterraneanMFL")
			.to("stream:out");
		}
		
		@Converter
		public static InputStream getInputStream(DataHandler dataHandler) throws IOException {
			return dataHandler.getInputStream();
		}
		
	}
	
	@Bean
	public MultipartHttpBinding multipartHttpBinding() {
		return new MultipartHttpBinding();
	}

//	public static class DataHandlerTypeConverter extends TypeConverterSupport {
//
//		@SuppressWarnings("unchecked")
//		@Override
//		public <T> T convertTo(Class<T> type, Exchange exchange, Object value)
//				throws TypeConversionException {
//			try {
//				return (T) ((DataHandler)value).getInputStream();
//			} catch (IOException e) {
//				throw new TypeConversionException(value, type, e);
//			}
//		}
//		
//	}
	
}
