package org.baeldung.test;

import com.ustn.userprofile.UserAccount;
import com.ustn.userprofile.dto.UserMvcDto;
import org.baeldung.config.ServiceConfig;
import org.baeldung.persistence.dao.VerificationTokenRepository;
import org.baeldung.persistence.model.VerificationToken;
import org.baeldung.service.IUserService;
import org.baeldung.service.UserService;
import org.baeldung.spring.TestDbConfig;
import org.baeldung.spring.TestIntegrationConfig;
import org.baeldung.validation.EmailExistsException;
import org.baeldung.web.error.UserAlreadyExistException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestDbConfig.class, ServiceConfig.class, TestIntegrationConfig.class })
public class UserServiceIntegrationTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Test
    public void givenNewUser_whenRegistered_thenCorrect() throws EmailExistsException {
        final String userEmail = UUID.randomUUID().toString();
        final UserMvcDto userDto = createUserDto(userEmail);

        final UserAccount user = userService.registerNewUserAccount(userDto);

        assertNotNull(user);
        assertNotNull(user.getEmail());
        assertEquals(userEmail, user.getEmail());
        assertNotNull(user.getId());
    }

    @Test
    public void givenDetachedUser_whenServiceLoadById_thenCorrect() throws EmailExistsException {
        final UserAccount user = registerUser();
        final UserAccount user2 = userService.getUserByID(user.getId());
        assertEquals(user, user2);
    }

    @Test
    public void givenDetachedUser_whenServiceLoadByEmail_thenCorrect() throws EmailExistsException {
        final UserAccount user = registerUser();
        final UserAccount user2 = userService.findUserByEmail(user.getEmail());
        assertEquals(user, user2);
    }

    @Test(expected = UserAlreadyExistException.class)
    public void givenUserRegistered_whenDuplicatedRegister_thenCorrect() {
        final String email = UUID.randomUUID().toString();
        final UserMvcDto userDto = createUserDto(email);

        userService.registerNewUserAccount(userDto);
        userService.registerNewUserAccount(userDto);
    }

    @Test
    public void givenUserRegistered_whenCreateToken_thenCorrect() {
        final UserAccount user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
    }

    @Test
    public void givenUserRegistered_whenCreateTokenCreateDuplicate_thenCorrect() {
        final UserAccount user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        userService.createVerificationTokenForUser(user, token);
    }

    @Test
    public void givenUserAndToken_whenLoadToken_thenCorrect() {
        final UserAccount user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final VerificationToken verificationToken = userService.getVerificationToken(token);
        assertNotNull(verificationToken);
        assertNotNull(verificationToken.getId());
        assertNotNull(verificationToken.getUser());
        assertEquals(user, verificationToken.getUser());
        assertEquals(user.getId(), verificationToken.getUser().getId());
        assertEquals(token, verificationToken.getToken());
        assertTrue(verificationToken.getExpiryDate().toInstant().isAfter(Instant.now()));
    }

    @Test
    public void givenUserAndToken_whenRemovingToken_thenCorrect() {
        final UserAccount user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final long tokenId = userService.getVerificationToken(token).getId();
        tokenRepository.delete(tokenId);
    }

    @Test
    public void givenUserAndToken_whenNewTokenRequest_thenCorrect() {
        final UserAccount user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final VerificationToken origToken = userService.getVerificationToken(token);
        final VerificationToken newToken = userService.generateNewVerificationToken(token);
        assertNotEquals(newToken.getToken(), origToken.getToken());
        assertNotEquals(newToken.getExpiryDate(), origToken.getExpiryDate());
        assertNotEquals(newToken, origToken);
    }

    @Test
    public void givenTokenValidation_whenValid_thenUserEnabled_andTokenRemoved() {
        UserAccount user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final long userId = user.getId();
        final String token_status = userService.validateVerificationToken(token);
        assertEquals(token_status, UserService.TOKEN_VALID);
        user = userService.getUserByID(userId);
        assertTrue(user.isActive());
    }

    @Test
    public void givenTokenValidation_whenInvalid_thenCorrect() {
        final UserAccount user = registerUser();
        final String token = UUID.randomUUID().toString();
        final String invalid_token = "INVALID_" + UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        userService.getVerificationToken(token).getId();
        final String token_status = userService.validateVerificationToken(invalid_token);
        token_status.equals(UserService.TOKEN_INVALID);
    }

    @Test
    public void givenTokenValidation_whenExpired_thenCorrect() {
        final UserAccount user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        user.getId();
        final VerificationToken verificationToken = userService.getVerificationToken(token);
        verificationToken.setExpiryDate(Date.from(verificationToken.getExpiryDate().toInstant().minus(2, ChronoUnit.DAYS)));
        tokenRepository.save(verificationToken);
        //tokenRepository.saveAndFlush(verificationToken);
        final String token_status = userService.validateVerificationToken(token);
        assertNotNull(token_status);
        token_status.equals(UserService.TOKEN_EXPIRED);
    }

    //

    private UserMvcDto createUserDto(final String email) {
        final UserMvcDto userDto = new UserMvcDto();
        userDto.setEmail(email);
        userDto.setPassword("SecretPassword");
        userDto.setMatchingPassword("SecretPassword");
        userDto.setName("First");
        userDto.setLogin("Last");
        return userDto;
    }

    private UserAccount registerUser() {
        final String email = UUID.randomUUID().toString();
        final UserMvcDto userDto = createUserDto(email);
        final UserAccount user = userService.registerNewUserAccount(userDto);
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(email, user.getEmail());
        return user;
    }

}
