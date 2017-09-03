package org.baeldung.config;

import com.radiatemedia.phoenix.util.database.DatabaseConnectionUtil;
import com.ustn.userprofile.manager.UserManager;
import org.baeldung.security.ActiveUserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

//http://www.baeldung.com/spring-security-registration-i-forgot-my-password

@Configuration
public class AppConfig {

    //@Autowired
    private DataSource dataSource;

    @Bean
    public ActiveUserStore activeUserStore() {
        return new ActiveUserStore();
    }

    @Bean
    public UserManager userManager () {
        dataSource = DatabaseConnectionUtil.getInstance().getDataSource("userprofile3");
        UserManager userManager = new UserManager();
        userManager.setDataSource(dataSource);
        return userManager;
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