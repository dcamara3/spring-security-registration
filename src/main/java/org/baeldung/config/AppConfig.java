package org.baeldung.config;

import com.ustn.userprofile.manager.UserManager;
import org.baeldung.security.ActiveUserStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//http://www.baeldung.com/spring-security-registration-i-forgot-my-password

@Configuration
public class AppConfig {
    // beans

    @Bean
    public ActiveUserStore activeUserStore() {
        return new ActiveUserStore();
    }

    @Bean
    public UserManager userManager () {
        return new UserManager();
    }

    /*@Bean
    public JavaMailSender javaMailSender() {

        String host = "localhost";
        int port = 25;

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties mailProperties = new Properties();
        //mailProperties.put("mail.smtp.auth", false);
        //mailProperties.put("mail.smtp.starttls.enable", starttls);
        mailSender.setJavaMailProperties(mailProperties);

        mailSender.setHost(host);
        mailSender.setPort(port);
        //mailSender.setProtocol(protocol);
        //mailSender.setUsername(username);
        //mailSender.setPassword(password);
        return mailSender;
    }*/
}