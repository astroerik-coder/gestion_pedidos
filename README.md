# 🚀 Sistema de Gestión de Pedidos - Arquitectura de Microservicios

## 📋 Descripción General

Este sistema es una plataforma completa de gestión de pedidos para una distribuidora de productos de consumo masivo, implementada bajo una arquitectura de microservicios utilizando **Spring Boot** y **RabbitMQ** para la comunicación asíncrona entre servicios. El frontend está desarrollado con **Next.js** y **TypeScript**.

## 🏗️ Arquitectura del Sistema

### Microservicios Backend

El sistema está compuesto por los siguientes microservicios independientes:

#### 1. **Pedidos** (`backend/Pedidos/`)
- **Responsabilidad**: Gestión completa del ciclo de vida de pedidos
- **Funcionalidades**:
  - Creación y gestión de pedidos
  - Control de estados del pedido (Patrón State)
  - Integración con inventario para validación de stock
  - Comunicación con sistema de pagos
- **Tecnologías**: Spring Boot 2.3.0, Spring Data JPA, MySQL, RabbitMQ
- **Puerto**: 8080 (configurable)

#### 2. **Inventario** (`backend/Inventario/`)
- **Responsabilidad**: Gestión de productos y control de stock
- **Funcionalidades**:
  - CRUD de productos
  - Control de stock en tiempo real
  - Notificaciones de cambios de inventario
  - Integración con WebSocket para actualizaciones en vivo
- **Tecnologías**: Spring Boot 2.3.0, Spring Data JPA, MySQL, RabbitMQ
- **Puerto**: 8080 (configurable)

#### 3. **Despachos** (`backend/Despachos/`)
- **Responsabilidad**: Gestión de envíos y logística
- **Funcionalidades**:
  - Creación y seguimiento de despachos
  - Control de estados de envío (Patrón State)
  - Integración con sistema de pedidos
- **Tecnologías**: Spring Boot 2.3.0, Spring Data JPA, MySQL, RabbitMQ

#### 4. **Pagos** (`backend/Pagos/`)
- **Responsabilidad**: Procesamiento de transacciones financieras
- **Funcionalidades**:
  - Procesamiento de pagos
  - Gestión de estados de cobro
  - Notificaciones de pagos exitosos/fallidos
- **Tecnologías**: Spring Boot 2.3.0, Spring Data JPA, MySQL, RabbitMQ

#### 5. **Usuarios** (`backend/Usuarios/`)
- **Responsabilidad**: Autenticación y gestión de usuarios
- **Funcionalidades**:
  - Registro y login de usuarios
  - Gestión de perfiles
  - Autenticación JWT
- **Tecnologías**: Spring Boot 2.3.0, Spring Security, JWT, MySQL

#### 6. **Envíos** (`backend/apiRestEnvio/`)
- **Responsabilidad**: Gestión de envíos de productos
- **Funcionalidades**:
  - Control de estados de envío
  - Seguimiento de paquetes
- **Tecnologías**: Spring Boot 2.3.0, Spring Data JPA, MySQL

#### 7. **WebSocket** (`backend/Websocket/`)
- **Responsabilidad**: Comunicación en tiempo real
- **Funcionalidades**:
  - Notificaciones en vivo de cambios de inventario
  - Actualizaciones de estado de pedidos
  - Comunicación bidireccional cliente-servidor

### Frontend

#### **Aplicación Web** (`frontend/`)
- **Tecnologías**: Next.js 15, React 18, TypeScript, Tailwind CSS
- **Funcionalidades**:
  - Dashboard administrativo
  - Interfaz de cliente
  - Sistema de autenticación
  - Gestión de pedidos, inventario y despachos
  - Seguimiento en tiempo real
  - Sistema de carrito de compras

## 🔄 Comunicación entre Microservicios

### RabbitMQ como Message Broker

El sistema utiliza **RabbitMQ** como columna vertebral para la comunicación asíncrona entre microservicios:

#### **Exchange Principal**: `pedido.exchange`
- **Tipo**: Topic Exchange
- **Propósito**: Centralizar todos los eventos del sistema

