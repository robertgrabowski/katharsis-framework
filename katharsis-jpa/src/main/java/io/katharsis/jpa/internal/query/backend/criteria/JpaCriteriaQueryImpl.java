package io.katharsis.jpa.internal.query.backend.criteria;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import io.katharsis.jpa.internal.query.AbstractJpaQueryImpl;
import io.katharsis.jpa.internal.query.ComputedAttributeRegistryImpl;
import io.katharsis.jpa.query.criteria.JpaCriteriaQuery;
import io.katharsis.meta.MetaLookup;

public class JpaCriteriaQueryImpl<T> extends AbstractJpaQueryImpl<T, JpaCriteriaQueryBackend<T>>
		implements JpaCriteriaQuery<T> {

	public JpaCriteriaQueryImpl(MetaLookup metaLookup, EntityManager em, Class<T> clazz,
			ComputedAttributeRegistryImpl virtualAttrs) {
		super(metaLookup, em, clazz, virtualAttrs);
	}

	public JpaCriteriaQueryImpl(MetaLookup metaLookup, EntityManager em, Class<?> clazz,
			ComputedAttributeRegistryImpl virtualAttrs, String attrName, List<?> entityIds) {
		super(metaLookup, em, clazz, virtualAttrs, attrName, entityIds);
	}

	public CriteriaQuery<T> buildQuery() {
		return buildExecutor().getQuery();
	}

	@Override
	public JpaCriteriaQueryExecutorImpl<T> buildExecutor() {
		return (JpaCriteriaQueryExecutorImpl<T>) super.buildExecutor();
	}

	@Override
	protected JpaCriteriaQueryBackend<T> newBackend() {
		return new JpaCriteriaQueryBackend<>(this, em, clazz, parentEntityClass, parentAttr, parentIdSelection);
	}

	@Override
	protected JpaCriteriaQueryExecutorImpl<T> newExecutor(JpaCriteriaQueryBackend<T> ctx, int numAutoSelections, Map<String, Integer> selectionBindings) {
		return new JpaCriteriaQueryExecutorImpl<>(em, meta, ctx.getCriteriaQuery(), numAutoSelections, selectionBindings);
	}
}
