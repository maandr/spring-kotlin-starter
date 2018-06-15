package maandr.starters

import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


fun ResultActions.andPrint() = this.andDo(print())

fun ResultActions.responseStatus(status: HttpStatus) = this.andExpect(status().`is`(status.value()))

fun ResultActions.andExpectThat(build: ResultActions.() -> Unit): ResultActions {
    this.build()
    return this
}