/*
package org.baeldung.service;

import com.ustn.userprofile.Permission;
import com.ustn.userprofile.UserAccount;
import com.ustn.userprofile.manager.UserManager;
import com.ustn.userprofile.security.SpringSecurityUserWithSalt;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class UstnUserDetailsService implements UserDetailsService {

	private static Logger logger = Logger.getLogger(com.ustn.userprofile.security.UstnUserDetailsService.class);

	@Autowired
	private UserManager userManager;

	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		logger.info("Looking up user: " + name);
		UserAccount user = userManager.getUserAccount(name);

		if (user == null) {
			logger.info("User not found");
			throw new UsernameNotFoundException("Username not found");
		}
		SpringSecurityUserWithSalt springSecurityUserWithSalt =
				new SpringSecurityUserWithSalt(
						user.getName(),
						user.getPassword(),
						user.isActive(),
						true, true, true,
						getGrantedAuthorities(user)
				);
		springSecurityUserWithSalt.setSalt(user.getSalt());
		return springSecurityUserWithSalt;
	}

	private List<GrantedAuthority> getGrantedAuthorities(UserAccount user) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Permission permission : user.getPermissionList()) {
//			logger.info("permission : " + permission);
			authorities.add(new SimpleGrantedAuthority("ROLE_" + permission.getName()));
		}
//		logger.info("authorities : " + authorities);
		return authorities;
	}
}*/
