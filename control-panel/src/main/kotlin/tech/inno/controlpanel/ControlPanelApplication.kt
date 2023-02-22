package tech.inno.controlpanel

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ControlPanelApplication

fun main(args: Array<String>) {
	runApplication<ControlPanelApplication>(*args)
}
