package br.com.lablims.spring_lablims.config;

import br.com.lablims.spring_lablims.domain.CustomRevisionEntity;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Map;

public class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(final Object entity) {
        CustomRevisionEntity revision = (CustomRevisionEntity) entity;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            Object principal = auth.getPrincipal();
            Object details = auth.getDetails();
            if (principal instanceof UserDetails)
                revision.setUserName(((UserDetails) principal).getUsername());
            if (details instanceof WebAuthenticationDetails)
                revision.setIp(((WebAuthenticationDetails) details).getRemoteAddress());
            revision.setMotivo(CustomRevisionEntity.getMotivoText());
        }
    }
}