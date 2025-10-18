# MediSupply G12 - Mobile Application

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

## 📱 Descripción

MediSupply G12 es una aplicación móvil desarrollada en Android que facilita la gestión de suministros médicos. La aplicación permite a diferentes tipos de usuarios (vendedores, repartidores y clientes) interactuar con un sistema de gestión de inventario, pedidos y entregas.

## 👥 Equipo de Desarrollo

**MISO GRUPO 12:**
- Manuel Sanchez
- Jairo Reyes
- Nicolas Malagon
- Sergio Perez

**Curso**: Proyecto Final 2 - MISO 2025-15  

## ✨ Características Principales

### 🔄 Selector de Ambiente
- **Desarrollo**: Conecta a servidores locales para testing
- **Producción**: Conecta a la API en producción (`api.medisupplyg4.online`)
- **Persistencia**: Recuerda la configuración seleccionada

### 👥 Roles de Usuario
- **Vendedor**: Gestión de rutas de visitas y creación de pedidos
- **Repartidor**: Visualización de rutas de entrega por día/semana/mes
- **Cliente**: Funcionalidad en desarrollo

### 🌐 Internacionalización
- **Español**: Idioma por defecto
- **Inglés**: Soporte completo
- **Cambio dinámico**: Sin reiniciar la aplicación

### 📦 Gestión de Productos
- **Filtro inteligente**: Solo muestra productos con stock disponible
- **Búsqueda avanzada**: Por nombre, descripción o categoría
- **Información detallada**: Precios, inventario y categorías

### 🚚 Gestión de Entregas
- **Rutas organizadas**: Por períodos (día, semana, mes)
- **Información completa**: Cliente, dirección, teléfono y fecha
- **Navegación fluida**: Scroll automático al cambiar períodos

### ⚠️ Manejo de Errores
- **Mensajes detallados**: Errores específicos del backend
- **Información de stock**: Detalles sobre productos sin inventario
- **Experiencia de usuario**: Mensajes claros y accionables

## 🛠️ Tecnologías Utilizadas

### Frontend
- **Kotlin**: Lenguaje principal
- **Jetpack Compose**: UI moderna y declarativa
- **Material Design 3**: Componentes de diseño consistentes
- **Navigation Compose**: Navegación entre pantallas

### Backend Integration
- **Retrofit**: Cliente HTTP type-safe
- **Gson**: Serialización/deserialización JSON
- **OkHttp**: Cliente HTTP con interceptores
- **Coroutines**: Programación asíncrona

### Arquitectura
- **MVVM**: Model-View-ViewModel
- **LiveData**: Observación de datos reactiva
- **Repository Pattern**: Abstracción de datos
- **Dependency Injection**: Gestión de dependencias

### Testing
- **JUnit**: Pruebas unitarias
- **MockK**: Mocking para Kotlin
- **Jacoco**: Cobertura de código
- **Espresso**: Pruebas de UI (en desarrollo)

## 📋 Requisitos del Sistema

- **Android**: API 28+ (Android 9.0+)
- **RAM**: Mínimo 2GB recomendado
- **Almacenamiento**: 50MB para la aplicación
- **Conexión**: Internet para funcionalidad completa

## 🚀 Instalación

### Opción 1: APK Pre-compilado (Recomendado)
```bash
# El APK ya está disponible en la raíz del proyecto
MediSupplyG12-v1.0.1.apk

# Instalar en dispositivo Android
adb install MediSupplyG12-v1.0.1.apk
```

## 📋 Changelog

### v1.0.1 (2025-10-18)
- ✅ **Corrección de información de clientes**: Los nombres y teléfonos de clientes ahora se muestran correctamente en las rutas de repartidor
- ✅ **Optimización de llamadas API**: Eliminadas llamadas duplicadas que causaban problemas de rendimiento
- ✅ **Manejo robusto de cancelaciones**: Mejor gestión de corrutinas canceladas

### v1.0.0 (2025-10-18)
- 🎉 **Lanzamiento inicial**
- 🔄 Selector de ambiente (Desarrollo/Producción)
- 👥 Gestión de roles (Vendedor, Repartidor, Cliente)
- 📦 Gestión de inventario y pedidos
- 🚚 Rutas de entrega por período
- 🌐 Internacionalización (Español/Inglés)

