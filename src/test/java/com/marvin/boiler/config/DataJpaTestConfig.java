package com.marvin.boiler.config;

import com.marvin.boiler.config.JpaAuditConfig;
import com.marvin.boiler.config.SecurityConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DataJpaTest
@Import({JpaAuditConfig.class, PasswordConfig.class}) // SecurityConfig 대신 PasswordConfig만 포함
public @interface DataJpaTestConfig {

}
