package hexlet.code.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.LoginDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final JWTHelper jwtHelper;

    public JWTAuthenticationFilter(final AuthenticationManager authenticationManager,
                                   final RequestMatcher loginRequest,
                                   final JWTHelper jwtHelper) {
        super(authenticationManager);
        super.setRequiresAuthenticationRequestMatcher(loginRequest);
        this.jwtHelper = jwtHelper;
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request,
                                                final HttpServletResponse response) throws AuthenticationException {
        final LoginDto loginData = getLoginData(request);
        System.out.println("Login data is extracted!");
        System.out.println(loginData.getUsername());
        System.out.println(loginData.getPassword());
        final var authRequest = new UsernamePasswordAuthenticationToken(
                loginData.getUsername(),
                loginData.getPassword()
        );
        setDetails(request, authRequest);
        System.out.println("Attempt authentication");
        return getAuthenticationManager().authenticate(authRequest);
    }

    private LoginDto getLoginData(final HttpServletRequest request) throws AuthenticationException {
        System.out.println("Extracting login data!");
        try {
            final String json = request.getReader()
                    .lines()
                    .collect(Collectors.joining());
            System.out.println(json);
            return MAPPER.readValue(json, LoginDto.class);
        } catch (IOException e) {
            throw new BadCredentialsException("Can't extract login data from request");
        }
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request,
                                            final HttpServletResponse response,
                                            final FilterChain chain,
                                            final Authentication authResult) throws IOException {
        System.out.println("We are successfully authenticating!");
        final UserDetails user = (UserDetails) authResult.getPrincipal();
        final String token = jwtHelper.expiring(Map.of(SPRING_SECURITY_FORM_USERNAME_KEY, user.getUsername()));

        response.getWriter().println(token);
    }

}
