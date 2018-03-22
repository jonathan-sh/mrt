package org.ivfun.mrt.sequence.obj

import org.springframework.data.domain.Example
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

object SequenceFriendlyId
{
    fun <T> setFriendlyId(repository: MongoRepository<T, String>, any: T)
    {
        val findOne: Optional<T> = repository.findById(getId(any!!))
        setByObj(any, findOne.get()!!)
    }

    private fun setByObj(any: Any, findOne: Any)
    {
        val get: Long = getFriendlyId(findOne)

        any.javaClass
                .kotlin
                .memberProperties
                .forEach { member ->
                    if (member.name == "friendly_id")
                    {
                        member.javaField!!.isAccessible = true
                        member.javaField!!.set(any, get)
                    }
                }
    }

    fun <T> setFriendlyId(any: T, friendlyId: Long): Any
    {
      return setFriendlyIdByValue(any!!, friendlyId)
    }

    private fun  setFriendlyIdByValue(any: Any, friendlyId: Long): Any
    {
        any.javaClass
                .kotlin
                .memberProperties
                .forEach { member ->
                    if (member.name == "friendly_id")
                    {
                        member.javaField!!.isAccessible = true
                        member.javaField!!.set(any, friendlyId)
                    }
                }
        return any
    }

    private fun getId(any: Any): String
    {
        var get: String = ""

        any.javaClass.kotlin.memberProperties.stream().forEach { k ->
            if (k.name == "id")
            {
                get = k.get(any) as String
            }
        }

        return get
    }


    private fun getFriendlyId(any: Any): Long
    {
        var get: Long = 0

        any.javaClass.kotlin.memberProperties.stream().forEach { k ->
            if (k.name == "friendly_id")
            {
                get = k.get(any) as Long
            }
        }

        return get
    }

}

