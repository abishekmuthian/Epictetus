#!/bin/bash

# AI Edge Gallery Build Script
# Copyright 2025 Google LLC

set -e  # Exit on any error

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ANDROID_DIR="$PROJECT_DIR/Android/src"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Android project exists
check_android_project() {
    if [ ! -d "$ANDROID_DIR" ]; then
        print_error "Android project not found at $ANDROID_DIR"
        exit 1
    fi

    if [ ! -f "$ANDROID_DIR/build.gradle.kts" ]; then
        print_error "Android build.gradle.kts not found"
        exit 1
    fi
}

# Show help
show_help() {
    echo "AI Edge Gallery Build Script"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  debug           Build debug APK"
    echo "  release         Build release APK"
    echo "  install         Build and install debug APK"
    echo "  clean           Clean build artifacts"
    echo "  test            Run unit tests"
    echo "  lint            Run lint checks"
    echo "  help            Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 debug       # Build debug APK"
    echo "  $0 install     # Build and install to connected device"
    echo ""
}

# Build debug APK
build_debug() {
    print_info "Building debug APK..."
    cd "$ANDROID_DIR"
    ./gradlew assembleDebug
    print_success "Debug APK built successfully!"
    echo "Location: $ANDROID_DIR/app/build/outputs/apk/debug/app-debug.apk"
}

# Build release APK
build_release() {
    print_info "Building release APK..."
    cd "$ANDROID_DIR"
    ./gradlew assembleRelease
    print_success "Release APK built successfully!"
    echo "Location: $ANDROID_DIR/app/build/outputs/apk/release/app-release.apk"
}

# Install debug APK
install_debug() {
    print_info "Building and installing debug APK..."
    cd "$ANDROID_DIR"
    ./gradlew installDebug
    print_success "Debug APK installed successfully!"
}

# Clean build artifacts
clean_build() {
    print_info "Cleaning build artifacts..."
    cd "$ANDROID_DIR"
    ./gradlew clean
    print_success "Build artifacts cleaned successfully!"
}

# Run tests
run_tests() {
    print_info "Running unit tests..."
    cd "$ANDROID_DIR"
    ./gradlew test
    print_success "Tests completed successfully!"
}

# Run lint
run_lint() {
    print_info "Running lint checks..."
    cd "$ANDROID_DIR"
    ./gradlew lint
    print_success "Lint checks completed!"
    echo "Report: $ANDROID_DIR/app/build/reports/lint-results.html"
}

# Main execution
main() {
    check_android_project

    case "${1:-help}" in
        "debug")
            build_debug
            ;;
        "release")
            build_release
            ;;
        "install")
            install_debug
            ;;
        "clean")
            clean_build
            ;;
        "test")
            run_tests
            ;;
        "lint")
            run_lint
            ;;
        "help"|*)
            show_help
            ;;
    esac
}

main "$@"