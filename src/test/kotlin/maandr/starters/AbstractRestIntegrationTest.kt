package maandr.starters

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.*
import org.springframework.http.MediaType
import org.springframework.restdocs.JUnitRestDocumentation
import org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel
import org.springframework.restdocs.hypermedia.HypermediaDocumentation.links
import org.springframework.restdocs.hypermedia.LinksSnippet
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
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

    // workaround to make junit rules work with kotlin
    // https://discuss.kotlinlang.org/t/how-can-i-use-rule/304
    val restDocumentation = JUnitRestDocumentation("build/api-snippets")
    @Rule fun restDocumentation(): JUnitRestDocumentation = restDocumentation

    @Before
    fun setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(
                documentationConfiguration(restDocumentation())
                    .uris()
                    .withHost("localhost")
                    .withPort(8080)
            )
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

    protected fun ignoreLinks(): FieldDescriptor =
        fieldWithPath("_links.*.*").optional().ignored()

    protected fun ignorePagination(): FieldDescriptor =
        fieldWithPath("page.*").optional().ignored()

    protected fun pagination(): List<FieldDescriptor> = listOf(
        fieldWithPath("page.size").optional().description("Amount of elements per page."),
        fieldWithPath("page.totalElements").optional().description("Total amount of results."),
        fieldWithPath("page.totalPages").optional().description("Total amount of pages."),
        fieldWithPath("page.number").optional().description("The current page.")
    )
}