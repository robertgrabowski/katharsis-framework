package io.katharsis.errorhandling.mapper;

import java.util.Set;

import io.katharsis.errorhandling.ErrorResponse;
import io.katharsis.utils.java.Optional;

public final class ExceptionMapperRegistry {

    private final Set<ExceptionMapperType> exceptionMappers;

    ExceptionMapperRegistry(Set<ExceptionMapperType> exceptionMappers) {
        this.exceptionMappers = exceptionMappers;
    }

    Set<ExceptionMapperType> getExceptionMappers() {
        return exceptionMappers;
    }

    public Optional<JsonApiExceptionMapper> findMapperFor(Class<? extends Throwable> exceptionClass) {
        int currentDistance = Integer.MAX_VALUE;
        JsonApiExceptionMapper closestExceptionMapper = null;
        for (ExceptionMapperType mapperType : exceptionMappers) {
            int tempDistance = getDistanceBetweenExceptions(exceptionClass, mapperType.getExceptionClass());
            if (tempDistance < currentDistance) {
                currentDistance = tempDistance;
                closestExceptionMapper = mapperType.getExceptionMapper();
                if (currentDistance == 0) {
                    break;
                }
            }
        }
        return Optional.ofNullable(closestExceptionMapper);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Optional<ExceptionMapper<?>> findMapperFor(ErrorResponse errorResponse) {
		int currentDepth = -1;
		ExceptionMapper closestExceptionMapper = null;
		    
		for (ExceptionMapperType mapperType : exceptionMappers) {
			JsonApiExceptionMapper mapperObj = mapperType.getExceptionMapper();
			if(mapperObj instanceof ExceptionMapper){
				ExceptionMapper mapper = (ExceptionMapper) mapperObj;
				boolean accepted = mapper.accepts(errorResponse);
				if(accepted){
					// the exception with the most super types is chosen
					int tempDepth = countSuperTypes(mapperType.getExceptionClass());
		            if (tempDepth > currentDepth) {
		                currentDepth = tempDepth;
		                closestExceptionMapper = mapper;
		            }
				}
			}
		}
		return (Optional)Optional.ofNullable(closestExceptionMapper);
	}

    int getDistanceBetweenExceptions(Class<?> clazz, Class<?> mapperTypeClazz) {
        int distance = 0;
        Class<?> superClazz = clazz;
        if (!mapperTypeClazz.isAssignableFrom(clazz))
            return Integer.MAX_VALUE;

        while (superClazz != mapperTypeClazz) {
            superClazz = superClazz.getSuperclass();
            distance++;
        }
        return distance;
    }
    
    int countSuperTypes(Class<?> clazz) {
        int count = 0;
        Class<?> superClazz = clazz;
        while (superClazz != Object.class) {
            superClazz = superClazz.getSuperclass();
            count++;
        }
        return count;
    }
}