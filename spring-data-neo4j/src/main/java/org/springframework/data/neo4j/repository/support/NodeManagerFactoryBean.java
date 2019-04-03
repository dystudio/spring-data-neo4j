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
package org.springframework.data.neo4j.repository.support;

import lombok.RequiredArgsConstructor;

import org.apiguardian.api.API;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.neo4j.core.NodeManager;
import org.springframework.data.neo4j.core.NodeManagerFactory;
import org.springframework.data.neo4j.core.mapping.MappingContextBasedScannerImpl;
import org.springframework.data.neo4j.core.mapping.Neo4jMappingContext;
import org.springframework.data.neo4j.core.transaction.ManagedTransactionProvider;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionUtils;

/**
 * This is a shim that integrates a {@link NodeManagerFactory} with Spring Datas infrastructure. It takes in the factory
 * that needs to be provided by the user and the mapping context provided by our infrastructure. The node manager factory
 * is than augment by our {@link Neo4jTransactionUtils} so that it doesn't use unmanaged native transaction, but managed
 * native transaction. Furthermore, the default noop scanner is replaced by a scanner based on our Neo4j mapping context.
 *
 * @author Gerrit Meier
 * @author Michael J. Simons
 */
@API(status = API.Status.INTERNAL, since = "1.0")
@RequiredArgsConstructor
public final class NodeManagerFactoryBean implements InitializingBean, FactoryBean<NodeManager> {

	private final NodeManagerFactory target;

	private final Neo4jMappingContext neo4jMappingContext;

	@Override
	public NodeManager getObject() {
		return SharedNodeManagerCreator.createSharedNodeManager(this.target);
	}

	@Override
	public Class<?> getObjectType() {
		return NodeManager.class;
	}

	@Override
	public void afterPropertiesSet() {

		// Use managed transactions
		target.setNativeTransactionProvider(new ManagedTransactionProvider());
		target.setScanner(new MappingContextBasedScannerImpl(this.neo4jMappingContext));
		target.initialize();
	}
}
