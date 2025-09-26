# RitmoFit API - Colección de Postman

Esta colección contiene todos los endpoints de la API de RitmoFit para probar las funcionalidades de autenticación, catálogo de clases, reservas e historial de asistencias.

## 📋 Contenido de la Colección

### 1. **Autenticación**
- ✅ Solicitar código OTP por email
- ✅ Verificar código OTP (usuario nuevo y existente)
- ✅ Reenviar código OTP

### 2. **Perfil de Usuario**
- ✅ Obtener perfil del usuario autenticado
- ✅ Actualizar perfil (nombre y foto)

### 3. **Catálogo de Clases**
- ✅ Obtener todas las clases con paginación
- ✅ Filtrar clases por sede
- ✅ Filtrar clases por disciplina
- ✅ Obtener detalle de una clase específica
- ✅ Obtener lista de disciplinas
- ✅ Obtener lista de sedes
- ✅ Obtener lista de instructores

### 4. **Reservas**
- ✅ Crear nueva reserva
- ✅ Obtener reservas del usuario
- ✅ Obtener próximas reservas
- ✅ Cancelar reserva

### 5. **Historial de Asistencias**
- ✅ Obtener historial de asistencias
- ✅ Filtrar historial por fechas
- ✅ Registrar asistencia a clase
- ✅ Calificar asistencia (1-5 estrellas + comentario)
- ✅ Obtener asistencias calificables

### 6. **Flujo Completo de Prueba**
- ✅ Secuencia completa de 8 pasos para probar toda la funcionalidad

## 🚀 Instalación y Configuración

### Paso 1: Importar la Colección
1. Abre Postman
2. Haz clic en "Import"
3. Selecciona el archivo `RitmoFit_API_Collection.postman_collection.json`
4. Importa el archivo `RitmoFit_Local_Environment.postman_environment.json`

### Paso 2: Configurar el Entorno
1. Selecciona el entorno "RitmoFit - Local Environment" en el dropdown superior derecho
2. Verifica que la variable `baseUrl` esté configurada como `http://localhost:8080`

### Paso 3: Iniciar la Aplicación
1. Asegúrate de que la base de datos MySQL esté corriendo
2. Ejecuta la aplicación Spring Boot: `./mvnw spring-boot:run`
3. La aplicación estará disponible en `http://localhost:8080`

## 📊 Datos de Prueba Incluidos

La aplicación incluye datos de prueba realistas que se cargan automáticamente:

### **Disciplinas:**
- 🧘 Yoga
- 💪 Pilates
- 🔥 CrossFit
- 🚴 Spinning
- 💃 Zumba

### **Sedes:**
- 🏢 RitmoFit Centro (Av. Corrientes 1234, CABA)
- 🌳 RitmoFit Palermo (Av. Santa Fe 5678, Palermo, CABA)
- 🏛️ RitmoFit Recoleta (Av. Callao 9012, Recoleta, CABA)

### **Instructores:**
- 👩 María González (Yoga)
- 👨 Carlos Rodríguez (Pilates)
- 👩 Ana Martínez (Zumba, Spinning)
- 👨 Diego López (CrossFit)

### **Clases de Prueba:**
- 🧘 Yoga Matutino (8:00 AM, Centro)
- 💪 Pilates Core (9:30 AM, Palermo)
- 🔥 CrossFit Intenso (6:00 PM, Centro)
- 🚴 Spinning Power (7:30 PM, Recoleta)
- 💃 Zumba Fiesta (10:00 AM, Palermo)
- 🧘 Yoga Restaurativo (5:00 PM, Recoleta)

## 🔄 Cómo Probar el Flujo Completo

### Opción 1: Flujo Automático
1. Ve a la carpeta "6. Flujo Completo de Prueba"
2. Ejecuta los requests en orden (1-8)
3. El JWT token se guardará automáticamente en las variables

### Opción 2: Flujo Manual

#### 1. **Autenticación**
```
POST /api/auth/solicitar-codigo
Body: {"email": "test@ritmofit.com"}
```

