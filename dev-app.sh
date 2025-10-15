#!/bin/bash

# Script de desarrollo para MediSupply
# Uso: ./dev-app.sh [comando] [opciones]

# Configurar variables de entorno
export JAVA_HOME=/snap/android-studio/209/jbr
export PATH=$JAVA_HOME/bin:$PATH
export ANDROID_HOME=/home/jairo-personal/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools

# Configuración
APP_PACKAGE="com.medisupplyg4"
MAIN_ACTIVITY="com.medisupplyg4.MainActivity"
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para mostrar ayuda
show_help() {
    echo -e "${BLUE}Script de desarrollo para MediSupply${NC}"
    echo ""
    echo "Uso: $0 [comando] [opciones]"
    echo ""
    echo "Comandos:"
    echo "  build, b          Compilar la aplicación"
    echo "  install, i        Instalar APK en el emulador"
    echo "  launch, l         Lanzar la aplicación"
    echo "  logs, log         Ver logs de la aplicación"
    echo "  clean, c          Limpiar proyecto"
    echo "  full, f           Compilar, instalar y lanzar (todo en uno)"
    echo "  restart, r        Reiniciar la aplicación"
    echo "  uninstall, u      Desinstalar la aplicación"
    echo "  devices, d        Ver dispositivos conectados"
    echo "  emulator, e       Lanzar emulador"
    echo "  help, h           Mostrar esta ayuda"
    echo ""
    echo "Opciones para logs:"
    echo "  -f, --follow      Seguir logs en tiempo real"
    echo "  -c, --clear       Limpiar logs antes de mostrar"
    echo "  -t, --tag TAG     Filtrar por tag específico"
    echo ""
    echo "Ejemplos:"
    echo "  $0 build                    # Solo compilar"
    echo "  $0 full                     # Compilar, instalar y lanzar"
    echo "  $0 logs -f                  # Ver logs en tiempo real"
    echo "  $0 logs -t MediSupply       # Ver logs filtrados por tag"
    echo "  $0 restart                  # Reiniciar la app"
}

# Función para mostrar mensajes con color
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Función para verificar si el emulador está conectado
check_emulator() {
    local devices=$(adb devices | grep -c "device$")
    if [ $devices -eq 0 ]; then
        log_error "No hay dispositivos conectados. Lanza el emulador primero."
        return 1
    fi
    return 0
}

# Función para compilar
build_app() {
    log_info "Compilando la aplicación..."
    if ./gradlew assembleDebug; then
        log_success "Compilación exitosa"
        return 0
    else
        log_error "Error en la compilación"
        return 1
    fi
}

# Función para instalar
install_app() {
    if ! check_emulator; then
        return 1
    fi
    
    if [ ! -f "$APK_PATH" ]; then
        log_warning "APK no encontrado. Compilando primero..."
        if ! build_app; then
            return 1
        fi
    fi
    
    log_info "Instalando aplicación..."
    if adb install -r "$APK_PATH"; then
        log_success "Aplicación instalada exitosamente"
        return 0
    else
        log_error "Error instalando la aplicación"
        return 1
    fi
}

# Función para lanzar
launch_app() {
    if ! check_emulator; then
        return 1
    fi
    
    log_info "Lanzando aplicación..."
    if adb shell am start -n "$APP_PACKAGE/$MAIN_ACTIVITY"; then
        log_success "Aplicación lanzada exitosamente"
        return 0
    else
        log_error "Error lanzando la aplicación"
        return 1
    fi
}

# Función para ver logs
show_logs() {
    local follow=false
    local clear_logs=false
    local tag_filter=""
    
    # Procesar opciones de logs
    while [[ $# -gt 0 ]]; do
        case $1 in
            -f|--follow)
                follow=true
                shift
                ;;
            -c|--clear)
                clear_logs=true
                shift
                ;;
            -t|--tag)
                tag_filter="$2"
                shift 2
                ;;
            *)
                shift
                ;;
        esac
    done
    
    if ! check_emulator; then
        return 1
    fi
    
    log_info "Mostrando logs de la aplicación..."
    
    if [ "$clear_logs" = true ]; then
        log_info "Limpiando logs..."
        adb logcat -c
    fi
    
    if [ -n "$tag_filter" ]; then
        log_info "Filtrando por tag: $tag_filter"
        if [ "$follow" = true ]; then
            adb logcat -s "$tag_filter"
        else
            adb logcat -s "$tag_filter" | head -50
        fi
    else
        if [ "$follow" = true ]; then
            adb logcat | grep -E "(MediSupply|$APP_PACKAGE|DeliveryRoute|AndroidRuntime)"
        else
            adb logcat | grep -E "(MediSupply|$APP_PACKAGE|DeliveryRoute|AndroidRuntime)" | head -50
        fi
    fi
}

# Función para limpiar
clean_project() {
    log_info "Limpiando proyecto..."
    if ./gradlew clean; then
        log_success "Proyecto limpiado exitosamente"
        return 0
    else
        log_error "Error limpiando el proyecto"
        return 1
    fi
}

# Función para proceso completo
full_process() {
    log_info "Ejecutando proceso completo: compilar, instalar y lanzar"
    
    if build_app && install_app && launch_app; then
        log_success "Proceso completo exitoso"
        echo ""
        log_info "Para ver logs en tiempo real, ejecuta:"
        echo "  $0 logs -f"
    else
        log_error "Error en el proceso completo"
        return 1
    fi
}

# Función para reiniciar la app
restart_app() {
    if ! check_emulator; then
        return 1
    fi
    
    log_info "Reiniciando aplicación..."
    
    # Cerrar la app
    adb shell am force-stop "$APP_PACKAGE"
    sleep 1
    
    # Lanzar de nuevo
    if launch_app; then
        log_success "Aplicación reiniciada exitosamente"
    else
        log_error "Error reiniciando la aplicación"
        return 1
    fi
}

# Función para desinstalar
uninstall_app() {
    if ! check_emulator; then
        return 1
    fi
    
    log_info "Desinstalando aplicación..."
    if adb uninstall "$APP_PACKAGE"; then
        log_success "Aplicación desinstalada exitosamente"
        return 0
    else
        log_error "Error desinstalando la aplicación"
        return 1
    fi
}

# Función para ver dispositivos
show_devices() {
    log_info "Dispositivos conectados:"
    adb devices
}

# Función para lanzar emulador
launch_emulator() {
    log_info "Lanzando emulador..."
    if [ -f "./run-emulator.sh" ]; then
        ./run-emulator.sh
    else
        emulator -avd Medium_Phone_API_36.1
    fi
}

# Función principal
main() {
    case $1 in
        build|b)
            build_app
            ;;
        install|i)
            install_app
            ;;
        launch|l)
            launch_app
            ;;
        logs|log)
            shift
            show_logs "$@"
            ;;
        clean|c)
            clean_project
            ;;
        full|f)
            full_process
            ;;
        restart|r)
            restart_app
            ;;
        uninstall|u)
            uninstall_app
            ;;
        devices|d)
            show_devices
            ;;
        emulator|e)
            launch_emulator
            ;;
        help|h|"")
            show_help
            ;;
        *)
            log_error "Comando desconocido: $1"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

# Ejecutar función principal con todos los argumentos
main "$@"






