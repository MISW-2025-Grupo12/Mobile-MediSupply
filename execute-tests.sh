#!/bin/bash

# Script para ejecutar pruebas unitarias
# Valida que las pruebas se ejecuten con éxito y genera reporte de coverage

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuración
COVERAGE_REPORT_DIR="app/build/reports/jacoco/testDebugUnitTest/html"
COVERAGE_FILE="app/build/reports/jacoco/testDebugUnitTest/jacocoTestReport.xml"

echo -e "${BLUE}[INFO]${NC} Ejecutando pruebas unitarias..."

# Limpiar builds anteriores
echo -e "${BLUE}[INFO]${NC} Limpiando builds anteriores..."
./gradlew clean

# Ejecutar pruebas unitarias con coverage
echo -e "${BLUE}[INFO]${NC} Ejecutando pruebas unitarias..."
./gradlew testDebugUnitTest jacocoTestReport

# Verificar si el reporte de coverage fue generado
if [ ! -f "$COVERAGE_FILE" ]; then
    echo -e "${YELLOW}[WARNING]${NC} No se pudo generar el reporte de coverage en: $COVERAGE_FILE"
    echo -e "${BLUE}[INFO]${NC} Las pruebas se ejecutaron correctamente, pero no se generó el reporte de coverage"
else
    echo -e "${GREEN}[SUCCESS]${NC} Reporte de coverage generado exitosamente"
fi

# Mostrar información adicional
echo -e "${BLUE}[INFO]${NC} Archivos de prueba encontrados:"
TEST_COUNT=$(find app/src/test/java -name "*Test.kt" | wc -l)
echo -e "  📄 Total: $TEST_COUNT archivos de prueba"

if [ -f "$COVERAGE_FILE" ]; then
    echo -e "${BLUE}[INFO]${NC} Reporte HTML detallado disponible en: ${COVERAGE_REPORT_DIR}/index.html"
fi

echo -e "${GREEN}[SUCCESS]${NC} ✅ Todas las pruebas unitarias se ejecutaron correctamente"
exit 0
