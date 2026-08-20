package org.example.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.xml.stream.*;
import java.io.IOException;
import java.io.InputStream;

public class JwtRequestFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // фильтр нужен только для SOAP-эндпоинта
        return !request.getRequestURI().startsWith("/ws");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        LoginRequestBody wrapped = new LoginRequestBody(request);
        String rootName = rootName(wrapped.getInputStream());

        if ("LoginRequest".equals(rootName)) {
            UsernamePasswordAuthenticationToken unknown = new UsernamePasswordAuthenticationToken(
                    "unknown", null, AuthorityUtils.NO_AUTHORITIES);
            SecurityContextHolder.getContext().setAuthentication(unknown);
        }
        filterChain.doFilter(wrapped, response);
    }

    private String rootName(InputStream is) {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(is);
            boolean insideBody = false;

            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if ("Body".equals(reader.getLocalName())) {
                        insideBody = true;
                    } else if (insideBody) {
                        return reader.getLocalName();
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
