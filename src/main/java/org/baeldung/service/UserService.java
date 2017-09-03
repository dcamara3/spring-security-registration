package org.baeldung.service;

import com.ustn.userprofile.UserAccount;
import com.ustn.userprofile.dto.UserMvcDto;
import com.ustn.userprofile.manager.UserManager;
import org.baeldung.persistence.dao.PasswordResetTokenRepository;
import org.baeldung.persistence.dao.VerificationTokenRepository;
import org.baeldung.persistence.model.PasswordResetToken;
import org.baeldung.persistence.model.VerificationToken;
import org.baeldung.web.error.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements IUserService {

    @Autowired
    private UserManager repository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    @Autowired
    @Qualifier("passwordEncoder")
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SessionRegistry sessionRegistry;

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";

    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String APP_NAME = "SpringRegistration";

    // API

    @Override
    public UserAccount registerNewUserAccount(final UserMvcDto accountDto) {
        if (emailExist(accountDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email adress: " + accountDto.getEmail());
        }
        final UserAccount user = new UserAccount();

        user.setName(accountDto.getName());
        user.setLogin(accountDto.getLogin());
        user.setPassword(passwordEncoder.encodePassword(accountDto.getPassword(), accountDto.getEmail()));
        user.setEmail(accountDto.getEmail());
        return repository.insertUserAccount(user, new ArrayList<>());
    }

    @Override
    public UserAccount getUser(final String verificationToken) {
        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    @Override
    public VerificationToken getVerificationToken(final String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(final UserAccount user) {
        repository.updateUserAccount(user, new ArrayList<>());
    }

    @Override
    public void createVerificationTokenForUser(final UserAccount user, final String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
        VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);
        vToken.updateToken(UUID.randomUUID().toString());
        vToken = tokenRepository.save(vToken);
        return vToken;
    }

    @Override
    public void createPasswordResetTokenForUser(final UserAccount user, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    @Override
    public UserAccount findUserByEmail(final String email) {
        return repository.findByEmail(email);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token);
    }

    @Override
    public UserAccount getUserByPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token).getUser();
    }

    @Override
    public UserAccount getUserByID(final long id) {
        return repository.getUserAccount(id);
    }

    @Override
    public void changeUserPassword(final UserAccount user, final String password) {
        user.setPassword(passwordEncoder.encodePassword(password, user.getEmail()));
        repository.updateUserAccount(user, new ArrayList<>());
    }

    @Override
    public boolean checkIfValidOldPassword(final UserAccount user, final String oldPassword) {
        return passwordEncoder.isPasswordValid(oldPassword, user.getPassword(), user.getEmail());
    }

    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final UserAccount user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        user.setActive(true);
        // tokenRepository.delete(verificationToken);
        repository.updateUserAccount(user, new ArrayList<>());
        return TOKEN_VALID;
    }

    private boolean emailExist(final String email) {
        return repository.findByEmail(email) != null;
    }

    @Override
    public List<String> getUsersFromSessionRegistry() {
        return sessionRegistry.getAllPrincipals().stream().filter((u) -> !sessionRegistry.getAllSessions(u, false).isEmpty()).map(Object::toString).collect(Collectors.toList());
    }

}