package com.scheduler.algorithm;

import java.util.List;

import com.scheduler.model.Course;

public class BenchmarkResult {
    private AlgorithmType algorithmType;
    private List<List<Course>> solutions;
    private long executionTimeMs;
    private long memoryUsedBytes;
    private int solutionsFound;
    private double qualityScore;

    public BenchmarkResult(AlgorithmType algorithmType, List<List<Course>> solutions, 
                          long executionTimeMs, long memoryUsedBytes) {
        this.algorithmType = algorithmType;
        this.solutions = solutions;
        this.executionTimeMs = executionTimeMs;
        this.memoryUsedBytes = memoryUsedBytes;
        this.solutionsFound = solutions.size();
        this.qualityScore = calculateQualityScore(solutions);
    }

    private double calculateQualityScore(List<List<Course>> solutions) {
        if (solutions.isEmpty()) return 0.0;
        
        // Calidad basada en:
        // - Número de soluciones encontradas (diversidad)
        // - Distribución de horarios (evitar concentración)
        double diversityScore = Math.min(solutions.size() * 10.0, 100.0);
        
        // Calcular dispersión promedio de horarios
        double avgDispersion = solutions.stream()
            .mapToDouble(this::calculateScheduleDispersion)
            .average()
            .orElse(0.0);
        
        return (diversityScore * 0.6) + (avgDispersion * 0.4);
    }

    private double calculateScheduleDispersion(List<Course> schedule) {
        // Mide qué tan distribuidos están los horarios en la semana
        // Mejor puntuación para horarios más balanceados
        if (schedule.isEmpty()) return 0.0;
        
        int[] dayCount = new int[7]; // Lun-Dom
        String[] days = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
        
        for (Course course : schedule) {
            course.getSchedules().forEach(ts -> {
                for (int i = 0; i < days.length; i++) {
                    if (ts.getDay().equalsIgnoreCase(days[i])) {
                        dayCount[i]++;
                        break;
                    }
                }
            });
        }
        
        // Calcular desviación estándar (menor es mejor distribución)
        double mean = java.util.Arrays.stream(dayCount).average().orElse(0.0);
        double variance = java.util.Arrays.stream(dayCount)
            .mapToDouble(count -> Math.pow(count - mean, 2))
            .average()
            .orElse(0.0);
        double stdDev = Math.sqrt(variance);
        
        // Invertir para que menor desviación = mayor puntuación
        return Math.max(0, 100 - (stdDev * 20));
    }

    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    public List<List<Course>> getSolutions() {
        return solutions;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public long getMemoryUsedBytes() {
        return memoryUsedBytes;
    }

    public double getMemoryUsedMB() {
        return memoryUsedBytes / (1024.0 * 1024.0);
    }

    public int getSolutionsFound() {
        return solutionsFound;
    }

    public double getQualityScore() {
        return qualityScore;
    }

    @Override
    public String toString() {
        return String.format("%s: %d soluciones, %dms, %.2fMB, calidad: %.2f",
            algorithmType.getDisplayName(), solutionsFound, executionTimeMs, 
            getMemoryUsedMB(), qualityScore);
    }
}
