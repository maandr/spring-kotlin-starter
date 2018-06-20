package maandr.starters.user

import maandr.starters.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParameters

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
                statusEquals(HttpStatus.OK)
                jsonPathExists("page")
                jsonPathEquals("page.totalElements", existingEntities.size)
            }
            .andDo(document("users-get-list",
                responseFields(userListFields()).and(ignorePagination()).and(ignoreLinks())
            ))
    }

    @Test
    fun `should get an existing user by id`() {
        // Given
        givenExistingUsers()
        val user = existingEntities.first()

        // When
        perform(method = HttpMethod.GET, resourcePath = "/users/${user.id}")

        // Then
        resultActions
            .print()
            .andExpectThat {
                statusEquals(HttpStatus.OK)
                jsonPathEquals("name", user.name)
                jsonPathEquals("age", user.age)
            }
            .andDo(document("users-read",
                responseFields(userFields()).and(ignoreLinks())
            ))
    }

    @Test
    fun `should respond 404 when a non existing user is requested`() {
        // Given
        givenExistingUsers()

        // When
        perform(method = HttpMethod.GET, resourcePath = "/users/404")

        // Then
        resultActions
            .print()
            .andExpectThat {
                statusEquals(HttpStatus.NOT_FOUND)
            }
    }

    @Test
    fun `should find user by name`() {
        // Given
        givenExistingUsers()
        val user = existingEntities.first()

        // When
        perform(method = HttpMethod.GET, resourcePath = "/users/search/find-by-name?name=${user.name}")

        // Then
        resultActions
            .print()
            .andExpectThat {
                statusEquals(HttpStatus.OK)
                jsonPathHasSize("_embedded.users", 1)
                jsonPathEquals("_embedded.users[0].name", user.name)
                jsonPathEquals("_embedded.users[0].age", user.age)
            }
            .andDo(document("users-find-by-name",
                requestParameters(parameterWithName("name").description("Name to search for.")),
                responseFields(userListFields()).and(ignorePagination()).and(ignoreLinks())
            ))
    }

    @Test
    fun `should return empty list when no user can be found with the given name`() {
        // Given
        givenExistingUsers()

        // When
        perform(method = HttpMethod.GET, resourcePath = "/users/search/find-by-name?name=john")

        // Then
        resultActions
            .print()
            .andExpectThat {
                statusEquals(HttpStatus.OK)
                jsonPathHasSize("_embedded.users", 0)
            }
    }

    @Test
    fun `should find users by age`() {
        // Given
        givenExistingUsers()
        val user = existingEntities.first()

        // When
        perform(method = HttpMethod.GET, resourcePath = "/users/search/find-by-age?age=${user.age}")

        // Then
        resultActions
            .print()
            .andExpectThat {
                statusEquals(HttpStatus.OK)
                jsonPathHasSize("_embedded.users", 1)
            }
            .andDo(document("users-find-by-age",
                requestParameters(parameterWithName("age").description("The age to search for.")),
                responseFields(userListFields()).and(ignoreLinks())
            ))
    }

    @Test
    fun `should return empty list when no user of a given age can be found`()  {
        // Given
        givenExistingUsers()

        // When
        perform(method = HttpMethod.GET, resourcePath = "/users/search/find-by-age?age=150")

        // Then
        resultActions
            .print()
            .andExpectThat {
                statusEquals(HttpStatus.OK)
                jsonPathEquals("_embedded.users", emptyList<User>())
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
                statusEquals(HttpStatus.CREATED)
                headerExists(HttpHeaders.LOCATION)
                jsonPathEquals("name", user.name)
                jsonPathEquals("age", user.age)
            }
            .andDo(document("users-create",
                requestFields(userFields()),
                responseFields(userFields()).and(ignoreLinks())
            ))
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
                statusEquals(HttpStatus.NO_CONTENT)
            }
            .andDo(document("users-delete"))
        assertThat(userRepository.count()).isEqualTo(existingEntities.size - 1L)
        assertThat(userRepository.findAll()).containsExactlyInAnyOrderElementsOf(existingEntities
                        .filter { it.id != user.id  }
                        .toList())
    }

    @Test
    fun `should update existing user`() {
        // Given
        givenExistingUsers()
        val user = existingEntities.first()
        val updatedUser = User(name = "Mina", age = 45)

        // When
        perform(method = HttpMethod.PUT, resourcePath = "/users/${user.id}", content = updatedUser)

        // Then
        resultActions
            .print()
            .andExpectThat {
                statusEquals(HttpStatus.OK)
                jsonPathEquals("name", "Mina")
                jsonPathEquals("age", 45)
                hateosContains("self")
            }
            .andDo(document("users-update",
                requestFields(userFields()),
                responseFields(userFields()).and(ignoreLinks())
            ))
    }

    fun givenExistingUsers() {
        existingEntities = listOf(
            userRepository.save(max()),
            userRepository.save(anna()),
            userRepository.save(john())
        )
    }

    fun userFields(): List<FieldDescriptor> =
        listOf(
            fieldWithPath("id").optional().ignored(),
            fieldWithPath("name").description("Name of the user."),
            fieldWithPath("age").description("Age of the user.")
        )

    fun userListFields(): List<FieldDescriptor> =
        listOf(
            fieldWithPath("_embedded.users[].name").description("Name of the user."),
            fieldWithPath("_embedded.users[].age").description("Age of the user."),
            fieldWithPath("_embedded.users[]._links.*.href").ignored()
        )

    fun max() = User(name = "Max", age = 33)
    fun anna() = User(name = "Anna", age = 28)
    fun john() = User(name = "John", age = 54)
}