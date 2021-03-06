package org.ivfun.mrt.sequence

import org.ivfun.mrt.sequence.impl.Sequence
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by: jonathan
 * DateTime: 2018-02-12 16:03
 **/
interface SequenceRepository : MongoRepository<Sequence, String>
{
    fun findByName(name: String): List<Sequence>
}