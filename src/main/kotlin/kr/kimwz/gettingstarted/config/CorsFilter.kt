package kr.kimwz.gettingstarted.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class CorsFilter: OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS" == request.method);
        run {
            // CORS "pre-flight" request
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
            response.addHeader("Access-Control-Allow-Headers", "Authorization, uid, dataType, content-type, x-waple-authorization, cookie")
            response.addHeader("Access-Control-Max-Age", "1728000")
        }
        response.setHeader("Access-Control-Allow-Origin", "*")
        filterChain.doFilter(request, response)
    }
}
