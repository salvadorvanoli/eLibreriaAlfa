package ti.elibreriaalfa.security;

import io.jsonwebtoken.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class FiltroJWTAutorizacion extends OncePerRequestFilter {

    private final String key;

    public FiltroJWTAutorizacion(String key) {
        this.key = key;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (validateTokenUse(request)) {
                Claims claims = validateToken(request);
                if (claims.get("authorities") != null) {
                    createAuthentication(claims);
                } else {
                    SecurityContextHolder.clearContext();
                }
            } else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException ex) {
            ((HttpServletResponse) response).sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    ex.getMessage()
            );
        }
    }

    private void createAuthentication(Claims claims) {
        List<String> authorities = (List<String>) claims.get("authorities");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                null,
                authorities.stream().map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private Claims validateToken(HttpServletRequest request) {
        String clientToken = request.getHeader("Authorization")
                .replace("Bearer ", "");
        return Jwts.parser().setSigningKey(key.getBytes())
                .parseClaimsJws(clientToken).getBody();
    }

    private boolean validateTokenUse(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return (authorization != null && authorization.startsWith("Bearer "));
    }
}
