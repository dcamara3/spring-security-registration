package org.baeldung.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ // @formatter:off 
    ChangePasswordIntegrationTest.class, 
    RegistrationControllerIntegrationTest.class,
    GetLoggedUsersIntegrationTest.class,
    UserServiceIntegrationTest.class,
    UserIntegrationTest.class
})// @formatter:on
public class IntegrationSuite {
    //
}