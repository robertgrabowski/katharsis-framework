package io.katharsis.resource;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.katharsis.errorhandling.ErrorData;
import io.katharsis.resource.internal.DocumentDataDeserializer;
import io.katharsis.resource.internal.NullableSerializer;
import io.katharsis.utils.java.Nullable;

public class Document implements MetaContainer, LinksContainer {

	@JsonInclude(Include.NON_EMPTY)
	@JsonDeserialize(using = DocumentDataDeserializer.class)
	@JsonSerialize(using = NullableSerializer.class)
	private Nullable<Object> data = Nullable.empty();

	@JsonInclude(Include.NON_EMPTY)
	private List<Resource> included;

	@JsonInclude(Include.NON_EMPTY)
	private ObjectNode links;

	@JsonInclude(Include.NON_EMPTY)
	private ObjectNode meta;

	@JsonInclude(Include.NON_EMPTY)
	private List<ErrorData> errors;

	@Override
	public ObjectNode getLinks() {
		return links;
	}

	@Override
	public void setLinks(ObjectNode links) {
		this.links = links;
	}

	@Override
	public ObjectNode getMeta() {
		return meta;
	}

	@Override
	public void setMeta(ObjectNode meta) {
		this.meta = meta;
	}

	public Nullable<Object> getData() {
		return data;
	}

	public void setData(Nullable<Object> data) {
		if (data == null) {
			throw new NullPointerException("make use of Nullable instead of null");
		}
		this.data = data;
	}

	public List<Resource> getIncluded() {
		return included;
	}

	public void setIncluded(List<Resource> includes) {
		this.included = includes;
	}

	public List<ErrorData> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorData> errors) {
		this.errors = errors;
	}

	@JsonIgnore
	public boolean isMultiple() {
		return data.get() instanceof Collection;
	}

	@JsonIgnore
	public Nullable<Resource> getSingleData() {
		return (Nullable<Resource>) (Nullable) data;
	}

	@Override
	public int hashCode() {
		return Objects.hash(data, errors, included, links, meta);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Document))
			return false;
		Document other = (Document) obj;
		return Objects.equals(data, other.data) && Objects.equals(errors, other.errors) // NOSONAR
				&& Objects.equals(included, other.included) && Objects.equals(meta, other.meta) && Objects.equals(links, other.links);
	}

	@JsonIgnore
	public Nullable<List<Resource>> getCollectionData() {
		if (!data.isPresent()) {
			return Nullable.empty();
		}
		Object value = data.get();
		if (value == null) {
			return Nullable.of((List<Resource>) (List) Collections.emptyList());
		}
		if (!(value instanceof Iterable)) {
			return Nullable.of((Collections.singletonList((Resource) value)));
		}
		return Nullable.of((List<Resource>) value);
	}
}