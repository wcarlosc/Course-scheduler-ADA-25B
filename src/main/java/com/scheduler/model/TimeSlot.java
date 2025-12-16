package com.scheduler.model;

public class TimeSlot {
    private String day;
    private int start; // en minutos desde medianoche
    private int end;   // en minutos desde medianoche

    public TimeSlot(String day, int start, int end) {
        this.day = day;
        this.start = start;
        this.end = end;
    }

    public String getDay() {
        return day;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    /**
     * Convierte minutos a formato HH:MM
     * @param minutes minutos desde medianoche
     * @return String en formato HH:MM
     */
    private String minutesToTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%d:%02d", hours, mins);
    }

    /**
     * Verifica si dos bloques horarios se solapan
     * Dos bloques se solapan si:
     * 1. Son el mismo día
     * 2. Sus intervalos de tiempo se cruzan
     */
    public boolean overlaps(TimeSlot other) {
        if (!this.day.equalsIgnoreCase(other.day)) {
            return false;
        }
        // Verifica si los intervalos se solapan
        // (StartA < EndB) AND (EndA > StartB)
        return this.start < other.end && this.end > other.start;
    }

    @Override
    public String toString() {
        return day + " " + minutesToTime(start) + "-" + minutesToTime(end);
    }
}
