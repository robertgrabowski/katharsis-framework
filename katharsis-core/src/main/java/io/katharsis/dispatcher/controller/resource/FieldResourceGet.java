package io.katharsis.dispatcher.controller.resource;

import java.io.Serializable;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.katharsis.dispatcher.controller.HttpMethod;
import io.katharsis.dispatcher.controller.Response;
import io.katharsis.queryspec.internal.QueryAdapter;
import io.katharsis.repository.RepositoryMethodParameterProvider;
import io.katharsis.request.path.FieldPath;
import io.katharsis.request.path.JsonPath;
import io.katharsis.request.path.PathIds;
import io.katharsis.resource.Document;
import io.katharsis.resource.exception.ResourceFieldNotFoundException;
import io.katharsis.resource.field.ResourceField;
import io.katharsis.resource.internal.DocumentMapper;
import io.katharsis.resource.registry.RegistryEntry;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.resource.registry.repository.adapter.RelationshipRepositoryAdapter;
import io.katharsis.response.JsonApiResponse;
import io.katharsis.utils.Generics;
import io.katharsis.utils.parser.TypeParser;

public class FieldResourceGet extends ResourceIncludeField {

    public FieldResourceGet(ResourceRegistry resourceRegistry, ObjectMapper objectMapper, TypeParser typeParser, DocumentMapper documentMapper) {
        super(resourceRegistry, objectMapper, typeParser, documentMapper);
    }

    @Override
    public boolean isAcceptable(JsonPath jsonPath, String requestType) {
        return !jsonPath.isCollection()
                && FieldPath.class.equals(jsonPath.getClass())
                && HttpMethod.GET.name().equals(requestType);
    }

    @Override
    public Response handle(JsonPath jsonPath, QueryAdapter queryAdapter, RepositoryMethodParameterProvider
            parameterProvider, Document requestBody) {
        String resourceName = jsonPath.getResourceName();
        PathIds resourceIds = jsonPath.getIds();

        RegistryEntry<?> registryEntry = resourceRegistry.getEntry(resourceName);
        Serializable castedResourceId = getResourceId(resourceIds, registryEntry);
        String elementName = jsonPath.getElementName();
        ResourceField relationshipField = registryEntry.getResourceInformation().findRelationshipFieldByName(elementName);
        if (relationshipField == null) {
            throw new ResourceFieldNotFoundException(elementName);
        }

        Class<?> baseRelationshipFieldClass = relationshipField.getType();
       
        Class<?> relationshipFieldClass = Generics.getResourceClass(relationshipField.getGenericType(), baseRelationshipFieldClass);

        String relationshipResourceType = resourceRegistry.getResourceType(relationshipField.getElementType());
        
        RelationshipRepositoryAdapter relationshipRepositoryForClass = registryEntry
                .getRelationshipRepositoryForClass(relationshipFieldClass, parameterProvider);
        JsonApiResponse entities; 
		if (Iterable.class.isAssignableFrom(baseRelationshipFieldClass)) {
        	entities = relationshipRepositoryForClass.findManyTargets(castedResourceId, elementName, queryAdapter);
        } else {
        	entities = relationshipRepositoryForClass.findOneTarget(castedResourceId, elementName, queryAdapter);
        }
        Document responseDocument = documentMapper.toDocument(entities, queryAdapter, parameterProvider);

        return new Response(responseDocument, 200);
    }

    private Serializable getResourceId(PathIds resourceIds, RegistryEntry<?> registryEntry) {
        String resourceId = resourceIds.getIds().get(0);
        @SuppressWarnings("unchecked")
        Class<? extends Serializable> idClass = (Class<? extends Serializable>) registryEntry
                .getResourceInformation()
                .getIdField()
                .getType();
        return typeParser.parse(resourceId, idClass);
    }
}
