package dev.notypie.base.annotations

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@TestPropertySource(properties = ["spring.config.location = classpath:application-test.yaml"])
annotation class ControllerTest