### Opción 2: Compilación desde Código Fuente

#### Prerrequisitos
- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17 o superior
- Android SDK API 28+
- Gradle 8.0+

#### Pasos de Instalación
```bash
# 1. Clonar el repositorio
git clone <repository-url>
cd Mobile-MediSupply

# 2. Configurar JAVA_HOME (si es necesario)
export JAVA_HOME=/snap/android-studio/209/jbr

# 3. Compilar la aplicación
./gradlew assembleDebug

# 4. Instalar en dispositivo
./gradlew installDebug
```

## 🎯 Uso de la Aplicación

### Configuración Inicial
1. **Seleccionar idioma**: Español o Inglés
2. **Elegir rol**: Vendedor, Repartidor o Cliente
3. **Configurar ambiente**: Desarrollo o Producción
4. **Iniciar sesión**: La aplicación está lista para usar

### Para Vendedores
- **Rutas de visitas**: Visualizar clientes asignados por período
- **Crear pedidos**: Seleccionar productos con stock disponible
- **Gestión de inventario**: Ver disponibilidad en tiempo real

### Para Repartidores
- **Rutas de entrega**: Organizadas por día, semana o mes
- **Información de clientes**: Direcciones y datos de contacto
- **Navegación optimizada**: Scroll automático entre períodos

## 🧪 Testing

### Ejecutar Pruebas Unitarias
```bash
# Configurar JAVA_HOME
export JAVA_HOME=/snap/android-studio/209/jbr

# Ejecutar todas las pruebas
./execute-tests.sh

# O ejecutar directamente con Gradle
./gradlew test
```

### Cobertura de Código
```bash
# Generar reporte de cobertura
./gradlew jacocoTestReport

# Ver reporte HTML
open app/build/reports/jacoco/testDebugUnitTest/html/index.html
```

## 📁 Estructura del Proyecto

```
app/
├── src/main/java/com/medisupplyg4/
│   ├── config/           # Configuración de API y ambientes
│   ├── models/           # Modelos de datos
│   ├── network/          # Servicios de red y clientes HTTP
│   ├── repositories/     # Repositorios de datos
│   ├── ui/              # Componentes de UI
│   │   ├── components/  # Componentes reutilizables
│   │   └── screens/     # Pantallas de la aplicación
│   ├── utils/           # Utilidades y helpers
│   └── viewmodels/      # ViewModels para MVVM
├── src/test/            # Pruebas unitarias
└── src/androidTest/     # Pruebas de integración
```

## 🔧 Configuración de Desarrollo

### Scripts Disponibles
- `./dev-app.sh full`: Compilar, instalar y lanzar la aplicación
- `./dev-app.sh logs`: Ver logs en tiempo real
- `./execute-tests.sh`: Ejecutar suite completa de pruebas

### Variables de Entorno
```bash
# Configurar JAVA_HOME para desarrollo
export JAVA_HOME=/snap/android-studio/209/jbr

# Configurar Android SDK (si es necesario)
export ANDROID_HOME=/path/to/android-sdk
```

## 🌐 Configuración de API

### Ambientes Disponibles
- **Desarrollo**: `http://10.0.2.2` (servidores locales)
- **Producción**: `https://api.medisupplyg4.online`

### Endpoints Principales
- **Usuarios**: `/usuarios/api/`
- **Ventas**: `/ventas/api/`
- **Productos**: `/productos/api/`
- **Logística**: `/logistica/api/`

## 🐛 Solución de Problemas

### Problemas Comunes

#### Error de Compilación
```bash
# Limpiar proyecto
./gradlew clean

# Recompilar
./gradlew assembleDebug
```

#### Problemas de Red
- Verificar configuración de ambiente
- Comprobar conectividad a internet
- Revisar logs de red en Android Studio

#### Problemas de Permisos
- Verificar permisos de internet en AndroidManifest.xml
- Comprobar configuración de red en dispositivo

---

**Versión**: 1.0.0  
**Última actualización**: 18 de Octubre de 2025  
**Estado**: ✅ Estable y listo para producción
