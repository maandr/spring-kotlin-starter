package maandr.starters

import org.springframework.http.HttpStatus
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import javax.xml.transform.Result
import org.springframework.test.web.servlet.result.MockMvcResultMatchers as matchers
import org.hamcrest.Matchers as hamcrest

fun ResultActions.print() =
    this.andDo(MockMvcResultHandlers.print())

fun ResultActions.statusEquals(status: HttpStatus) =
    this.andExpect(matchers.status().`is`(status.value()))

fun ResultActions.jsonPathExists(expression: String) =
    this.andExpect(matchers.jsonPath(expression, hamcrest.notNullValue()))

fun ResultActions.jsonPathEquals(expression: String, expectedValue: Any) =
    this.andExpect(matchers.jsonPath(expression, hamcrest.`is`(expectedValue)))

fun ResultActions.jsonPathHasSize(expression: String, size: Int) =
    this.andExpect(matchers.jsonPath(expression, hamcrest.hasSize<Int>(size)))

fun ResultActions.headerExists(headerName: String) =
    this.andExpect(matchers.header().exists(headerName))

fun ResultActions.headerEquals(headerName: String, expectedValue: Any) =
    this.andExpect(matchers.header().string(headerName, hamcrest.`is`(expectedValue)))

fun ResultActions.hateosContains(expression: String): ResultActions =
    this.jsonPathExists("_links")
        .jsonPathExists("_links.$expression")

fun ResultActions.andExpectThat(build: ResultActions.() -> Unit): ResultActions {
    this.build()
    return this
}