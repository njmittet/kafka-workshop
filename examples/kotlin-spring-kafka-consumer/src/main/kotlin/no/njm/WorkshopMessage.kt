package no.njm

import com.fasterxml.jackson.module.kotlin.readValue

data class WorkshopMessage(val id: String, val message: String)

fun String.toWorkshopMessage(): WorkshopMessage = objectMapper.readValue(this)
