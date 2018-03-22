package org.ivfun.mrt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class MrtApplication

fun main(args: Array<String>)
{
    runApplication<MrtApplication>(*args)
}
