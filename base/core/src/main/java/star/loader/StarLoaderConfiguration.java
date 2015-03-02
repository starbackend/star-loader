package star.loader;

import org.cwatch.boot.camel.CamelBootConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:/star-loader.properties"})
@Import({ProjectOVRUploadController.class, StarLoaderRoutes.class, CamelBootConfiguration.class})
public class StarLoaderConfiguration {
	
	
	
}
