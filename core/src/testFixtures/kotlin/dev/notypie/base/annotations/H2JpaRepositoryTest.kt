package dev.notypie.base.annotations

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.TestPropertySource

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@DataJpaTest
@TestPropertySource(properties = ["spring.config.location = classpath:application-test.yaml"])
annotation class H2JpaRepositoryTest()
