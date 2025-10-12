#!/bin/bash

# Script para ejecutar pruebas unitarias y validar coverage
# Mínimo coverage requerido: 70%

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuración
MIN_COVERAGE=70
COVERAGE_REPORT_DIR="app/build/reports/jacoco/testDebugUnitTest/html"
COVERAGE_FILE="app/build/reports/jacoco/testDebugUnitTest/jacocoTestReport.xml"

echo -e "${BLUE}[INFO]${NC} Ejecutando pruebas unitarias y generando reporte de coverage..."

# Limpiar builds anteriores
echo -e "${BLUE}[INFO]${NC} Limpiando builds anteriores..."
./gradlew clean

# Ejecutar pruebas unitarias con coverage
echo -e "${BLUE}[INFO]${NC} Ejecutando pruebas unitarias..."
./gradlew testDebugUnitTest jacocoTestReport

# Verificar si el reporte de coverage fue generado
if [ ! -f "$COVERAGE_FILE" ]; then
    echo -e "${RED}[ERROR]${NC} No se pudo generar el reporte de coverage"
    exit 1
fi

echo -e "${GREEN}[SUCCESS]${NC} Reporte de coverage generado exitosamente"

# Extraer el porcentaje de coverage del archivo XML
COVERAGE_PERCENT=$(grep -o 'line-rate="[^"]*"' "$COVERAGE_FILE" | head -1 | sed 's/line-rate="//;s/"//' | awk '{print int($1 * 100)}')

echo -e "${BLUE}[INFO]${NC} Coverage actual: ${COVERAGE_PERCENT}%"
echo -e "${BLUE}[INFO]${NC} Coverage mínimo requerido: ${MIN_COVERAGE}%"

# Validar coverage
if [ "$COVERAGE_PERCENT" -ge "$MIN_COVERAGE" ]; then
    echo -e "${GREEN}[SUCCESS]${NC} ✅ Coverage válido: ${COVERAGE_PERCENT}% >= ${MIN_COVERAGE}%"
    echo -e "${GREEN}[INFO]${NC} Reporte HTML disponible en: ${COVERAGE_REPORT_DIR}/index.html"
    exit 0
else
    echo -e "${RED}[ERROR]${NC} ❌ Coverage insuficiente: ${COVERAGE_PERCENT}% < ${MIN_COVERAGE}%"
    echo -e "${YELLOW}[INFO]${NC} Reporte HTML disponible en: ${COVERAGE_REPORT_DIR}/index.html"
    echo -e "${YELLOW}[INFO]${NC} Necesitas agregar más pruebas para alcanzar el ${MIN_COVERAGE}% mínimo"
    exit 1
fi
