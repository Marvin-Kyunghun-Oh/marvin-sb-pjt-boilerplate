package com.marvin.boiler.global.test;

import com.marvin.boiler.config.JpaAuditConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DataJpaTest // 원본 애노테이션을 메타 애노테이션으로 포함
@Import({JpaAuditConfig.class}) // 공통으로 필요한 빈 설정
public @interface DataJpaTestConfig {

}
