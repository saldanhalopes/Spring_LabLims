package br.com.lablims.spring_lablims.controller.auth.custom;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;


public class CustomAuthenticationDetails extends WebAuthenticationDetails {

    private String user2FaCode;

    public CustomAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.user2FaCode = request.getParameter("user2FaCode");
    }

    public String getUser2FaCode() {
        return user2FaCode;
    }
}
