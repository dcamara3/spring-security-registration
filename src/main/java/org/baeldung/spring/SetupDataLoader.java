package org.baeldung.spring;

import com.ustn.userprofile.UserAccount;
import com.ustn.userprofile.manager.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserManager userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {

        alreadySetup = true;
        if (alreadySetup) {
            return;
        }

        final UserAccount user = new UserAccount();
        user.setName("Test");
        user.setLogin("test");
        user.setPassword(passwordEncoder.encode("test"));
        user.setEmail("test@test.com");
        user.setActive(true);
        userRepository.insertUserAccount(user, new ArrayList<>());

        alreadySetup = true;
    }
}