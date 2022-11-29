package dev.ricardovm.springmongodemo.infra.handlers;

import dev.ricardovm.springmongodemo.infra.annotations.UseCase;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UseCaseMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final ApplicationContext context;

    public UseCaseMethodArgumentResolver(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().getAnnotation(UseCase.class) != null;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        return context.getBean(parameter.getParameterType());
    }
}
