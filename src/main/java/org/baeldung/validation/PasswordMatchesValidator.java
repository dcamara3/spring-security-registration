package org.baeldung.validation;

import com.ustn.userprofile.dto.UserMvcDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
        //
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final UserMvcDto user = (UserMvcDto) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }

}
