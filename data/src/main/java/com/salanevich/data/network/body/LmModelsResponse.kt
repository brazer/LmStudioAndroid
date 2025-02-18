package com.salanevich.data.network.body

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * {
 *     "data": [
 *         {
 *             "id": "meta-llama-3.1-8b-instruct",
 *             "object": "model",
 *             "owned_by": "organization_owner"
 *         },
 *         {
 *             "id": "nomic-ai/nomic-embed-text-v1.5-GGUF",
 *             "object": "model",
 *             "owned_by": "organization_owner"
 *         },
 *         {
 *             "id": "meta-llama-3.1-8b-instruct:2",
 *             "object": "model",
 *             "owned_by": "organization_owner"
 *         }
 *     ],
 *     "object": "list"
 * }
 */

@Serializable
data class LmModelsResponse(

    @SerialName("data")
    val data: List<LmModel>,

    @SerialName("object")
    val _object: String
)

@Serializable
data class LmModel(

    @SerialName("owned_by")
    val ownedBy: String,

    @SerialName("id")
    val id: String,

    @SerialName("object")
    val _object: String
)