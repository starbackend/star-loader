package star.auto;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import star.loader.StarLoaderConfiguration;

@Configuration
@Import(StarLoaderConfiguration.class)
public class StarLoaderAutoConfiguration {

}
