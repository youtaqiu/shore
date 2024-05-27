package io.irain.reactor.web.autoconfigure;

import io.irain.reactor.commons.enums.EnvEnum;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * SwaggerConfiguration is a configuration class that sets up the OpenAPI documentation for Swagger.
 * It includes methods for creating the OpenAPI instance and setting up the contact, license, security scheme, and servers.
 *
 * @author youta
 */
@Configuration
public class SwaggerConfiguration {

    /**
     * 环境
     */
    private final Environment env;

    /***
     * SwaggerConfiguration 构造函数
     * @param env Environment 环境
     */
    public SwaggerConfiguration(Environment env) {
        this.env = env;
    }

    /***
     * customOpenAPI
     * @return OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact()
                .name("youta")
                .email("youta@irain.io")
                .url("https://www.irain.io/")
                .extensions(new HashMap<>());
        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                .identifier("Apache-2.0")
                .extensions(new HashMap<>());
        Info info = new Info()
                .title("服务端接口文档")
                .description("服务端api接口文档")
                .version("v1")
                .termsOfService("https://api.irain.io/")
                .license(license)
                .contact(contact);
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .description("令牌")
                .name("Authorization")
                .in(SecurityScheme.In.HEADER);

        String environment = env.getProperty("spring.profiles.active"); // 获取当前活动的环境
        List<Server> servers = new ArrayList<>();
        if (!EnvEnum.LOCAL.getEnv().equals(environment)) {
            servers.add(new Server().url("https://test.irain.io").description("Test server"));
            servers.add(new Server().url("https://api.irain.io").description("Production server"));
        }
        var openAPI = new OpenAPI()
                .openapi("3.1.0")
                .info(info)
                .components(new Components().addSecuritySchemes("BearerAuth", securityScheme))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
        if (!servers.isEmpty()) {
            openAPI.servers(servers);
        }
        return openAPI;
    }
}
