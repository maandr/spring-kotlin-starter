package maandr.starters.user

import maandr.starters.*
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.After
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class UserRestIntegrationTest : AbstractRestIntegrationTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var existingEntities: Collection<User>

    @After
    fun cleanup() = userRepository.deleteAllInBatch()

    @Test
    fun `should list all existing users`() {
        // Given
        givenExistingUsers()

        // When
        perform(method = HttpMethod.GET, resourcePath = "/users")

        // Then
        resultActions
            .print()
            .andExpectThat {
                responseStatus(HttpStatus.OK)
                jsonPathExists("page")
                jsonPathEquals("page.totalElements", existingEntities.size)
            }
    }

    @Test
    fun `should create a new user`() {
        // Given
        val user = anna()

        // When
        perform(method = HttpMethod.POST, resourcePath = "/users", content = user)

        // Then
        resultActions
            .print()
            .andExpectThat {
                responseStatus(HttpStatus.CREATED)
                headerExists(HttpHeaders.LOCATION)
                jsonPathEquals("name", user.name)
                jsonPathEquals("age", user.age)
            }
    }

    @Test
    fun `should delete existing user`() {
        // Given
        givenExistingUsers()
        val user = existingEntities.first()

        // When
        perform(method = HttpMethod.DELETE, resourcePath = "/users/${user.id}")

        // Then
        resultActions
            .print()
            .andExpectThat {
                responseStatus(HttpStatus.NO_CONTENT)
            }
        assertThat(userRepository.count()).isEqualTo(existingEntities.size - 1L)
        assertThat(userRepository.findAll()).containsExactlyInAnyOrderElementsOf(existingEntities
                        .filter { it.id != user.id  }
                        .toList())
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