```
POST /api/auth/verificar-codigo
Body: {"email": "test@ritmofit.com", "codigo": "123456", "nombre": "Usuario Test"}
```
> ⚠️ **Importante**: Copia el `token` de la respuesta a la variable `jwtToken`

#### 2. **Explorar Catálogo**
```
GET /api/clases/disciplinas
GET /api/clases/sedes
GET /api/clases?page=0&size=5
```

#### 3. **Crear Reserva**
```
POST /api/reservas
Headers: Authorization: Bearer {{jwtToken}}
Body: {"claseId": 1}
```

#### 4. **Ver Reservas**
```
GET /api/reservas
Headers: Authorization: Bearer {{jwtToken}}
```

#### 5. **Registrar Asistencia**
```
POST /api/asistencias/registrar?claseId=1&reservaId={{reservaId}}
Headers: Authorization: Bearer {{jwtToken}}
```

#### 6. **Calificar**
```
POST /api/asistencias/1/calificar
Headers: Authorization: Bearer {{jwtToken}}
Body: {"calificacion": 5, "comentario": "¡Excelente clase!"}
```

## 🔧 Variables Automáticas

La colección incluye scripts automáticos que:

- ✅ **Extraen el JWT token** de la respuesta de autenticación
- ✅ **Guardan el ID de usuario** automáticamente
- ✅ **Capturan el ID de reserva** para usar en otros requests
- ✅ **Configuran variables** según las respuestas

## 📝 Ejemplos de Respuestas Esperadas

### Autenticación Exitosa
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "test@ritmofit.com",
  "nombre": "Usuario Test",
  "nuevoUsuario": true,
  "mensaje": "Usuario creado exitosamente"
}
```

### Lista de Clases
```json
{
  "content": [
    {
      "id": 1,
      "nombre": "Yoga Matutino",
      "descripcion": "Clase de yoga para comenzar el día...",
      "fechaInicio": "2024-01-16T08:00:00",
      "fechaFin": "2024-01-16T09:00:00",
      "cupoMaximo": 20,
      "cupoActual": 5,
      "disponible": true,
      "disciplina": {...},
      "instructor": {...},
      "sede": {...}
    }
  ],
  "pageable": {...},
  "totalElements": 6
}
```

### Reserva Creada
```json
{
  "id": 1,
  "usuarioId": 1,
  "claseId": 1,
  "fechaReserva": "2024-01-15T10:30:00",
  "estado": "CONFIRMADA",
  "clase": {...}
}
```

## 🐛 Solución de Problemas

### Error 401 (No Autorizado)
- ✅ Verifica que el JWT token esté configurado en la variable `jwtToken`
- ✅ Asegúrate de que el token no haya expirado (válido por 24 horas)

### Error 400 (Datos Inválidos)
- ✅ Verifica el formato del JSON en el body
- ✅ Asegúrate de que los IDs existan en la base de datos

### Error 409 (Conflicto)
- ✅ Para reservas: La clase puede estar llena o ya tener una reserva activa
- ✅ Para asistencias: Puede que ya exista una asistencia registrada

### Error 500 (Error del Servidor)
- ✅ Verifica que la base de datos esté corriendo
- ✅ Revisa los logs de la aplicación Spring Boot

## 📚 Documentación Adicional

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/api-docs`

## 🎯 Casos de Prueba Recomendados

### Casos de Éxito
1. ✅ Registro de usuario nuevo con OTP
2. ✅ Login de usuario existente
3. ✅ Reserva de clase con cupo disponible
4. ✅ Cancelación de reserva antes de la clase
5. ✅ Registro de asistencia
6. ✅ Calificación de clase

### Casos de Error
1. ❌ Código OTP inválido o expirado
2. ❌ Reserva de clase sin cupo
3. ❌ Cancelación de reserva después de iniciada la clase
4. ❌ Calificación fuera del plazo de 24 horas
5. ❌ Acceso a endpoints sin autenticación

¡Disfruta probando la API de RitmoFit! 🏋️‍♀️💪
