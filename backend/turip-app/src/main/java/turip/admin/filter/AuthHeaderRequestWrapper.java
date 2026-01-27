package turip.admin.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class AuthHeaderRequestWrapper extends HttpServletRequestWrapper {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final String token;

    public AuthHeaderRequestWrapper(HttpServletRequest request, String token) {
        super(request);
        this.token = token;
    }

    @Override
    public String getHeader(String name) {
        if (AUTHORIZATION_HEADER.equals(name)) {
            return "Bearer " + token;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (AUTHORIZATION_HEADER.equals(name)) {
            return Collections.enumeration(Collections.singletonList("Bearer " + token));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = super.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.put(headerName, super.getHeader(headerName));
            }
        }
        headers.put(AUTHORIZATION_HEADER, "Bearer " + token);
        return Collections.enumeration(headers.keySet());
    }
}
