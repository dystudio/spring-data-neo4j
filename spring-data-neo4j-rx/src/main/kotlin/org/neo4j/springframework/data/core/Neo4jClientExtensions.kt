/*
 * Copyright (c) 2019 "Neo4j,"
 * Neo4j Sweden AB [https://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.springframework.data.core

/**
 * Extension for [Neo4jClient.RunnableSpec.in] providing an `inDatabase` alias since `in` is a reserved keyword in Kotlin.
 *
 * @author Michael J. Simons
 * @since 1.0
 */
fun Neo4jClient.RunnableSpec.inDatabase(targetDatabase: String): Neo4jClient.RunnableSpecTightToDatabase = `in`(targetDatabase)

/**
 * Extension for [Neo4jClient.OngoingDelegation.in] providing an `inDatabase` alias since `in` is a reserved keyword in Kotlin.
 *
 * @author Michael J. Simons
 * @since 1.0
 */
fun <T : Any?> Neo4jClient.OngoingDelegation<T>.inDatabase(targetDatabase: String): Neo4jClient.RunnableDelegation<T> = `in`(targetDatabase)
