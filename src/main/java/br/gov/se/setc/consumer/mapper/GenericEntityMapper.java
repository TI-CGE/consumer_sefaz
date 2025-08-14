package br.gov.se.setc.consumer.mapper;

import br.gov.se.setc.consumer.contracts.EndpontSefaz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Mapper genérico para entidades que já possuem tipos consistentes entre DTO e Entity.
 * 
 * Este mapper usa reflection para mapear automaticamente campos de DTOs para entidades
 * quando os tipos já são compatíveis, eliminando a necessidade de mappers específicos
 * para entidades simples.
 * 
 * Usado para entidades como:
 * - BaseDespesaCredor
 * - BaseDespesaLicitacao  
 * - ContratoEmpenho
 * - Pagamento
 * - Empenho
 * - etc.
 */
@Component
public class GenericEntityMapper {
    
    private static final Logger logger = LoggerFactory.getLogger(GenericEntityMapper.class);
    
    /**
     * Mapeia um DTO para uma entidade usando reflection.
     * 
     * @param dto DTO source
     * @param entityClass Classe da entidade target
     * @param <T> Tipo do DTO (deve estender EndpontSefaz)
     * @param <E> Tipo da entidade
     * @return Entidade mapeada
     */
    public <T extends EndpontSefaz, E> E mapToEntity(T dto, Class<E> entityClass) {
        if (dto == null) {
            return null;
        }
        
        try {
            E entity = entityClass.getDeclaredConstructor().newInstance();
            
            // Usar o mapa de campos resposta do DTO para mapear para a entidade
            Map<String, Object> camposResposta = dto.getCamposResposta();
            
            for (Map.Entry<String, Object> entry : camposResposta.entrySet()) {
                String columnName = entry.getKey();
                Object value = entry.getValue();
                
                if (value != null) {
                    // Converter nome da coluna para nome do campo Java (snake_case → camelCase)
                    String fieldName = snakeCaseToCamelCase(columnName);
                    
                    // Tentar definir o valor no campo da entidade
                    setFieldValue(entity, fieldName, value);
                }
            }
            
            // Definir campos de auditoria se existirem
            setAuditFields(entity);
            
            return entity;
            
        } catch (Exception e) {
            logger.error("Erro ao mapear DTO {} para entidade {}: {}", 
                        dto.getClass().getSimpleName(), entityClass.getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Erro no mapeamento genérico", e);
        }
    }
    
    /**
     * Converte snake_case para camelCase.
     * Exemplo: cd_unidade_gestora → cdUnidadeGestora
     */
    private String snakeCaseToCamelCase(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }
        
        StringBuilder camelCase = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    camelCase.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    camelCase.append(c);
                }
            }
        }
        
        return camelCase.toString();
    }
    
    /**
     * Define o valor de um campo na entidade usando reflection.
     */
    private void setFieldValue(Object entity, String fieldName, Object value) {
        try {
            // Primeiro tentar encontrar o campo diretamente
            Field field = findField(entity.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.set(entity, value);
                return;
            }
            
            // Se não encontrou o campo, tentar usar setter
            String setterName = "set" + capitalize(fieldName);
            Method setter = findSetter(entity.getClass(), setterName, value.getClass());
            if (setter != null) {
                setter.invoke(entity, value);
                return;
            }
            
            logger.debug("Campo '{}' não encontrado na entidade {}", fieldName, entity.getClass().getSimpleName());
            
        } catch (Exception e) {
            logger.warn("Erro ao definir campo '{}' na entidade {}: {}", 
                       fieldName, entity.getClass().getSimpleName(), e.getMessage());
        }
    }
    
    /**
     * Encontra um campo na classe ou suas superclasses.
     */
    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }
    
    /**
     * Encontra um método setter na classe.
     */
    private Method findSetter(Class<?> clazz, String setterName, Class<?> paramType) {
        try {
            return clazz.getMethod(setterName, paramType);
        } catch (NoSuchMethodException e) {
            // Tentar com tipos compatíveis
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(setterName) && 
                    method.getParameterCount() == 1 &&
                    method.getParameterTypes()[0].isAssignableFrom(paramType)) {
                    return method;
                }
            }
        }
        return null;
    }
    
    /**
     * Define campos de auditoria se existirem na entidade.
     */
    private void setAuditFields(Object entity) {
        LocalDateTime now = LocalDateTime.now();
        
        // Tentar definir createdAt
        setFieldValue(entity, "createdAt", now);
        
        // Tentar definir updatedAt
        setFieldValue(entity, "updatedAt", now);
    }
    
    /**
     * Capitaliza a primeira letra de uma string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
