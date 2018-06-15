package maandr.starters.user

import maandr.starters.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class UserRestIntegrationTest : AbstractRestIntegrationTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var existingEntities: Collection<User>

    @Test
    fun `should list all existing users`() {
        // Given
        givenExistingUsers()

        // When
        perform(method = HttpMethod.GET, resourcePath = "/users")

        // Then
        resultActions
            .andPrint()
            .andExpectThat {
                responseStatus(HttpStatus.OK)
            }
    }

    fun givenExistingUsers() {
        existingEntities = listOf(
            userRepository.save(max()),
            userRepository.save(anna()),
            userRepository.save(john())
        )
    }

    fun max() = User(name = "Max", age = 33)
    fun anna() = User(name = "Anna", age = 28)
    fun john() = User(name = "John", age = 54)
}