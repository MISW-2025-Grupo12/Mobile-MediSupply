#!/bin/bash

# Script para lanzar el emulador de Android
# Uso: ./run-emulator.sh [opciones]

# Configurar variables de entorno
export ANDROID_HOME=/home/jairo-personal/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools

# Dispositivo virtual por defecto
AVD_NAME="Medium_Phone_API_36.1"

# Función para mostrar ayuda
show_help() {
    echo "Uso: $0 [opciones]"
    echo ""
    echo "Opciones:"
    echo "  -l, --list     Listar dispositivos virtuales disponibles"
    echo "  -a, --avd      Especificar nombre del AVD"
    echo "  -n, --no-audio Lanzar sin audio"
    echo "  -g, --gpu      Lanzar con GPU acelerada"
    echo "  -h, --help     Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  $0                    # Lanzar con configuración por defecto"
    echo "  $0 -l                 # Listar AVDs disponibles"
    echo "  $0 -a MiDispositivo   # Lanzar AVD específico"
    echo "  $0 -n                 # Lanzar sin audio"
}

# Procesar argumentos
while [[ $# -gt 0 ]]; do
    case $1 in
        -l|--list)
            echo "Dispositivos virtuales disponibles:"
            emulator -list-avds
            exit 0
            ;;
        -a|--avd)
            AVD_NAME="$2"
            shift 2
            ;;
        -n|--no-audio)
            NO_AUDIO="-no-audio"
            shift
            ;;
        -g|--gpu)
            GPU_ACCEL="-gpu host"
            shift
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            echo "Opción desconocida: $1"
            show_help
            exit 1
            ;;
    esac
done

# Verificar que el AVD existe
if ! emulator -list-avds | grep -q "$AVD_NAME"; then
    echo "Error: El AVD '$AVD_NAME' no existe."
    echo "AVDs disponibles:"
    emulator -list-avds
    exit 1
fi

# Lanzar el emulador
echo "Lanzando emulador: $AVD_NAME"
emulator -avd "$AVD_NAME" $NO_AUDIO $GPU_ACCEL




