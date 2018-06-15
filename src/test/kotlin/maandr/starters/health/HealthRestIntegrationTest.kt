package maandr.starters.health

import maandr.starters.AbstractRestIntegrationTest
import org.junit.Test
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class HealthRestIntegrationTest : AbstractRestIntegrationTest() {

    @Test
    fun `should respond with 200 OK`() {
        perform(method = HttpMethod.GET, resourcePath = "/health")
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}