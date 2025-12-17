package com.scheduler.logic;

public enum PriorityType {
    FEWER_DAYS("Menos días de clase"),
    LESS_GAPS("Menos espacios entre cursos");

    private final String description;

    PriorityType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
