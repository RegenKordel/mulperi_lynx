package eu.openreq.mulperi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("eu.openreq.mulperi.controllers"))    
                .paths(PathSelectors.any())  
                .build()
                .apiInfo(metaData());
             
    }
    
    private ApiInfo metaData() {
      return new ApiInfoBuilder()
              .title("Mulperi")
              .description("Easy to use REST API with JSON for configuring models. Mulperi is a service taking care of the functionality of constructing a model from requirements, more precisely utilizing feature modeling technologies (for details about adopted feature model technology, see D5.1). The constructed model allows expressing various kinds of relationship and, thus, dependency inferences by using KeljuCaaS. Therefore, Mulperi also provides interfaces for the operations to the resulting model.")
              .build();
  }
}