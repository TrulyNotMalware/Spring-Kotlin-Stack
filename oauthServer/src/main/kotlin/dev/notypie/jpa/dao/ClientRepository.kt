package dev.notypie.jpa.dao

import dev.notypie.domain.Client
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
@Profile("jpa-oauth-server")
interface ClientRepository: JpaRepository<Client, Long>{

    fun findByClientId(clientId: String): Optional<Client>

}