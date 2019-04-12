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
package org.springframework.data.neo4j.core.context;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.neo4j.core.context.tracking.EntityTrackingStrategy;
import org.springframework.data.neo4j.core.mapping.Neo4jMappingContext;

/**
 * @author Gerrit Meier
 * @author Michael J. Simons
 */
class DefaultPersistenceContextTest {

	private PersistenceContext context;
	private EntityTrackingStrategy entityTrackingStrategy;

	@BeforeEach
	void setup() {
		entityTrackingStrategy = mock(EntityTrackingStrategy.class);
		when(entityTrackingStrategy.getObjectIdentifier(any()))
				.thenAnswer(invocation -> System.identityHashCode(invocation.getArguments()[0]));

		Neo4jMappingContext schema = new Neo4jMappingContext();
		schema.setInitialEntitySet(new HashSet<Class<?>>(Arrays.asList(Something.class)));
		schema.initialize();

		// override method to return a verifiable mock
		context = new DefaultPersistenceContext(schema) {
			@Override
			EntityTrackingStrategy getEntityTrackingStrategy() {
				return entityTrackingStrategy;
			}
		};
	}

	@Test
	void registerAddsEntityToTrackingStrategy() {
		context.register(new Something());

		verify(entityTrackingStrategy).track(any(), any());
	}

	@Test
	void registerTheSameObjectMultipleTimesCallsTrackJustOnce() {
		Something entity = new Something();
		context.register(entity);
		context.register(entity);

		verify(entityTrackingStrategy).track(any(), any());
	}

	@Test
	void registerTwoObjectOfTheSameTypeCallsTrackTwice() {
		Something entity1 = new Something();
		Something entity2 = new Something();

		context.register(entity1);
		context.register(entity2);

		verify(entityTrackingStrategy, times(2)).track(any(), any());
	}

	@Test
	void triggersDeltaCalculationOnDeltaCall() {
		Something entity = new Something();
		context.register(entity);
		context.getEntityChanges(entity);

		verify(entityTrackingStrategy).getAggregatedEntityChangeEvents(entity);
	}

	@Test
	void deregisterRemovesEntityFromTracking() {
		Something entity = new Something();
		context.register(entity);

		context.deregister(entity);

		verify(entityTrackingStrategy).untrack(entity);
	}

	@Test
	void deregisterUnknownEntityDoesNotCallUntrack() {
		Something entity = new Something();

		context.deregister(entity);

		verify(entityTrackingStrategy, never()).untrack(entity);
	}

	class Something {

		String value;
	}

}
