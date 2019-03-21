package net.cockamamy.jv;

import com.google.common.net.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Optional;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;

@RestControllerAdvice(basePackageClasses = Application.class)
public class ValueHandler implements ResponseBodyAdvice<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(ValueHandler.class);

    @Override
    public boolean supports(final MethodParameter returnType,
                            final Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getParameterType().equals(Optional.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object beforeBodyWrite(final Object body,
                                  final MethodParameter returnType,
                                  final MediaType selectedContentType,
                                  final Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  final ServerHttpRequest request,
                                  final ServerHttpResponse response) {

        LOG.debug("Handling optional body {}", body);

        if (body == null) {
            return null;
        }

        return ((Optional<KvStore.Value>) body).map(value -> {
            response.getHeaders().set(CONTENT_TYPE, value.getContentType().getType());
            return value.getObject();
        }).orElse(null);

    }
}

