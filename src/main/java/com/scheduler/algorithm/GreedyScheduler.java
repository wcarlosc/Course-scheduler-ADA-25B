package com.scheduler.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.scheduler.logic.PriorityType;
import com.scheduler.logic.ScheduleEvaluator;
import com.scheduler.model.Course;
import com.scheduler.model.TimeSlot;

/**
 * Implementación de Algoritmo Goloso (Greedy) para planificación de horarios
 * 
 * Estrategia heurística:
 * 1. Para cada materia, seleccionar el grupo con menos conflictos potenciales
 * 2. Priorizar grupos con horarios tempranos o menos sesiones semanales
 * 3. Construir una única solución "óptima" localmente
 */
public class GreedyScheduler {
    
    private List<Course> allCourses;

    public GreedyScheduler(List<Course> allCourses) {
        this.allCourses = allCourses;
    }

    public List<List<Course>> generateSchedules(List<String> desiredSubjects) {
        return generateSchedules(desiredSubjects, PriorityType.NONE);
    }

    public List<List<Course>> generateSchedules(List<String> desiredSubjects, PriorityType priority) {
        List<List<Course>> solutions = new ArrayList<>();
        
        Map<String, List<Course>> coursesBySubject = allCourses.stream()
                .filter(c -> desiredSubjects.contains(c.getSubject()))
                .collect(Collectors.groupingBy(Course::getSubject));

        // Verificar que todas las materias existan
        for (String subject : desiredSubjects) {
            if (!coursesBySubject.containsKey(subject)) {
                return solutions;
            }
        }

        // Generar múltiples soluciones con diferentes criterios de ordenamiento
        solutions.add(greedyByEarliestStart(desiredSubjects, coursesBySubject));
        solutions.add(greedyByFewestSessions(desiredSubjects, coursesBySubject));
        solutions.add(greedyByLeastConflicts(desiredSubjects, coursesBySubject));

        // Eliminar soluciones nulas o duplicadas
        List<List<Course>> validSolutions = solutions.stream()
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
            
        return ScheduleEvaluator.sortSchedules(validSolutions, priority);
    }

    /**
     * Estrategia golosa: Priorizar cursos con horario más temprano
     */
    private List<Course> greedyByEarliestStart(List<String> subjects, 
                                                Map<String, List<Course>> coursesBySubject) {
        List<Course> schedule = new ArrayList<>();
        
        for (String subject : subjects) {
            List<Course> options = coursesBySubject.get(subject);
            
            // Ordenar por horario de inicio más temprano
            Course bestOption = options.stream()
                .filter(course -> !hasConflictWithSchedule(schedule, course))
                .min(Comparator.comparingInt(this::getEarliestStartTime))
                .orElse(null);
            
            if (bestOption == null) {
                return null; // No se puede completar el horario
            }
            
            schedule.add(bestOption);
        }
        
        return schedule;
    }

    /**
     * Estrategia golosa: Priorizar cursos con menos sesiones semanales
     */
    private List<Course> greedyByFewestSessions(List<String> subjects, 
                                                 Map<String, List<Course>> coursesBySubject) {
        List<Course> schedule = new ArrayList<>();
        
        for (String subject : subjects) {
            List<Course> options = coursesBySubject.get(subject);
            
            // Ordenar por cantidad de sesiones (menos es mejor)
            Course bestOption = options.stream()
                .filter(course -> !hasConflictWithSchedule(schedule, course))
                .min(Comparator.comparingInt(c -> c.getSchedules().size()))
                .orElse(null);
            
            if (bestOption == null) {
                return null;
            }
            
            schedule.add(bestOption);
        }
        
        return schedule;
    }

    /**
     * Estrategia golosa: Priorizar cursos con menos conflictos potenciales
     */
    private List<Course> greedyByLeastConflicts(List<String> subjects, 
                                                 Map<String, List<Course>> coursesBySubject) {
        List<Course> schedule = new ArrayList<>();
        
        // Ordenar materias por número de opciones disponibles (ascendente)
        List<String> sortedSubjects = subjects.stream()
            .sorted(Comparator.comparingInt(s -> coursesBySubject.get(s).size()))
            .collect(Collectors.toList());
        
        for (String subject : sortedSubjects) {
            List<Course> options = coursesBySubject.get(subject);
            
            // Seleccionar curso con menos conflictos potenciales con otros cursos
            Course bestOption = null;
            int minConflicts = Integer.MAX_VALUE;
            
            for (Course option : options) {
                if (!hasConflictWithSchedule(schedule, option)) {
                    int conflicts = countPotentialConflicts(option, coursesBySubject, subjects);
                    if (conflicts < minConflicts) {
                        minConflicts = conflicts;
                        bestOption = option;
                    }
                }
            }
            
            if (bestOption == null) {
                return null;
            }
            
            schedule.add(bestOption);
        }
        
        return schedule;
    }

    private int getEarliestStartTime(Course course) {
        return course.getSchedules().stream()
            .mapToInt(TimeSlot::getStart)
            .min()
            .orElse(Integer.MAX_VALUE);
    }

    private boolean hasConflictWithSchedule(List<Course> schedule, Course newCourse) {
        return schedule.stream().anyMatch(c -> c.overlaps(newCourse));
    }

    private int countPotentialConflicts(Course course, 
                                        Map<String, List<Course>> coursesBySubject,
                                        List<String> allSubjects) {
        int conflicts = 0;
        
        for (String subject : allSubjects) {
            if (subject.equals(course.getSubject())) continue;
            
            for (Course other : coursesBySubject.get(subject)) {
                if (course.overlaps(other)) {
                    conflicts++;
                }
            }
        }
        
        return conflicts;
    }
}
