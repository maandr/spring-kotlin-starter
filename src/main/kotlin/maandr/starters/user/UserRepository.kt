package maandr.starters.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RestResource
import org.springframework.data.rest.webmvc.RepositoryRestController
import java.util.*

@RepositoryRestController
interface UserRepository : JpaRepository<User, Long> {

    @RestResource(path = "find-by-name")
    fun findByName(@Param("name") name: String): Optional<User>

    @RestResource(path = "find-by-age")
    fun findByAge(@Param("age") age: Int): List<User>
}