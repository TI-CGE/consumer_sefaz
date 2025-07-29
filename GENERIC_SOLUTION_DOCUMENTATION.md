# Truly Generic SEFAZ API Consumer Solution

## 🎯 **Problem Solved**

The previous solution used hardcoded `instanceof` checks and specific mapping methods for each DTO type, which:
- ❌ Defeated the purpose of using generics
- ❌ Required modifying `ConsumoApiService` for every new endpoint
- ❌ Violated the open/closed principle
- ❌ Was not scalable

## ✅ **New Truly Generic Solution**

### **Core Architecture**

The new solution uses **reflection** and **leverages existing DTO methods** to create a truly generic system:

```java
// Generic method that works with ANY DTO extending EndpontSefaz
private T criarInstanciaGenerica(JsonNode itemNode, T mapper) {
    // 1. Create new instance using reflection
    T newInstance = (T) typeClass.getDeclaredConstructor().newInstance();
    
    // 2. Auto-map JSON fields to DTO setters using reflection
    mapearJsonParaDTO(itemNode, newInstance);
    
    // 3. Call DTO's own mapearCamposResposta() method
    newInstance.mapearCamposResposta();
    
    return newInstance;
}
```

### **Key Features**

#### **1. Automatic Field Mapping**
```java
private void mapearJsonParaDTO(JsonNode jsonNode, T dtoInstance) {
    // Iterates through ALL JSON fields automatically
    jsonNode.fieldNames().forEachRemaining(fieldName -> {
        // Tries multiple setter naming conventions:
        // - setFieldName (camelCase)
        // - setfield_name (snake_case)
        // - setFIELDNAME (uppercase)
        invocarSetterSeExistir(dtoInstance, dtoClass, fieldName, fieldValue);
    });
}
```

#### **2. Smart Type Conversion**
```java
private boolean tryInvokeSetterWithValue(T dtoInstance, Class<?> dtoClass, String setterName, JsonNode fieldValue) {
    // Automatically tries different parameter types:
    // - String.class (most common)
    // - Integer.class
    // - Long.class  
    // - Boolean.class
    // Returns true on first successful match
}
```

#### **3. Naming Convention Support**
```java
// Supports multiple JSON field naming conventions:
String[] possibleSetterNames = {
    "set" + capitalize(fieldName),                    // setFieldName
    "set" + capitalize(toCamelCase(fieldName)),       // setFieldName (from snake_case)  
    "set" + fieldName,                               // setfieldName
    "set" + fieldName.toUpperCase()                  // setFIELDNAME
};
```

## 🚀 **How to Add New Endpoints**

### **Step 1: Create New DTO (Only Required Step)**

```java
public class NovoEndpointDTO extends EndpontSefaz {
    
    // 1. Add your fields with getters/setters
    private String campoEspecifico;
    
    public String getCampoEspecifico() { return campoEspecifico; }
    public void setCampoEspecifico(String campoEspecifico) { this.campoEspecifico = campoEspecifico; }
    
    // 2. Implement required methods
    @Override
    protected void inicializarDadosEndpoint() {
        tabelaBanco = "sco.novo_endpoint";
        url = "https://api-transparencia.apps.sefaz.se.gov.br/novo/v1/dados";
        // ... other config
    }
    
    @Override
    public void mapearCamposResposta() {
        // ConsumoApiService already mapped JSON to setters automatically
        // This method just finalizes any custom mapping if needed
        camposResposta.put("campo_especifico", campoEspecifico);
    }
    
    // ... implement other abstract methods
}
```

### **Step 2: Add Configuration Bean**

```java
@Bean("novoEndpointConsumoApiService")
public ConsumoApiService<NovoEndpointDTO> novoEndpointConsumoApiService(
        RestTemplate restTemplate,
        AcessoTokenService acessoTokenService,
        JdbcTemplate jdbcTemplate,
        ValidacaoUtil<NovoEndpointDTO> validacaoUtil) {
    
    return new ConsumoApiService<>(
        restTemplate, acessoTokenService, jdbcTemplate, 
        validacaoUtil, NovoEndpointDTO.class
    );
}
```

### **Step 3: Create Controller**

```java
@RestController
@RequestMapping("/novo-endpoint")
public class NovoEndpointController {
    
    private final ConsumoApiService<NovoEndpointDTO> consumoApiService;
    
    public NovoEndpointController(
            @Qualifier("novoEndpointConsumoApiService") 
            ConsumoApiService<NovoEndpointDTO> consumoApiService) {
        this.consumoApiService = consumoApiService;
    }
    
    @GetMapping
    public List<NovoEndpointDTO> listar() {
        return consumoApiService.consumirPersistir(new NovoEndpointDTO());
    }
}
```

## 🎯 **Benefits of This Solution**

### **✅ Truly Generic**
- `ConsumoApiService` never needs modification
- Works with ANY DTO extending `EndpontSefaz`
- Uses reflection for automatic field mapping

### **✅ Follows SOLID Principles**
- **Open/Closed**: Open for extension, closed for modification
- **Single Responsibility**: Each DTO handles its own mapping logic
- **Dependency Inversion**: Depends on abstractions, not concretions

### **✅ Automatic JSON Mapping**
- Handles multiple naming conventions (camelCase, snake_case, etc.)
- Supports multiple data types (String, Integer, Long, Boolean)
- Graceful error handling for missing setters

### **✅ Leverages Existing Architecture**
- Uses existing `mapearCamposResposta()` method
- Maintains compatibility with current DTOs
- No breaking changes to existing code

### **✅ Scalable**
- Adding new endpoints requires only DTO creation
- No service layer modifications needed
- Consistent pattern across all endpoints

## 🧪 **Testing the Solution**

```bash
# Test existing endpoints (should work unchanged)
GET http://localhost:8083/unidade-gestora
GET http://localhost:8083/contratos-fiscais

# Test new endpoints (no ConsumoApiService changes needed)
GET http://localhost:8083/novo-endpoint
```

## 📋 **Migration Path**

1. **✅ Existing DTOs work unchanged** - No breaking changes
2. **✅ New DTOs use the generic approach** - Just create DTO + config
3. **✅ ConsumoApiService is now truly generic** - Never needs modification

This solution transforms the codebase from a hardcoded approach to a truly generic, scalable architecture that follows best practices and design principles.
