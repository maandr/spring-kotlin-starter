package maandr.starters.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.webmvc.RepositoryRestController

@RepositoryRestController
interface UserRepository : JpaRepository<User, Long>