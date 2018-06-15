package maandr.starters

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.*
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractRestIntegrationTest {

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    protected lateinit var mockMvc: MockMvc

    protected lateinit var resultActions: ResultActions

    @Before
    fun setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build()
    }

    protected fun perform(
        method: HttpMethod,
        resourcePath: String,
        content: Any? = null,
        contentType: MediaType = MediaType.APPLICATION_JSON,
        accept: MediaType = MediaType.APPLICATION_JSON
    ): ResultActions {
        val requestBuilder = getRequestBuilder(method, resourcePath)
            .contentType(contentType)
            .accept(accept)
            .apply { if(content != null) this.content(toJson(content)) }
        resultActions = mockMvc.perform(requestBuilder)
        return resultActions
    }

    private fun getRequestBuilder(method: HttpMethod, resourcePath: String) = when(method) {
        GET -> get(resourcePath)
        POST -> post(resourcePath)
        PUT -> put(resourcePath)
        DELETE -> delete(resourcePath)
        else -> get(resourcePath)
    }

    protected fun toJson(value: Any): String = objectMapper.writeValueAsString(value)
}