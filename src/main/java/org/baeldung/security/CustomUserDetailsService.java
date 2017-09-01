package org.baeldung.security;

import com.ustn.userprofile.dto.LegacyAuthorityDto;
import com.ustn.userprofile.dto.LegacyUserLoginMetadataDtoExtended;
import com.ustn.userprofile.security.SpringSecurityUserWithSalt;
import com.ustn.userprofile.web.client.legacy.UserProfileClient;
import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private static Logger logger = Logger.getLogger(CustomUserDetailsService.class);

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        logger.info("Looking up user: " + loginName);
        LegacyUserLoginMetadataDtoExtended userAccount = UserProfileClient.getUserByLoginName(loginName);

        if (userAccount == null) {
            logger.info("User not found");
            throw new UsernameNotFoundException("Username not found");
        }

        SpringSecurityUserWithSalt springSecurityUserWithSalt =
                new SpringSecurityUserWithSalt(
                        userAccount.getName(),
                        userAccount.getPassword(),
                        userAccount.isActive(),
                        true, true, true,
                        getGrantedAuthorities(userAccount)
                );
        springSecurityUserWithSalt.setSalt(userAccount.getSalt());
        return springSecurityUserWithSalt;
    }

    private List<GrantedAuthority> getGrantedAuthorities(LegacyUserLoginMetadataDtoExtended user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (LegacyAuthorityDto authority : user.getAuthorities().getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + authority.getName()));
        }
        logger.info("authorities : " + authorities);
        return authorities;
    }
}