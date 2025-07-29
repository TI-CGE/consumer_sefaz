# Spring Boot Startup Test Guide

## ✅ **Issues Fixed:**

### **1. Removed Conflicting RestTemplate Bean**
- **Problem**: `ApiConfig.java` was creating a duplicate RestTemplate bean
- **Solution**: ✅ Removed `src/main/java/br/gov/se/setc/consumer/confg/ApiConfig.java`

### **2. Fixed Bean Configuration**
- **Problem**: Circular dependencies and conflicting bean definitions
- **Solution**: ✅ Centralized all bean creation in `SefazConsumerConfig.java`

### **3. Removed @Service Annotation**
- **Problem**: `ConsumoApiService` had both `@Service` and manual bean creation
- **Solution**: ✅ Removed `@Service` annotation, using only configuration beans

### **4. Added Unique Bean Names**
- **Problem**: Potential bean name conflicts
- **Solution**: ✅ Named RestTemplate bean as `"sefazRestTemplate"`

## 🚀 **Testing Steps:**

### **Step 1: Verify Configuration**
```bash
# Check for compilation errors
mvn clean compile
```

### **Step 2: Start Application**
```bash
# Start the Spring Boot application
mvn spring-boot:run
```

### **Step 3: Test Health Endpoints**
```bash
# Basic health check
curl http://localhost:8083/health

# Token service test
curl http://localhost:8083/health/token-test

# Endpoint configuration test
curl http://localhost:8083/unidade-gestora/test
```

## 📋 **Expected Startup Sequence:**

1. **✅ Spring Boot Context Initialization**
2. **✅ Database Connection Established**
3. **✅ RestTemplate Bean Created** (`sefazRestTemplate`)
4. **✅ AcessoTokenService Bean Created**
5. **✅ ValidacaoUtil Beans Created**
6. **✅ ConsumoApiService Beans Created**
7. **✅ Controllers Registered**
8. **✅ Application Ready on Port 8083**

## 🔍 **Troubleshooting:**

### **If Application Still Fails to Start:**

1. **Check for Port Conflicts:**
   ```bash
   netstat -an | findstr :8083
   ```

2. **Verify Database Connection:**
   - Ensure PostgreSQL is running on `172.28.65.26:5432`
   - Test connection with credentials: `gideon/setc@2025`

3. **Check Logs for Specific Errors:**
   - Look for bean creation errors
   - Check for database connection issues
   - Verify no circular dependencies remain

### **Success Indicators:**
- ✅ No "APPLICATION FAILED TO START" messages
- ✅ "Started ConsumerSefazApplication" message appears
- ✅ Port 8083 is listening
- ✅ Health endpoint responds with HTTP 200

### **Test Endpoints:**
- `GET /health` - Application health check
- `GET /health/token-test` - SEFAZ token service test
- `GET /unidade-gestora/test` - Endpoint configuration test
- `GET /unidade-gestora` - Full API consumption (requires SEFAZ connectivity)

## 🎯 **Configuration Summary:**

- **RestTemplate**: Single `@Primary` bean named `sefazRestTemplate`
- **ConsumoApiService**: Two specific beans for different DTOs
- **ValidacaoUtil**: Separate beans for each DTO type
- **Bean Overriding**: Disabled to prevent conflicts
- **Lazy Initialization**: Disabled for faster error detection
