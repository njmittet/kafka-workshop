package no.njm

import com.fasterxml.jackson.annotation.JsonInclude
import no.njm.WorkshopMessageProducer.WorkshopMessageException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class WorkshopMessageRestApi(
    private val workshopMessageProducer: WorkshopMessageProducer
) {

    @ExceptionHandler(WorkshopMessageException::class)
    fun handleException(): ResponseEntity<Any> {
        return ResponseEntity<Any>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @PostMapping("/message")
    fun sendMessage(@RequestBody message: Message?): ResponseEntity<Any> {
        message?.let {
            workshopMessageProducer.sendSync(message.toWorkshopMessage(UUID.randomUUID().toString()))
            return ResponseEntity<Any>(HttpStatus.ACCEPTED)
        }
        return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/message/async")
    fun sendAsyncMessage(@RequestBody message: Message?): ResponseEntity<Any> {
        message?.let {
            workshopMessageProducer.sendAsync(message.toWorkshopMessage(UUID.randomUUID().toString()))
            return ResponseEntity<Any>(HttpStatus.ACCEPTED)
        }
        return ResponseEntity<Any>(HttpStatus.BAD_REQUEST)
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Message(
        var message: String,
    )

    private fun Message.toWorkshopMessage(uuid: String): WorkshopMessage = WorkshopMessage(uuid, message)
}