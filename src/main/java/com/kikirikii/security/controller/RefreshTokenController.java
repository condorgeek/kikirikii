package com.kikirikii.security.controller;

import com.kikirikii.exceptions.InvalidTokenException;
import com.kikirikii.model.User;
import com.kikirikii.security.configuration.SecurityProperties;
import com.kikirikii.security.configuration.WebSecurityConfig;
import com.kikirikii.security.model.UserContext;
import com.kikirikii.security.token.BearerJwtToken;
import com.kikirikii.security.token.JwtToken;
import com.kikirikii.security.token.JwtTokenFactory;
import com.kikirikii.security.token.RefreshToken;
import com.kikirikii.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public")
public class RefreshTokenController {
    @Autowired
    private JwtTokenFactory tokenFactory;
    @Autowired
    private SecurityProperties securitySettings;
    @Autowired
    private UserService userService;
    @Autowired
    private JtiTokenVerifier tokenVerifier;

    @RequestMapping(value="/token", method=RequestMethod.GET, produces={ MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    JwtToken refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        BearerJwtToken bearerToken = new BearerJwtToken(request.getHeader(WebSecurityConfig.AUTHENTICATION_HEADER_NAME));
        RefreshToken refreshToken = RefreshToken.create(bearerToken, securitySettings
                .getTokenSigningKey())
                .orElseThrow(InvalidTokenException::new);

        String jti = refreshToken.getJti();
        if (!tokenVerifier.verify(jti)) {
            throw new InvalidTokenException();
        }

        String subject = refreshToken.getSubject();
        User user = userService
                .findByUsername(subject)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + subject));

        if (user.getRoles() == null) throw new InsufficientAuthenticationException("User has no roles assigned");
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());

        UserContext userContext = UserContext.of(user.getUsername(), authorities);

        return tokenFactory.createAccessJwtToken(userContext);
    }

}
