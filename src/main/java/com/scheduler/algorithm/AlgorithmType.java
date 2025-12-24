package com.scheduler.algorithm;

public enum AlgorithmType {
    BACKTRACKING("Principal", "Búsqueda exhaustiva con retroceso"),
    DIVIDE_CONQUER("(Test) Divide y Conquista", "División recursiva del problema"),
    GREEDY("(Test) Algoritmo Goloso", "Selección por heurística local"),
    DYNAMIC_PROGRAMMING("(Test) Programación Dinámica", "Optimización con memorización");

    private final String displayName;
    private final String description;

    AlgorithmType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
