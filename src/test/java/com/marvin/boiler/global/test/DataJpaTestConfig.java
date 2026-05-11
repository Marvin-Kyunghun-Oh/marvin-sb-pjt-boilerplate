package com.marvin.boiler.global.test;

import com.marvin.boiler.config.JpaAuditConfig;
import com.marvin.boiler.config.SecurityConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DataJpaTest
@Import({JpaAuditConfig.class, SecurityConfig.class}) // SecurityConfig를 포함하여 PasswordEncoder 빈 확보
public @interface DataJpaTestConfig {

}
