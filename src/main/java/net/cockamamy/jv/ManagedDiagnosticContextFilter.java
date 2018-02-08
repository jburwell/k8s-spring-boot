/*
 * Copyright 2018 John S. Burwell III
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cockamamy.jv;

import static net.cockamamy.jv.util.MorePreconditions.checkArgumentNotNull;

import java.io.IOException;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
@Immutable
public final class ManagedDiagnosticContextFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ManagedDiagnosticContextFilter.class);

    public final static String REQUEST_ID_HEADER = "JV-Request-Id";

    private static String newRequestId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // DO nothing
    }

    @Override
    public void doFilter(@Nonnull final ServletRequest request, @Nonnull final ServletResponse response,
        @Nonnull final FilterChain chain)
        throws IOException, ServletException {

        checkArgumentNotNull(request);
        checkArgumentNotNull(response);
        checkArgumentNotNull(chain);

        try {
            final String requestId = newRequestId();
            final HttpServletResponse httpResponse = (HttpServletResponse) response;

            LOG.trace("Associating required id {} with request {}", requestId, request);
            httpResponse.addHeader(REQUEST_ID_HEADER, requestId);
            MDC.put("requestId", newRequestId());

            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }

    }

    @Override
    public void destroy() {
        // Do nothing
    }

}
