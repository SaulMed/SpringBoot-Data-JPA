package mx.springboot.datajpa.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer{

	//Registrar otro directorio como un recurso estatico de la App ( Carpeta uploads ) 
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// TODO Auto-generated method stub
		WebMvcConfigurer.super.addResourceHandlers(registry);
		
		//Declarar este directorio como la carpeta "static"
		registry.addResourceHandler("/uploads/**")
		.addResourceLocations("file:/C:/Temp/uploads/");
	}
	
}