#### **Colas Principales**:
- `producto.consultar.queue` - Consultas de productos
- `producto.info.queue` - Respuestas de información de productos
- `stock.disminuir.queue` - Solicitudes de disminución de stock
- `pedido.listo_para_pagar.queue` - Pedidos listos para pago
- `pago.exitoso.queue` - Notificaciones de pagos exitosos

#### **Routing Keys**:
- `producto.consultar` - Solicitar información de producto
- `producto.info` - Información de producto
- `stock.disminuir` - Disminuir stock
- `pedido.listo_para_pagar` - Pedido listo para pago
- `pago.exitoso` - Pago exitoso

### Flujo de Comunicación

```
Cliente → Pedidos → Inventario (consulta stock)
                ↓
            Despachos (crea envío)
                ↓
            Pagos (procesa pago)
                ↓
            WebSocket (notifica cambios)
```

## 🗄️ Base de Datos

### MySQL como Base de Datos Principal
- **Versión**: MySQL 8.0
- **Configuración**: Docker container con persistencia de datos
- **Bases de datos separadas por microservicio**:
  - `pedidos_db` - Gestión de pedidos
  - `productos_db` - Inventario de productos
  - `despachos_db` - Gestión de envíos
  - `cobros_db` - Transacciones financieras
  - `autenticacion_db` - Usuarios y autenticación

### Docker Compose
```yaml
# Database/docker-compose.yml
services:
  db:
    image: mysql:8.0
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./mysql/initdb:/docker-entrypoint-initdb.d
```

## 🚀 Despliegue y Ejecución

### Prerrequisitos
- Java 11 o superior
- Maven 3.6+
- Docker y Docker Compose
- Node.js 18+ (para frontend)

### Iniciar Infraestructura
```bash
# Iniciar MySQL
cd Database
docker-compose up -d

# Iniciar RabbitMQ
docker-compose -f docker-compose-rabbitmq.yml up -d
```

### Ejecutar Microservicios
```bash
# Pedidos
cd backend/Pedidos
./mvnw spring-boot:run

# Inventario
cd backend/Inventario
./mvnw spring-boot:run

# Despachos
cd backend/Despachos
./mvnw spring-boot:run

# Pagos
cd backend/Pagos
./mvnw spring-boot:run

# Usuarios
cd backend/Usuarios
./mvnw spring-boot:run

# WebSocket
cd backend/Websocket
./mvnw spring-boot:run
```

### Ejecutar Frontend
```bash
cd frontend
npm install
npm run dev
```

## 📚 Documentación de APIs

### Swagger/OpenAPI
Cada microservicio incluye documentación automática de APIs:
- **Pedidos**: `http://localhost:8080/swagger-ui.html`
- **Inventario**: `http://localhost:8080/swagger-ui.html`
- **Despachos**: `http://localhost:8080/swagger-ui.html`
- **Pagos**: `http://localhost:8080/swagger-ui.html`

### Endpoints Principales

#### Pedidos
- `GET /api/pedidos` - Listar pedidos
- `POST /api/pedidos` - Crear pedido
- `PUT /api/pedidos/{id}/avanzar` - Avanzar estado
- `PUT /api/pedidos/{id}/cancelar` - Cancelar pedido

#### Inventario
- `GET /api/productos` - Listar productos
- `POST /api/productos` - Crear producto
- `PUT /api/productos/{id}/disminuir-stock` - Disminuir stock
- `PUT /api/productos/{id}/liberar-stock` - Liberar stock

#### Despachos
- `GET /api/despachos` - Listar despachos
- `POST /api/despachos` - Crear despacho
- `PUT /api/despachos/{id}/estado` - Cambiar estado

## 🔐 Seguridad

### Autenticación JWT
- **Implementación**: Spring Security con JWT
- **Almacenamiento**: Tokens en localStorage del frontend
- **Protección**: Endpoints protegidos con anotaciones `@PreAuthorize`

### CORS
- Configuración habilitada para desarrollo
- Orígenes permitidos configurados por microservicio

## 🎯 Patrones de Diseño Implementados

### 1. **Patrón State**
- **Pedidos**: Estados de pedido (Pendiente, Confirmado, En Preparación, etc.)
- **Despachos**: Estados de envío (En Preparación, Listo para Envío, En Tránsito, etc.)
- **Pagos**: Estados de cobro (Pendiente, Pagado, Fallido)

