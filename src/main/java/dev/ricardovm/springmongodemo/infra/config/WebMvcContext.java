package dev.ricardovm.springmongodemo.infra.config;

import dev.ricardovm.springmongodemo.infra.handlers.UseCaseMethodArgumentResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcContext implements WebMvcConfigurer {

    private final ApplicationContext context;

    public WebMvcContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UseCaseMethodArgumentResolver(context));
    }
}
