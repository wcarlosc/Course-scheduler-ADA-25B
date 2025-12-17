package com.scheduler.logic;

import com.scheduler.model.Course;
import com.scheduler.model.TimeSlot;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Clase para evaluar y comparar horarios según diferentes criterios
 */
public class ScheduleEvaluator {

    /**
     * Calcula el número de días únicos que se usan en un horario
     */
    public static int calculateUniqueDays(List<Course> schedule) {
        Set<String> uniqueDays = new HashSet<>();
        for (Course course : schedule) {
            for (TimeSlot slot : course.getSchedules()) {
                uniqueDays.add(slot.getDay());
            }
        }
        return uniqueDays.size();
    }

    /**
     * Calcula el tiempo total de gaps (espacios entre cursos) en minutos
     * Solo cuenta gaps en el mismo día entre cursos consecutivos
     */
    public static int calculateTotalGaps(List<Course> schedule) {
        // Agrupar bloques horarios por día
        Map<String, List<TimeSlot>> slotsByDay = new HashMap<>();
        
        for (Course course : schedule) {
            for (TimeSlot slot : course.getSchedules()) {
                slotsByDay.computeIfAbsent(slot.getDay(), k -> new ArrayList<>()).add(slot);
            }
        }

        int totalGaps = 0;

        // Para cada día, calcular gaps entre bloques
        for (List<TimeSlot> slots : slotsByDay.values()) {
            if (slots.size() < 2) continue;

            // Ordenar por hora de inicio
            slots.sort(Comparator.comparingInt(TimeSlot::getStart));

            // Calcular gaps entre bloques consecutivos
            for (int i = 0; i < slots.size() - 1; i++) {
                TimeSlot current = slots.get(i);
                TimeSlot next = slots.get(i + 1);
                
                // Gap = inicio del siguiente - fin del actual
                int gap = next.getStart() - current.getEnd();
                if (gap > 0) {
                    totalGaps += gap;
                }
            }
        }

        return totalGaps;
    }

    /**
     * Ordena una lista de horarios según el tipo de prioridad especificado
     */
    public static List<List<Course>> sortSchedules(List<List<Course>> schedules, PriorityType priority) {
        if (priority == PriorityType.NONE || schedules.isEmpty()) {
            return schedules;
        }

        List<List<Course>> sortedSchedules = new ArrayList<>(schedules);

        switch (priority) {
            case FEWER_DAYS:
                sortedSchedules.sort(Comparator.comparingInt(ScheduleEvaluator::calculateUniqueDays));
                break;

            case LESS_GAPS:
                sortedSchedules.sort(Comparator.comparingInt(ScheduleEvaluator::calculateTotalGaps));
                break;

            default:
                break;
        }

        return sortedSchedules;
    }

    /**
     * Genera un resumen de métricas para un horario
     */
    public static String getScheduleMetrics(List<Course> schedule) {
        int uniqueDays = calculateUniqueDays(schedule);
        int totalGaps = calculateTotalGaps(schedule);
        
        StringBuilder sb = new StringBuilder();
        sb.append("   📊 Días de clase: ").append(uniqueDays);
        sb.append(" | Tiempo libre entre cursos: ").append(formatMinutes(totalGaps));
        
        return sb.toString();
    }

    /**
     * Formatea minutos a formato legible (ej: "2h 30min")
     */
    private static String formatMinutes(int minutes) {
        if (minutes == 0) return "0min";
        
        int hours = minutes / 60;
        int mins = minutes % 60;
        
        if (hours == 0) {
            return mins + "min";
        } else if (mins == 0) {
            return hours + "h";
        } else {
            return hours + "h " + mins + "min";
        }
    }
}
