/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mongodb.core.aggregation;

import java.util.Arrays;
import java.util.Collection;

import org.bson.Document;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Abstraction for
 * <a href="https://docs.mongodb.com/manual/meta/aggregation-quick-reference/#object-expression-operators">object
 * expression operators</a>.
 *
 * @author Christoph Strobl
 * @since 2.1
 */
public class ObjectOperators {

	/**
	 * Take the value referenced by given {@literal fieldReference}.
	 *
	 * @param fieldReference must not be {@literal null}.
	 * @return new instance of {@link ObjectOperatorFactory}.
	 */
	public static ObjectOperatorFactory valueOf(String fieldReference) {
		return new ObjectOperatorFactory(Fields.field(fieldReference));
	}

	/**
	 * Take the value provided by the given {@link AggregationExpression}.
	 *
	 * @param expression must not be {@literal null}.
	 * @return new instance of {@link ObjectOperatorFactory}.
	 */
	public static ObjectOperatorFactory valueOf(AggregationExpression expression) {
		return new ObjectOperatorFactory(expression);
	}

	/**
	 * @author Christoph Strobl
	 */
	public static class ObjectOperatorFactory {

		@Nullable private final Object value;

		/**
		 * Creates new {@link ObjectOperatorFactory} for given {@literal value}.
		 *
		 * @param value must not be {@literal null}.
		 */
		public ObjectOperatorFactory(Object value) {

			Assert.notNull(value, "Value must not be null!");
			this.value = value;
		}

		/**
		 * Creates new {@link MergeObjects aggregation expression} that takes the associated value and uses
		 * {@literal $mergeObjects} as an accumulator within the {@literal $group} stage. <br />
		 * <strong>NOTE:</strong> Requires MongoDB 4.0 or later.
		 *
		 * @return new instance of {@link MergeObjects}.
		 */
		public MergeObjects merge() {
			return MergeObjects.merge(value);
		}

		/**
		 * Creates new {@link MergeObjects aggregation expression} that takes the associated value and combines it with the
		 * given values into a single document. <br />
		 * <strong>NOTE:</strong> Requires MongoDB 4.0 or later.
		 *
		 * @return new instance of {@link MergeObjects}.
		 */
		public MergeObjects mergeWith(Object... values) {
			return merge().mergeWith(values);
		}

		/**
		 * Creates new {@link MergeObjects aggregation expression} that takes the associated value and combines it with the
		 * values of the given {@link Field field references} into a single document. <br />
		 * <strong>NOTE:</strong> Requires MongoDB 4.0 or later.
		 *
		 * @return new instance of {@link MergeObjects}.
		 */
		public MergeObjects mergeWithValuesOf(String... fieldReferences) {
			return merge().mergeWithValuesOf(fieldReferences);
		}

		/**
		 * Creates new {@link MergeObjects aggregation expression} that takes the associated value and combines it with the
		 * result values of the given {@link Aggregation expressions} into a single document. <br />
		 * <strong>NOTE:</strong> Requires MongoDB 4.0 or later.
		 *
		 * @return new instance of {@link MergeObjects}.
		 */
		public MergeObjects mergeWithValuesOf(AggregationExpression... expression) {
			return merge().mergeWithValuesOf(expression);
		}
	}

	/**
	 * {@link AggregationExpression} for {@code $mergeObjects} that combines multiple documents into a single document.
	 * <br />
	 * <strong>NOTE:</strong> Requires MongoDB 4.0 or later.
	 *
	 * @author Christoph Strobl
	 * @see <a href=
	 *      "https://docs.mongodb.com/manual/reference/operator/aggregation/mergeObjects/">https://docs.mongodb.com/manual/reference/operator/aggregation/mergeObjects/</a>
	 * @since 2.1
	 */
	public static class MergeObjects extends AbstractAggregationExpression {

		private MergeObjects(Object value) {
			super(value);
		}

		/**
		 * Creates new {@link MergeObjects aggregation expression} that takes given values and combines them into a single
		 * document. <br />
		 *
		 * @param values must not be {@literal null}.
		 * @return new instance of {@link MergeObjects}.
		 */
		public static MergeObjects merge(Object... values) {
			return new MergeObjects(Arrays.asList(values));
		}

		/**
		 * Creates new {@link MergeObjects aggregation expression} that takes the given {@link Field field references} and
		 * combines them into a single document.
		 *
		 * @param fieldReferences must not be {@literal null}.
		 * @return new instance of {@link MergeObjects}.
		 */
		public static MergeObjects mergeValuesOf(String... fieldReferences) {
			return merge(Arrays.stream(fieldReferences).map(Fields::field).toArray());
		}

		/**
		 * Creates new {@link MergeObjects aggregation expression} that takes the result of the given {@link Aggregation
		 * expressions} and combines them into a single document.
		 *
		 * @param expressions must not be {@literal null}.
		 * @return new instance of {@link MergeObjects}.
		 */
		public static MergeObjects mergeValuesOf(AggregationExpression... expressions) {
			return merge(expressions);
		}

		/**
		 * Creates new {@link MergeObjects aggregation expression} by adding the given {@link Field field references}.
		 *
		 * @param fieldReferences must not be {@literal null}.
		 * @return new instance of {@link MergeObjects}.
		 */
		public MergeObjects mergeWithValuesOf(String... fieldReferences) {
			return mergeWith(Arrays.stream(fieldReferences).map(Fields::field).toArray());
		}

		/**
		 * Creates new {@link MergeObjects aggregation expression} by adding the given {@link AggregationExpression
		 * expressions}.
		 *
		 * @param expression must not be {@literal null}.
		 * @return new instance of {@link MergeObjects}.
		 */
		public MergeObjects mergeWithValuesOf(AggregationExpression... expression) {
			return mergeWith(expression);
		}

		/**
		 * Creates new {@link MergeObjects aggregation expression} by adding the given values.
		 *
		 * @param values must not be {@literal null}.
		 * @return new instance of {@link MergeObjects}.
		 */
		public MergeObjects mergeWith(Object... values) {
			return new MergeObjects(append(Arrays.asList(values)));
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.data.mongodb.core.aggregation.AbstractAggregationExpression#toDocument(java.lang.Object, org.springframework.data.mongodb.core.aggregation.AggregationOperationContext)
		 */
		@Override
		public Document toDocument(Object value, AggregationOperationContext context) {
			return super.toDocument(potentiallyExtractSingleValue(value), context);
		}

		private Object potentiallyExtractSingleValue(Object value) {

			if (value instanceof Collection) {

				Collection<Object> collection = ((Collection) value);
				if (collection.size() == 1) {
					return collection.iterator().next();
				}
			}
			return value;
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.data.mongodb.core.aggregation.AbstractAggregationExpression#getMongoMethod()
		 */
		@Override
		protected String getMongoMethod() {
			return "$mergeObjects";
		}
	}
}