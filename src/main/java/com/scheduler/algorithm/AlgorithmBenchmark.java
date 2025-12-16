package com.scheduler.algorithm;

import java.util.List;

import com.scheduler.logic.Scheduler;
import com.scheduler.model.Course;


public class AlgorithmBenchmark {

    private List<Course> allCourses;

    public AlgorithmBenchmark(List<Course> allCourses) {
        this.allCourses = allCourses;
    }

    
    public BenchmarkResult runBenchmark(AlgorithmType algorithmType, List<String> desiredSubjects) {
        // Forzar garbage collection antes de medir
        System.gc();
        
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long startTime = System.nanoTime();

        List<List<Course>> solutions = null;

        switch (algorithmType) {
            case BACKTRACKING:
                Scheduler backtracking = new Scheduler(allCourses);
                solutions = backtracking.generateSchedules(desiredSubjects);
                break;

            case DIVIDE_CONQUER:
                DivideConquerScheduler divideConquer = new DivideConquerScheduler(allCourses);
                solutions = divideConquer.generateSchedules(desiredSubjects);
                break;

            case GREEDY:
                GreedyScheduler greedy = new GreedyScheduler(allCourses);
                solutions = greedy.generateSchedules(desiredSubjects);
                break;

            case DYNAMIC_PROGRAMMING:
                DynamicProgrammingScheduler dp = new DynamicProgrammingScheduler(allCourses);
                solutions = dp.generateSchedules(desiredSubjects);
                break;
        }

        long endTime = System.nanoTime();
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        long executionTimeMs = (endTime - startTime) / 1_000_000;
        long memoryUsed = Math.max(0, memoryAfter - memoryBefore);

        return new BenchmarkResult(algorithmType, solutions, executionTimeMs, memoryUsed);
    }

    /**
     * Ejecuta benchmark para todos los algoritmos
     */
    public List<BenchmarkResult> runAllBenchmarks(List<String> desiredSubjects) {
        return List.of(
            runBenchmark(AlgorithmType.BACKTRACKING, desiredSubjects),
            runBenchmark(AlgorithmType.DIVIDE_CONQUER, desiredSubjects),
            runBenchmark(AlgorithmType.GREEDY, desiredSubjects),
            runBenchmark(AlgorithmType.DYNAMIC_PROGRAMMING, desiredSubjects)
        );
    }

    
    public String generateComparisonReport(List<BenchmarkResult> results) {
        StringBuilder report = new StringBuilder();
        report.append("**************************************************************\n");
        report.append("                  REPORTE COMPARATIVO DE ALGORITMOS\n");
        report.append("**************************************************************\n");

        // Tabla de resultados
        report.append(String.format("%-25s %10s %12s %12s %10s\n", 
            "ALGORITMO", "SOLUCIONES", "TIEMPO (ms)", "MEMORIA (MB)", "CALIDAD"));
        report.append("─".repeat(75)).append("\n");

        for (BenchmarkResult result : results) {
            report.append(String.format("%-25s %10d %12d %12.2f %10.2f\n",
                result.getAlgorithmType().getDisplayName(),
                result.getSolutionsFound(),
                result.getExecutionTimeMs(),
                result.getMemoryUsedMB(),
                result.getQualityScore()));
        }

        report.append("\n");

        return report.toString();
    }
}
