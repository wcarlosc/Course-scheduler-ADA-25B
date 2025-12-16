package com.scheduler;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.scheduler.gui.SchedulerGUI;
import com.scheduler.logic.DataLoader;
import com.scheduler.logic.Scheduler;
import com.scheduler.model.Course;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--console")) {
            runConsoleMode();
        } else {
            Application.launch(SchedulerGUI.class, args);
        }
    }

    private static void runConsoleMode() {
        System.out.println("****************************************");
        System.out.println("  Horarios Académicos");
        System.out.println("  Algoritmo: Backtracking (NP-Complete)");
        System.out.println("****************************************\n");

        // 1. Cargar cursos desde JSON
        DataLoader loader = new DataLoader();
        List<Course> allCourses = loader.loadCourses("courses.json");
        
        if (allCourses.isEmpty()) {
            System.err.println("Error: No se pudieron cargar los cursos.");
            return;
        }

        System.out.println("Cursos disponibles cargados: " + allCourses.size() + "\n");
        
        // Mostrar materias únicas disponibles
        List<String> availableSubjects = allCourses.stream()
                .map(Course::getSubject)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        System.out.println("Materias disponibles:");
        for (String subject : availableSubjects) {
            long groupCount = allCourses.stream()
                    .filter(c -> c.getSubject().equals(subject))
                    .count();
            System.out.println("  • " + subject + " (" + groupCount + " grupos)");
        }

        // 2. Obtener entrada del usuario
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n" + "─".repeat(55));
        System.out.print("Ingrese las materias que desea llevar:\n> ");
        String input = scanner.nextLine();
        
        List<String> desiredSubjects = Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (desiredSubjects.isEmpty()) {
            System.out.println("No se ingresaron materias.");
            return;
        }

        // 3. Ejecutar el algoritmo de planificación
        System.out.println("\nBuscando combinaciones de horarios compatibles...\n");
        Scheduler scheduler = new Scheduler(allCourses);
        
        long startTime = System.currentTimeMillis();
        List<List<Course>> solutions = scheduler.generateSchedules(desiredSubjects);
        long endTime = System.currentTimeMillis();

        // 4. Mostrar resultados
        System.out.println("****************************************");
        System.out.println(" RESULTADOS");
        System.out.println("****************************************");
        System.out.println("Horarios encontrados: " + solutions.size());
        System.out.println("Tiempo de ejecución: " + (endTime - startTime) + "ms");
        System.out.println("─".repeat(55) + "\n");
        
        if (solutions.isEmpty()) {
            System.out.println(" No se encontró ningún horario compatible.");
            System.out.println(" Las materias seleccionadas tienen conflictos.");
        } else {
            int count = 1;
            for (List<Course> schedule : solutions) {
                System.out.println("Opción " + count++ + ":");
                for (Course c : schedule) {
                    System.out.println("   " + c);
                }
                System.out.println();
            }
        }

        scanner.close();
    }
}
