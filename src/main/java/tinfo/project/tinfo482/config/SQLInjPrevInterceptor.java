package tinfo.project.tinfo482.config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class SQLInjPrevInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       log.debug("Enter Custom Interceptor:::: SQLInjPrevInterceptor...");
//        String parameterValue = request.getParameter("")

        return true;
    }

    private boolean containsSpecialCharacter(String value){
        return value != null && value.matches("^[a-zA-z0-9]*$");
    }



}
