package mx.buap.fcc.clasificador

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ClasificadorWebApplication

fun main(args: Array<String>) {
	runApplication<ClasificadorWebApplication>(*args)
}
