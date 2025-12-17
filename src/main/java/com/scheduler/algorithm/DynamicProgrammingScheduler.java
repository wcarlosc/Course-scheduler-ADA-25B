package com.scheduler.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.scheduler.logic.PriorityType;
import com.scheduler.logic.ScheduleEvaluator;
import com.scheduler.model.Course;

/**
 * Implementación de Programación Dinámica para planificación de horarios
 * 
 * Estrategia:
 * 1. Estado: dp[i][mask] = lista de horarios válidos usando las primeras i materias
 *    y grupos específicos (representados por mask)
 * 2. Transición: Para cada materia, probar cada grupo compatible
 * 3. Memorización: Guardar subproblemas para evitar recálculos
 */
public class DynamicProgrammingScheduler {
    
    private List<Course> allCourses;
    private Map<String, List<List<Course>>> memo;

    public DynamicProgrammingScheduler(List<Course> allCourses) {
        this.allCourses = allCourses;
        this.memo = new HashMap<>();
    }

    public List<List<Course>> generateSchedules(List<String> desiredSubjects) {
        return generateSchedules(desiredSubjects, PriorityType.NONE);
    }

    public List<List<Course>> generateSchedules(List<String> desiredSubjects, PriorityType priority) {
        memo.clear();
        
        Map<String, List<Course>> coursesBySubject = allCourses.stream()
                .filter(c -> desiredSubjects.contains(c.getSubject()))
                .collect(Collectors.groupingBy(Course::getSubject));

        // Verificar que todas las materias existan
        for (String subject : desiredSubjects) {
            if (!coursesBySubject.containsKey(subject)) {
                return new ArrayList<>();
            }
        }

        List<List<Course>> schedules = dpSolve(desiredSubjects, 0, new ArrayList<>(), coursesBySubject);
        return ScheduleEvaluator.sortSchedules(schedules, priority);
    }

    /**
     * Solución con programación dinámica usando memorización
     * 
     * @param subjects Lista de materias
     * @param index Índice actual de materia
     * @param currentSchedule Horario en construcción
     * @param coursesBySubject Mapa de materias a cursos
     * @return Lista de todos los horarios válidos
     */
    private List<List<Course>> dpSolve(List<String> subjects, int index, 
                                       List<Course> currentSchedule,
                                       Map<String, List<Course>> coursesBySubject) {
        // Caso base: Todas las materias procesadas
        if (index == subjects.size()) {
            List<List<Course>> result = new ArrayList<>();
            result.add(new ArrayList<>(currentSchedule));
            return result;
        }

        // Crear clave para memorización
        String memoKey = createMemoKey(index, currentSchedule);
        
        // Verificar si ya calculamos este estado
        if (memo.containsKey(memoKey)) {
            return memo.get(memoKey);
        }

        List<List<Course>> allSolutions = new ArrayList<>();
        String currentSubject = subjects.get(index);
        List<Course> options = coursesBySubject.get(currentSubject);

        // Probar cada opción de grupo para la materia actual
        for (Course option : options) {
            if (!hasConflict(currentSchedule, option)) {
                // Agregar curso actual
                currentSchedule.add(option);
                
                // Resolver recursivamente para las siguientes materias
                List<List<Course>> subSolutions = dpSolve(subjects, index + 1, 
                                                          currentSchedule, coursesBySubject);
                allSolutions.addAll(subSolutions);
                
                // Backtrack
                currentSchedule.remove(currentSchedule.size() - 1);
            }
        }

        // Guardar en memoria
        memo.put(memoKey, allSolutions);
        return allSolutions;
    }

    /**
     * Crea una clave única para el estado actual (para memorización)
     */
    private String createMemoKey(int index, List<Course> schedule) {
        StringBuilder key = new StringBuilder();
        key.append(index).append(":");
        
        // Incluir IDs de cursos ordenados para consistencia
        schedule.stream()
            .map(Course::getId)
            .sorted()
            .forEach(id -> key.append(id).append(","));
        
        return key.toString();
    }

    private boolean hasConflict(List<Course> currentSchedule, Course newCourse) {
        for (Course existing : currentSchedule) {
            if (existing.overlaps(newCourse)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Versión optimizada usando bitmasking (para pocos cursos)
     */
    public List<List<Course>> generateSchedulesOptimized(List<String> desiredSubjects) {
        Map<String, List<Course>> coursesBySubject = allCourses.stream()
                .filter(c -> desiredSubjects.contains(c.getSubject()))
                .collect(Collectors.groupingBy(Course::getSubject));

        for (String subject : desiredSubjects) {
            if (!coursesBySubject.containsKey(subject)) {
                return new ArrayList<>();
            }
        }

        // DP con tabla: dp[materia][máscara de grupos seleccionados]
        List<String> subjectList = new ArrayList<>(desiredSubjects);
        Map<Integer, List<List<Course>>> dp = new HashMap<>();
        
        // Inicializar: sin materias seleccionadas
        dp.put(0, Arrays.asList(new ArrayList<>()));

        // Procesar cada materia
        for (int i = 0; i < subjectList.size(); i++) {
            String subject = subjectList.get(i);
            List<Course> options = coursesBySubject.get(subject);
            Map<Integer, List<List<Course>>> newDp = new HashMap<>();

            for (Map.Entry<Integer, List<List<Course>>> entry : dp.entrySet()) {
                for (List<Course> prevSchedule : entry.getValue()) {
                    // Probar cada grupo de la materia actual
                    for (int j = 0; j < options.size(); j++) {
                        Course option = options.get(j);
                        
                        if (!hasConflict(prevSchedule, option)) {
                            List<Course> newSchedule = new ArrayList<>(prevSchedule);
                            newSchedule.add(option);
                            
                            int newMask = entry.getKey() | (1 << (i * 4 + j));
                            newDp.computeIfAbsent(newMask, k -> new ArrayList<>())
                                  .add(newSchedule);
                        }
                    }
                }
            }

            dp = newDp;
        }

        // Recolectar todas las soluciones completas
        return dp.values().stream()
            .flatMap(List::stream)
            .filter(schedule -> schedule.size() == subjectList.size())
            .distinct()
            .collect(Collectors.toList());
    }
}
