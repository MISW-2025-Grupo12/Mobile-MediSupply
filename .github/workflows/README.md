# GitHub Actions Workflows

Este directorio contiene los workflows de GitHub Actions para el proyecto MediSupply.

## Workflows Disponibles

### 1. `android-ci.yml` - Pipeline Básico
- **Trigger**: Pull Requests y Push a `main` y `develop`
- **Funcionalidad**:
  - Compila el proyecto Android
  - Ejecuta las pruebas unitarias
  - Genera reporte de coverage
  - Sube artefactos con los resultados
  - Comenta en PRs con el estado de las pruebas

### 2. `android-ci-enhanced.yml` - Pipeline Mejorado
- **Trigger**: Pull Requests y Push a `main` y `develop`
- **Funcionalidad**:
  - Configuración optimizada del Android SDK
  - Cache mejorado para Gradle y Android SDK
  - Sube reportes de coverage por separado
  - Comentarios más detallados en PRs

## Configuración Requerida

### Variables de Entorno (Opcional)
Puedes configurar estas variables en Settings > Secrets and variables > Actions:

- `ANDROID_SDK_LICENSE`: Licencia del Android SDK (si es necesario)

### Permisos Requeridos
El workflow necesita estos permisos:
- `contents: read` - Para hacer checkout del código
- `pull-requests: write` - Para comentar en PRs
- `actions: write` - Para subir artefactos

## Cómo Funciona

1. **Trigger**: Se ejecuta automáticamente en PRs y pushes
2. **Setup**: Configura Java 11, Android SDK y Gradle
3. **Cache**: Usa cache para acelerar builds posteriores
4. **Tests**: Ejecuta `./execute-tests.sh`
5. **Artifacts**: Sube reportes de pruebas y coverage
6. **Comments**: Comenta en PRs con el estado

## Fallos del Pipeline

El pipeline fallará si:
- ❌ Las pruebas unitarias fallan
- ❌ El proyecto no compila
- ❌ Hay errores de sintaxis
- ❌ Faltan dependencias

## Artefactos Generados

- `test-results-{run_number}`: Reportes de pruebas unitarias
- `coverage-report-{run_number}`: Reporte HTML de coverage

## Personalización

Para personalizar el pipeline:

1. **Cambiar triggers**: Modifica la sección `on:`
2. **Agregar pasos**: Añade nuevos `steps` en el job
3. **Cambiar Java version**: Modifica `java-version` en setup-java
4. **Agregar notificaciones**: Usa actions como `slack` o `discord`

## Troubleshooting

### Error: "Android SDK not found"
- Verifica que `setup-android` esté configurado correctamente
- Asegúrate de que `api-level` y `build-tools` sean compatibles

### Error: "Gradle build failed"
- Revisa que todas las dependencias estén en `build.gradle.kts`
- Verifica que no haya errores de sintaxis

### Error: "Tests failed"
- Revisa los logs en la pestaña Actions
- Descarga los artefactos para ver reportes detallados
