package org.baeldung.test;

import com.ustn.userprofile.UserAccount;
import com.ustn.userprofile.manager.UserManager;
import org.baeldung.config.ServiceConfig;
import org.baeldung.persistence.dao.VerificationTokenRepository;
import org.baeldung.persistence.model.VerificationToken;
import org.baeldung.spring.TestDbConfig;
import org.baeldung.spring.TestIntegrationConfig;
import org.baeldung.validation.EmailExistsException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestDbConfig.class, ServiceConfig.class, TestIntegrationConfig.class})
@Transactional
public class UserIntegrationTest {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserManager userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void givenUserAndVerificationToken() throws EmailExistsException {
        UserAccount user = new UserAccount();
        user.setEmail("test@example.com");
        user.setPassword("SecretPassword");
        user.setName("First");
        user.setLogin("Last");
        entityManager.persist(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        entityManager.persist(verificationToken);

        entityManager.flush();
        entityManager.clear();
    }

    @After
    public void flushAfter() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void whenContextLoad_thenCorrect() {
        //assertEquals(1, userRepository.count());
        //assertEquals(1, tokenRepository.count());
    }
}