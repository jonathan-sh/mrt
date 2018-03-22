package org.ivfun.mrt.validation.obj

import org.ivfun.mrt.response.impl.Response
import org.ivfun.mrt.validation.annotation.IsRequiredToCreate
import org.ivfun.mrt.validation.annotation.IsRequiredToUpdate
import org.springframework.data.annotation.Id
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

/**
 * Created by: jonathan
 * DateTime: 2018-02-15 23:31
 **/
object Validation
{
    const val is_valid_key = "is_valid"

    fun toCreate(any: Any): Response
    {
        val list: ArrayList<String> = makeValidation(any, IsRequiredToCreate::class.java)
        val mapOf: MutableMap<String, Any> = mutableMapOf()
        mapOf[is_valid_key] = list.size == 0
        mapOf["action"] = "create"
        mapOf["missing_fields"] = list
        return Response(any, mapOf)
    }

    fun toUpdate(any: Any): Response
    {
        val up: ArrayList<String> = makeValidation(any, IsRequiredToUpdate::class.java)
        val id: ArrayList<String> = makeValidation(any, Id::class.java)
        val list: ArrayList<String> = ArrayList<String>()
        list.addAll(up)
        list.addAll(id)

        val mapOf: MutableMap<String, Any> = mutableMapOf()
        mapOf[is_valid_key] = list.size == 0
        mapOf["action"] = "update"
        mapOf["missing_fields"] = list
        return Response(any, mapOf)
    }

    private fun makeValidation(any: Any, annotation: Class<out Annotation>): ArrayList<String>
    {
        val field = ArrayList<String>()
        any.javaClass
                .kotlin
                .memberProperties
                .forEach { memberProperty ->
                    if (memberProperty.javaField!!.isAnnotationPresent(annotation))
                    {
                        val value: Any? = memberProperty.get(any)
                        if (isNullOrIsEmpty(value))
                        {
                            field.add(memberProperty.name)
                        }
                    }
                }
        return field
    }

    private fun isNullOrIsEmpty(value: Any?): Boolean
    {
        return value == null || value.toString().isEmpty()
    }
}