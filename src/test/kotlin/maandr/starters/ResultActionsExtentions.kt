package maandr.starters

import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers as matchers
import org.hamcrest.Matchers as hamcrest

fun ResultActions.print() = this.andDo(MockMvcResultHandlers.print())

fun ResultActions.responseStatus(status: HttpStatus) =
    this.andExpect(matchers.status().`is`(status.value()))

fun ResultActions.jsonPathExists(expression: String) =
    this.andExpect(matchers.jsonPath(expression, hamcrest.notNullValue()))

fun ResultActions.jsonPathEquals(expression: String, expectedValue: Any) =
    this.andExpect(matchers.jsonPath(expression, hamcrest.`is`(expectedValue)))

fun ResultActions.headerExists(headerName: String) =
    this.andExpect(matchers.header().exists(headerName))

fun ResultActions.headerEquals(headerName: String, expectedValue: Any) =
    this.andExpect(matchers.header().string(headerName, hamcrest.`is`(expectedValue)))

fun ResultActions.andExpectThat(build: ResultActions.() -> Unit): ResultActions {
    this.build()
    return this
}