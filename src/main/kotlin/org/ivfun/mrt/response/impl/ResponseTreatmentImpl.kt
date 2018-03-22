package org.ivfun.mrt.response.impl

import org.ivfun.mrt.response.ResponseTreatment
import org.ivfun.mrt.sequence.impl.SequenceHelper
import org.ivfun.mrt.sequence.impl.SequenceService
import org.ivfun.mrt.sequence.obj.SequenceFriendlyId
import org.ivfun.mrt.validation.obj.Errors
import org.ivfun.mrt.validation.obj.Fields
import org.ivfun.mrt.validation.obj.Validation
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

/**
 * Created by: jonathan
 * DateTime: 2018-02-17 06:43
 **/
@Service
class ResponseTreatmentImpl<T>(val sequence: SequenceService) : ResponseTreatment<T>
{

    override
    fun findOne(repository: MongoRepository<T, String>, id: String): ResponseEntity<Any>
    {
        val toFindOne: Optional<T> = repository.findById(id)
        val mapOf: MutableMap<String, Any> = mutableMapOf()
        val result: T = toFindOne.get()
        if (!toFindOne.isPresent)
        {
            mapOf["id"] = id
            mapOf["not_found"] = true
            return Response(mapOf).get()
        }
        return Response(mapOf(Fields.getEntityName(result!!) to result)).get()
    }

    override
    fun findByFriendlyId(repository: MongoRepository<T, String>,id: Long): ResponseEntity<Any>
    {
        val any: T = Any() as T
        val example : Example<T> = Example.of(SequenceFriendlyId.setFriendlyId(any,id) as T)

        val toFindOne: Optional<T> = repository.findOne(example)
        val mapOf: MutableMap<String, Any> = mutableMapOf()
        val result: T = toFindOne.get()
        if (!toFindOne.isPresent)
        {
            mapOf["id"] = id
            mapOf["not_found"] = true
            return Response(mapOf).get()
        }
        return Response(mapOf(Fields.getEntityName(result!!) to result)).get()
    }

    override
    fun findAll(repository: MongoRepository<T, String>): ResponseEntity<Any>
    {
        val findAll: List<T> = repository.findAll()
        val mapOf: MutableMap<String, Any> = mutableMapOf()
        if (findAll.isEmpty())
        {
            mapOf["id"] = "\u2200"
            mapOf["not_found"] = true
            return Response(mapOf).get()
        }
        else
        {
            mapOf[Fields.getEntityName(findAll[0]!!).plus('s')] = findAll
        }
        return Response(mapOf).get()
    }

    override
    fun create(repository: MongoRepository<T, String>, any: T): ResponseEntity<Any>
    {
        val toSave: Response = Validation.toCreate(any!!)

        if (toSave.isValid())
        {
            val sequenceHelper: SequenceHelper = sequence.containsAutoIncrement(any)
            if (sequenceHelper.contains!!)
            {
                sequence.setNext(any, sequenceHelper)
            }
            return trySave(repository, any)
        }
        return toSave.get()
    }

    override
    fun update(repository: MongoRepository<T, String>, any: T): ResponseEntity<Any>
    {
        val toUpdate: Response = Validation.toUpdate(any!!)

        SequenceFriendlyId.setFriendlyId(repository, any)

        if (toUpdate.isValid())
        {
            return trySave(repository, any)
        }
        return toUpdate.get()
    }

    override
    fun delete(repository: MongoRepository<T, String>, id: String): ResponseEntity<Any>
    {
        val find: Optional<T> = repository.findById(id)
        val mapOf: MutableMap<String, Any> = mutableMapOf()
        mapOf["id"] = id
        mapOf["deleted"] = find.isPresent
        if (find.isPresent)
        {
            repository.delete(find.get())
        }
        return Response(mapOf).get()
    }

    private fun trySave(repository: MongoRepository<T, String>, any: T): ResponseEntity<Any>
    {
        return try
        {
            Response(mapOf(Fields.getEntityName(any!!) to repository.save(any))).get()
        }
        catch (e: Exception)
        {
            Errors.get(e.message!!, any!!)
        }
    }
}