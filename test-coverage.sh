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
    echo -e "${RED}[ERROR]${NC} No se pudo generar el reporte de coverage en: $COVERAGE_FILE"
    exit 1
fi

echo -e "${GREEN}[SUCCESS]${NC} Reporte de coverage generado exitosamente"

# Extraer cobertura total del reporte JaCoCo
echo -e "${BLUE}[INFO]${NC} Extrayendo métricas de coverage del reporte JaCoCo..."

# Buscar el resumen total al final del archivo XML
TOTAL_COVERED=$(grep -o 'missed="[0-9]*" covered="[0-9]*"' "$COVERAGE_FILE" | tail -1 | sed 's/.*covered="\([0-9]*\)".*/\1/')
TOTAL_MISSED=$(grep -o 'missed="[0-9]*" covered="[0-9]*"' "$COVERAGE_FILE" | tail -1 | sed 's/missed="\([0-9]*\)".*/\1/')

# Si no encontramos el resumen total, buscar en los contadores de línea
if [ -z "$TOTAL_COVERED" ] || [ -z "$TOTAL_MISSED" ]; then
    echo -e "${YELLOW}[WARNING]${NC} No se encontró resumen total, calculando desde contadores individuales..."
    
    # Sumar todos los contadores de línea
    TOTAL_COVERED=$(grep '<counter type="LINE"' "$COVERAGE_FILE" | sed 's/.*covered="\([0-9]*\)".*/\1/' | awk '{sum += $1} END {print sum}')
    TOTAL_MISSED=$(grep '<counter type="LINE"' "$COVERAGE_FILE" | sed 's/.*missed="\([0-9]*\)".*/\1/' | awk '{sum += $1} END {print sum}')
fi

# Verificar que tenemos valores válidos
if [ -z "$TOTAL_COVERED" ] || [ -z "$TOTAL_MISSED" ]; then
    echo -e "${RED}[ERROR]${NC} No se pudieron extraer las métricas de coverage del XML"
    echo -e "${BLUE}[DEBUG]${NC} Contenido del archivo XML (últimas 10 líneas):"
    tail -10 "$COVERAGE_FILE"
    exit 1
fi

# Calcular porcentaje de cobertura
TOTAL_LINES=$((TOTAL_COVERED + TOTAL_MISSED))
if [ "$TOTAL_LINES" -eq 0 ]; then
    COVERAGE_PERCENT=0
else
    COVERAGE_PERCENT=$((TOTAL_COVERED * 100 / TOTAL_LINES))
fi

echo -e "${BLUE}[INFO]${NC} Líneas cubiertas: $TOTAL_COVERED"
echo -e "${BLUE}[INFO]${NC} Líneas totales: $TOTAL_LINES"
echo -e "${BLUE}[INFO]${NC} Coverage actual: ${COVERAGE_PERCENT}%"
echo -e "${BLUE}[INFO]${NC} Coverage mínimo requerido: ${MIN_COVERAGE}%"

# Mostrar información adicional
echo -e "${BLUE}[INFO]${NC} Archivos de prueba encontrados:"
TEST_COUNT=$(find app/src/test/java -name "*Test.kt" | wc -l)
echo -e "  📄 Total: $TEST_COUNT archivos de prueba"
echo -e "${BLUE}[INFO]${NC} Reporte HTML detallado disponible en: ${COVERAGE_REPORT_DIR}/index.html"

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
