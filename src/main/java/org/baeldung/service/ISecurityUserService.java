package org.baeldung.service;

public interface ISecurityUserService {

    String validatePasswordResetToken(long id, String token);

}
