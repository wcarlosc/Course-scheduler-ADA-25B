package com.scheduler.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.scheduler.model.Course;

/**
 * Implementación de Divide & Conquer para planificación de horarios
 * 
 * Estrategia:
 * 1. DIVIDE: Dividir las materias en dos grupos
 * 2. CONQUER: Resolver cada subgrupo recursivamente
 * 3. COMBINE: Combinar soluciones verificando compatibilidad
 */
public class DivideConquerScheduler {
    
    private List<Course> allCourses;

    public DivideConquerScheduler(List<Course> allCourses) {
        this.allCourses = allCourses;
    }

    public List<List<Course>> generateSchedules(List<String> desiredSubjects) {
        if (desiredSubjects.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, List<Course>> coursesBySubject = allCourses.stream()
                .filter(c -> desiredSubjects.contains(c.getSubject()))
                .collect(Collectors.groupingBy(Course::getSubject));

        // Verificar que todas las materias existan
        for (String subject : desiredSubjects) {
            if (!coursesBySubject.containsKey(subject)) {
                return new ArrayList<>();
            }
        }

        return divideAndConquer(desiredSubjects, coursesBySubject);
    }

    private List<List<Course>> divideAndConquer(List<String> subjects, 
                                                 Map<String, List<Course>> coursesBySubject) {
        // Caso base: una sola materia
        if (subjects.size() == 1) {
            String subject = subjects.get(0);
            return coursesBySubject.get(subject).stream()
                .map(course -> {
                    List<Course> schedule = new ArrayList<>();
                    schedule.add(course);
                    return schedule;
                })
                .collect(Collectors.toList());
        }

        // DIVIDE: Dividir en dos mitades
        int mid = subjects.size() / 2;
        List<String> leftSubjects = subjects.subList(0, mid);
        List<String> rightSubjects = subjects.subList(mid, subjects.size());

        // CONQUER: Resolver recursivamente cada mitad
        List<List<Course>> leftSolutions = divideAndConquer(leftSubjects, coursesBySubject);
        List<List<Course>> rightSolutions = divideAndConquer(rightSubjects, coursesBySubject);

        // COMBINE: Combinar soluciones compatibles
        return combine(leftSolutions, rightSolutions);
    }

    private List<List<Course>> combine(List<List<Course>> leftSolutions, 
                                       List<List<Course>> rightSolutions) {
        List<List<Course>> combined = new ArrayList<>();

        for (List<Course> left : leftSolutions) {
            for (List<Course> right : rightSolutions) {
                if (!hasConflict(left, right)) {
                    List<Course> merged = new ArrayList<>(left);
                    merged.addAll(right);
                    combined.add(merged);
                }
            }
        }

        return combined;
    }

    private boolean hasConflict(List<Course> schedule1, List<Course> schedule2) {
        for (Course c1 : schedule1) {
            for (Course c2 : schedule2) {
                if (c1.overlaps(c2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
