package org.baeldung.service;

import com.ustn.userprofile.UserAccount;
import com.ustn.userprofile.dto.UserMvcDto;
import org.baeldung.persistence.model.PasswordResetToken;
import org.baeldung.persistence.model.VerificationToken;
import org.baeldung.web.error.UserAlreadyExistException;

import java.util.List;

public interface IUserService {

    UserAccount registerNewUserAccount(UserMvcDto accountDto) throws UserAlreadyExistException;

    UserAccount getUser(String verificationToken);

    void saveRegisteredUser(UserAccount user);

    void createVerificationTokenForUser(UserAccount user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String token);

    void createPasswordResetTokenForUser(UserAccount user, String token);

    UserAccount findUserByEmail(String email);

    PasswordResetToken getPasswordResetToken(String token);

    UserAccount getUserByPasswordResetToken(String token);

    UserAccount getUserByID(long id);

    void changeUserPassword(UserAccount user, String password);

    boolean checkIfValidOldPassword(UserAccount user, String password);

    String validateVerificationToken(String token);

    List<String> getUsersFromSessionRegistry();
}
