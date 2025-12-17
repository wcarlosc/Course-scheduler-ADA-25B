package com.scheduler.logic;

import com.scheduler.model.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Scheduler {

    private List<Course> allCourses;

    public Scheduler(List<Course> allCourses) {
        this.allCourses = allCourses;
    }

    /**
     * Genera todos los horarios válidos para las materias deseadas
     * @param desiredSubjects lista de materias que el usuario quiere llevar
     * @return lista de todas las combinaciones de horarios sin conflictos
     */
    public List<List<Course>> generateSchedules(List<String> desiredSubjects) {
        List<List<Course>> validSchedules = new ArrayList<>();
        
        // Agrupar cursos por materia para acceso rápido
        Map<String, List<Course>> coursesBySubject = allCourses.stream()
                .filter(c -> desiredSubjects.contains(c.getSubject()))
                .collect(Collectors.groupingBy(Course::getSubject));

        // Verificar que todas las materias deseadas existan
        for (String subject : desiredSubjects) {
            if (!coursesBySubject.containsKey(subject) || coursesBySubject.get(subject).isEmpty()) {
                System.out.println("⚠ No se encontraron cursos para la materia: " + subject);
                return validSchedules;
            }
        }

        // Iniciar el algoritmo de backtracking
        backtrack(desiredSubjects, 0, new ArrayList<>(), validSchedules, coursesBySubject);
        return validSchedules;
    }

    /**
     * Algoritmo de Backtracking recursivo
     * 
     * @param desiredSubjects lista de materias deseadas
     * @param index índice actual en la lista de materias
     * @param currentSchedule horario en construcción
     * @param validSchedules acumulador de horarios válidos
     * @param coursesBySubject mapa de materias a sus grupos disponibles
     */
    private void backtrack(List<String> desiredSubjects, int index, List<Course> currentSchedule, 
                           List<List<Course>> validSchedules, Map<String, List<Course>> coursesBySubject) {
        // Caso base: Si hemos seleccionado un grupo para cada materia
        if (index == desiredSubjects.size()) {
            validSchedules.add(new ArrayList<>(currentSchedule));
            return;
        }

        String currentSubject = desiredSubjects.get(index);
        List<Course> options = coursesBySubject.get(currentSubject);

        // Probar cada grupo disponible para la materia actual
        for (Course option : options) {
            if (!hasConflict(currentSchedule, option)) {
                // ELEGIR: agregar este grupo al horario
                currentSchedule.add(option);
                
                // EXPLORAR: continuar con la siguiente materia
                backtrack(desiredSubjects, index + 1, currentSchedule, validSchedules, coursesBySubject);
                
                // DESHACER (Backtrack): quitar el grupo para probar otras opciones
                currentSchedule.remove(currentSchedule.size() - 1);
            }
        }
    }

    /**
     * Verifica si un nuevo curso tiene conflicto con los cursos ya seleccionados
     */
    private boolean hasConflict(List<Course> currentSchedule, Course newCourse) {
        for (Course existing : currentSchedule) {
            if (existing.overlaps(newCourse)) {
                return true;
            }
        }
        return false;
    }
}