### 2. **Patrón Repository**
- Acceso a datos abstracto en todos los microservicios
- Implementación con Spring Data JPA

### 3. **Patrón Service**
- Lógica de negocio encapsulada en servicios
- Separación clara de responsabilidades

### 4. **Event-Driven Architecture**
- Comunicación asíncrona entre microservicios
- Desacoplamiento de servicios

## 📱 Características del Frontend

### Componentes Principales
- **Dashboard Administrativo**: Gestión completa del sistema
- **Interfaz de Cliente**: Creación y seguimiento de pedidos
- **Sistema de Autenticación**: Login y registro de usuarios
- **Gestión de Inventario**: CRUD de productos con validaciones
- **Seguimiento de Pedidos**: Estado en tiempo real
- **Sistema de Carrito**: Gestión de productos antes de confirmar pedido

### Tecnologías UI
- **Tailwind CSS**: Framework de utilidades CSS
- **Radix UI**: Componentes accesibles y personalizables
- **React Hook Form**: Manejo de formularios
- **Zod**: Validación de esquemas
- **Recharts**: Gráficos y visualizaciones

## 🔧 Configuración y Personalización

### Variables de Entorno
Cada microservicio incluye `application.properties` configurable:
- Puertos de servicio
- Configuración de base de datos
- Configuración de RabbitMQ
- Configuración de JWT

### Logging
- Logging configurado con Spring Boot
- Niveles configurables por microservicio

## 🚨 Monitoreo y Observabilidad

### Health Checks
- Endpoints de salud en cada microservicio
- Verificación de conectividad a base de datos
- Verificación de conectividad a RabbitMQ

### Logs
- Logs estructurados para debugging
- Trazabilidad de eventos entre microservicios

## 🔄 Flujo de Trabajo Completo

### 1. **Creación de Pedido**
```
Cliente → Frontend → Microservicio Pedidos → Validación Inventario → Confirmación
```

### 2. **Procesamiento de Pago**
```
Pedidos → Microservicio Pagos → Procesamiento → Notificación de Resultado
```

### 3. **Preparación de Despacho**
```
Pedido Confirmado → Microservicio Despachos → Creación de Envío → Notificación
```

### 4. **Seguimiento en Tiempo Real**
```
Cambios de Estado → WebSocket → Frontend → Actualización de UI
```

## 🛠️ Herramientas de Desarrollo

### Backend
- **IDE**: IntelliJ IDEA, Eclipse, VS Code
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test
- **API Testing**: Postman, Insomnia

### Frontend
- **IDE**: VS Code, WebStorm
- **Package Manager**: npm
- **Build Tool**: Next.js
- **Testing**: Jest, React Testing Library

## 📈 Escalabilidad y Mantenimiento

### Características de Escalabilidad
- **Microservicios independientes**: Escalado individual por servicio
- **Base de datos separada**: Aislamiento de datos
- **Message broker**: Desacoplamiento y balanceo de carga
- **Stateless services**: Fácil replicación

### Estrategias de Mantenimiento
- **Versionado de APIs**: Compatibilidad hacia atrás
- **Documentación automática**: Swagger/OpenAPI
- **Logging centralizado**: Trazabilidad de operaciones
- **Health checks**: Monitoreo de servicios

## 🤝 Contribución

### Estructura del Proyecto
```
gestion_pedidos/
├── backend/           # Microservicios Spring Boot
├── frontend/          # Aplicación Next.js
├── Database/          # Configuración de bases de datos
└── docs/             # Documentación adicional
```

### Convenciones de Código
- **Java**: Convenciones de Spring Boot
- **TypeScript**: ESLint + Prettier
- **Commits**: Conventional Commits
- **Branches**: Git Flow

## 📞 Soporte y Contacto

Para soporte técnico o consultas sobre el sistema:
- **Equipo de Desarrollo**: Equipo de Desarrollo
- **Documentación**: Swagger UI en cada microservicio
- **Issues**: Sistema de tickets del proyecto

---

**Desarrollado con ❤️ usando Spring Boot, RabbitMQ y Next.js**
