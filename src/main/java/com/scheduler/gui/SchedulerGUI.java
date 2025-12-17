package com.scheduler.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.scheduler.algorithm.AlgorithmBenchmark;
import com.scheduler.algorithm.AlgorithmType;
import com.scheduler.algorithm.BenchmarkResult;
import com.scheduler.logic.DataLoader;
import com.scheduler.model.Course;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class SchedulerGUI extends Application {

    private List<Course> allCourses;
    private VBox subjectsContainer;
    private VBox resultsContainer;
    private Label statusLabel;
    private Map<String, CheckBox> subjectCheckBoxes;
    private ComboBox<AlgorithmType> algorithmSelector;
    private CheckBox compareAllCheckbox;
    private Spinner<Integer> maxSolutionsSpinner;

    // Colores
    private static final String PRIMARY_COLOR = "#FF6B35"; 
    private static final String SECONDARY_COLOR = "#F7931E"; 
    private static final String DARK_BG = "#1a1a1a"; 
    private static final String CARD_BG = "#2d2d2d"; 
    private static final String TEXT_COLOR = "#ffffff"; 
    private static final String ACCENT_COLOR = "#ff8c5a"; 

    @Override
    public void start(Stage primaryStage) {
        // Cargar datos
        DataLoader loader = new DataLoader();
        allCourses = loader.loadCourses("courses.json");

        if (allCourses.isEmpty()) {
            showError("Error al cargar los cursos desde el archivo JSON");
            return;
        }

        // Layout principal
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + DARK_BG + ";");

        // Header
        VBox header = createHeader();
        root.setTop(header);

        // Panel izquierdo - Selección de materias
        VBox leftPanel = createLeftPanel();
        root.setLeft(leftPanel);

        // Panel derecho - Resultados
        VBox rightPanel = createRightPanel();
        root.setCenter(rightPanel);

        // Escena
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setTitle("Sistema de generacion de Horarios");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: linear-gradient(to right, " + PRIMARY_COLOR + ", " + SECONDARY_COLOR + ");");

        Label title = new Label("🎓 Sistema de generacion de Horarios");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        // Label subtitle = new Label("Comparación de Algoritmos");
        // subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        // subtitle.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-opacity: 0.9;");

        header.getChildren().addAll(title);
        return header;
    }

    private VBox createLeftPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(500);
        panel.setStyle("-fx-background-color: " + CARD_BG + ";");

        // Título
        Label title = new Label(" Configuración");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

        // Selector de algoritmo
        Label algoLabel = new Label("Tipo de solución:");
        algoLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        algoLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        algorithmSelector = new ComboBox<>();
        algorithmSelector.getItems().addAll(AlgorithmType.values());
        algorithmSelector.setValue(AlgorithmType.BACKTRACKING);
        algorithmSelector.setPrefWidth(320);
        algorithmSelector.setStyle(
            "-fx-background-color: " + TEXT_COLOR  + ";" +
            "-fx-text-fill: " + TEXT_COLOR + ";" 
        );

        // Checkbox para comparar todos
        compareAllCheckbox = new CheckBox("Solo ejecutar test");
        compareAllCheckbox.setFont(Font.font("Segoe UI", 12));
        compareAllCheckbox.setStyle("-fx-text-fill: " + ACCENT_COLOR + ";");
        compareAllCheckbox.setOnAction(e -> {
            algorithmSelector.setDisable(compareAllCheckbox.isSelected());
            maxSolutionsSpinner.setDisable(compareAllCheckbox.isSelected());
        });

        // Selector de cantidad de soluciones
        Label limitLabel = new Label("Máx. soluciones:");
        limitLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        limitLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        maxSolutionsSpinner = new Spinner<>();
        maxSolutionsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 5));
        maxSolutionsSpinner.setEditable(true);
        maxSolutionsSpinner.setPrefWidth(320);

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: " + PRIMARY_COLOR + ";");

        // Título materias
        Label subjectsTitle = new Label(" Cursos:");
        subjectsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        subjectsTitle.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        // Contenedor de checkboxes organizados por año
        subjectsContainer = new VBox(15);
        subjectsContainer.setPadding(new Insets(10, 0, 0, 0));
        
        subjectCheckBoxes = new HashMap<>();
        
        // Organizar cursos por año
        Map<Integer, List<Course>> coursesByYear = allCourses.stream()
                .collect(Collectors.groupingBy(Course::getYear));
        
        String[] yearLabels = {"1er Año", "2do Año", "3er Año", "4to Año", "5to Año"};
        
        for (int year = 1; year <= 5; year++) {
            List<Course> coursesInYear = coursesByYear.getOrDefault(year, new java.util.ArrayList<>());
            if (!coursesInYear.isEmpty()) {
                // Título del año
                Label yearLabel = new Label(" " + yearLabels[year - 1]);
                yearLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
                yearLabel.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
                subjectsContainer.getChildren().add(yearLabel);
                
                // Obtener materias únicas para este año
                List<String> subjectsInYear = coursesInYear.stream()
                        .map(Course::getSubject)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());
                
                GridPane yearBox = new GridPane();
                yearBox.setHgap(10);
                yearBox.setVgap(8);
                yearBox.setPadding(new Insets(5, 0, 10, 15));
                
                int row = 0;
                int col = 0;
                
                for (String subject : subjectsInYear) {
                    long groupCount = coursesInYear.stream()
                            .filter(c -> c.getSubject().equals(subject))
                            .count();

                    CheckBox checkBox = new CheckBox(subject + " (" + groupCount + " grupos)");
                    checkBox.setFont(Font.font("Segoe UI", 13));
                    checkBox.setWrapText(true);
                    checkBox.setMaxWidth(220);
                    checkBox.setStyle(
                        "-fx-text-fill: " + TEXT_COLOR + ";" +
                        "-fx-cursor: hand;"
                    );
                    
                    // Estilo personalizado para el checkbox
                    checkBox.setOnMouseEntered(e -> 
                        checkBox.setStyle("-fx-text-fill: " + ACCENT_COLOR + "; -fx-cursor: hand;")
                    );
                    checkBox.setOnMouseExited(e -> 
                        checkBox.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-cursor: hand;")
                    );

                    subjectCheckBoxes.put(subject, checkBox);
                    yearBox.add(checkBox, col, row);
                    
                    // Alternar entre columnas
                    col++;
                    if (col >= 2) {
                        col = 0;
                        row++;
                    }
                }
                
                subjectsContainer.getChildren().add(yearBox);
            }
        }

        ScrollPane scrollPane = new ScrollPane(subjectsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + CARD_BG + "; -fx-background-color: " + CARD_BG + ";");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Botones
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        Button generateButton = createStyledButton("Generar", PRIMARY_COLOR);
        generateButton.setOnAction(e -> generateSchedules());

        Button clearButton = createStyledButton("Limpiar", PRIMARY_COLOR);
        clearButton.setOnAction(e -> clearSelection());

        buttons.getChildren().addAll(generateButton, clearButton);

        // Status label
        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Segoe UI", 12));
        statusLabel.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
        statusLabel.setWrapText(true);

        panel.getChildren().addAll(title, algoLabel, algorithmSelector, compareAllCheckbox, 
                                   limitLabel, maxSolutionsSpinner,
                                   sep1, subjectsTitle, scrollPane, buttons, statusLabel);
        return panel;
    }

    private VBox createRightPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: " + DARK_BG + ";");

        Label title = new Label("Horarios Generados");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

        resultsContainer = new VBox(15);
        resultsContainer.setPadding(new Insets(10));

        Label placeholder = new Label("Selecciona las materias y presiona 'Generar'");
        placeholder.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        placeholder.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-opacity: 0.6;");
        placeholder.setAlignment(Pos.CENTER);
        resultsContainer.getChildren().add(placeholder);

        ScrollPane scrollPane = new ScrollPane(resultsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + DARK_BG + "; -fx-background-color: " + DARK_BG + ";");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        panel.getChildren().addAll(title, scrollPane);
        return panel;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        button.setPrefWidth(150);
        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10 20;"
        );

        return button;
    }

    private void generateSchedules() {
        List<String> selectedSubjects = subjectCheckBoxes.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (selectedSubjects.isEmpty()) {
            statusLabel.setText(" Selecciona al menos una materia");
            statusLabel.setStyle("-fx-text-fill: " + SECONDARY_COLOR + ";");
            return;
        }

        if (compareAllCheckbox.isSelected()) {
            runComparison(selectedSubjects);
        } else {
            runSingleAlgorithm(selectedSubjects);
        }
    }

    private void runSingleAlgorithm(List<String> selectedSubjects) {
        statusLabel.setText(" Generando horarios...");
        statusLabel.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

        AlgorithmBenchmark benchmark = new AlgorithmBenchmark(allCourses);
        BenchmarkResult result = benchmark.runBenchmark(algorithmSelector.getValue(), selectedSubjects);

        displaySingleResult(result);
    }

    private void runComparison(List<String> selectedSubjects) {
        statusLabel.setText(" Comparando algoritmos...");
        statusLabel.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

        AlgorithmBenchmark benchmark = new AlgorithmBenchmark(allCourses);
        List<BenchmarkResult> results = benchmark.runAllBenchmarks(selectedSubjects);

        displayComparisonResults(results, benchmark);
    }

    private void displaySingleResult(BenchmarkResult result) {
        resultsContainer.getChildren().clear();

        if (result.getSolutions().isEmpty()) {
            statusLabel.setText(" No se encontraron horarios compatibles");
            statusLabel.setStyle("-fx-text-fill: " + SECONDARY_COLOR + ";");

            Label noResults = new Label("No hay combinaciones de horarios sin conflictos");
            noResults.setFont(Font.font("Segoe UI", 14));
            noResults.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-opacity: 0.6;");
            resultsContainer.getChildren().add(noResults);
            return;
        }

        statusLabel.setText(String.format(" %s: %d solución(es) en %dms, %.2fMB, calidad: %.2f",
            result.getAlgorithmType().getDisplayName(),
            result.getSolutionsFound(),
            result.getExecutionTimeMs(),
            result.getMemoryUsedMB(),
            result.getQualityScore()));
        statusLabel.setStyle("-fx-text-fill: #4CAF50;");

        // Mostrar métricas
        VBox metricsCard = createMetricsCard(result);
        resultsContainer.getChildren().add(metricsCard);

        // Mostrar horarios
        int maxSolutions = maxSolutionsSpinner.getValue();
        int limit = Math.min(result.getSolutions().size(), maxSolutions);

        for (int i = 0; i < limit; i++) {
            HBox scheduleCard = createScheduleCard(result.getSolutions().get(i), i + 1);
            resultsContainer.getChildren().add(scheduleCard);
        }

        if (result.getSolutions().size() > limit) {
             Label moreLabel = new Label("... y " + (result.getSolutions().size() - limit) + " soluciones más.");
             moreLabel.setFont(Font.font("Segoe UI", 14));
             moreLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-padding: 10;");
             resultsContainer.getChildren().add(moreLabel);
        }
    }

    private void displayComparisonResults(List<BenchmarkResult> results, AlgorithmBenchmark benchmark) {
        resultsContainer.getChildren().clear();

        statusLabel.setText(" Comparación completada");
        statusLabel.setStyle("-fx-text-fill: #4CAF50;");

        // Tabla comparativa
        VBox comparisonCard = createComparisonCard(results, benchmark);
        resultsContainer.getChildren().add(comparisonCard);

        // Gráficos de comparación
        VBox chartsCard = createChartsCard(results);
        resultsContainer.getChildren().add(chartsCard);
    }

    private VBox createMetricsCard(BenchmarkResult result) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + SECONDARY_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;"
        );

        Label header = new Label(" Rendimiento");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        header.setStyle("-fx-text-fill: " + SECONDARY_COLOR + ";");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        addMetricRow(grid, 0, "Tiempo:", result.getExecutionTimeMs() + " ms");
        addMetricRow(grid, 1, "Memoria:", String.format("%.2f MB", result.getMemoryUsedMB()));
        addMetricRow(grid, 2, "Soluciones:", String.valueOf(result.getSolutionsFound()));
        addMetricRow(grid, 3, "Calidad:", String.format("%.2f puntos", result.getQualityScore()));

        card.getChildren().addAll(header, grid);
        return card;
    }

    private void addMetricRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        labelNode.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

        Label valueNode = new Label(value);
        valueNode.setFont(Font.font("Segoe UI", 13));
        valueNode.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");

        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private VBox createComparisonCard(List<BenchmarkResult> results, AlgorithmBenchmark benchmark) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + PRIMARY_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;"
        );

        Label header = new Label("Comparación de Algoritmos");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        header.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

        // Tabla de comparación
        GridPane table = new GridPane();
        table.setHgap(15);
        table.setVgap(8);
        table.setPadding(new Insets(10));

        // Headers
        String[] headers = {"Algoritmo", "Soluciones", "Tiempo", "Memoria", "Calidad"};
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i]);
            h.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            h.setStyle("-fx-text-fill: " + SECONDARY_COLOR + ";");
            table.add(h, i, 0);
        }

        // Datos
        for (int i = 0; i < results.size(); i++) {
            BenchmarkResult r = results.get(i);
            
            Label algo = new Label(r.getAlgorithmType().getDisplayName());
            algo.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
            table.add(algo, 0, i + 1);

            Label sols = new Label(String.valueOf(r.getSolutionsFound()));
            sols.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
            table.add(sols, 1, i + 1);

            Label time = new Label(r.getExecutionTimeMs() + " ms");
            time.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
            table.add(time, 2, i + 1);

            Label mem = new Label(String.format("%.2f MB", r.getMemoryUsedMB()));
            mem.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
            table.add(mem, 3, i + 1);

            Label qual = new Label(String.format("%.2f", r.getQualityScore()));
            qual.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
            table.add(qual, 4, i + 1);
        }

        // Análisis
        TextArea analysis = new TextArea(benchmark.generateComparisonReport(results));
        analysis.setEditable(false);
        analysis.setPrefRowCount(12);
        analysis.setWrapText(true);
        analysis.setStyle(
            "-fx-control-inner-background: " + DARK_BG + ";" +
            "-fx-text-fill: " + TEXT_COLOR + ";" +
            "-fx-font-family: 'Consolas', 'Monaco', monospace;" +
            "-fx-font-size: 11px;"
        );

        card.getChildren().addAll(header, table, new Separator(), analysis);
        return card;
    }

    private VBox createChartsCard(List<BenchmarkResult> results) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + ACCENT_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;"
        );

        Label header = new Label("Comparativa");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        header.setStyle("-fx-text-fill: " + ACCENT_COLOR + ";");

        // Gráficos de barras simples con caracteres
        VBox charts = new VBox(15);
        charts.setPadding(new Insets(10));

        // Tiempo
        charts.getChildren().add(createBarChart("Tiempo (ms)", results,
            r -> (double) r.getExecutionTimeMs()));

        // Memoria
        charts.getChildren().add(createBarChart("Memoria (MB)", results,
            r -> r.getMemoryUsedMB()));

        // Calidad
        charts.getChildren().add(createBarChart("Calidad", results,
            BenchmarkResult::getQualityScore));

        card.getChildren().addAll(header, charts);
        return card;
    }

    private VBox createBarChart(String title, List<BenchmarkResult> results,
                                java.util.function.Function<BenchmarkResult, Double> valueExtractor) {
        VBox chart = new VBox(5);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        titleLabel.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
        chart.getChildren().add(titleLabel);

        double max = results.stream()
            .mapToDouble(valueExtractor::apply)
            .max()
            .orElse(1.0);

        for (BenchmarkResult r : results) {
            HBox bar = new HBox(5);
            bar.setAlignment(Pos.CENTER_LEFT);

            Label name = new Label(r.getAlgorithmType().name().substring(0, 
                Math.min(12, r.getAlgorithmType().name().length())));
            name.setPrefWidth(120);
            name.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-size: 11px;");

            double value = valueExtractor.apply(r);
            int barLength = (int) ((value / max) * 40);
            
            Label barLabel = new Label("█".repeat(Math.max(1, barLength)));
            barLabel.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

            Label valueLabel = new Label(String.format("%.2f", value));
            valueLabel.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-font-size: 11px;");

            bar.getChildren().addAll(name, barLabel, valueLabel);
            chart.getChildren().add(bar);
        }

        return chart;
    }

    private HBox createScheduleCard(List<Course> schedule, int optionNumber) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + PRIMARY_COLOR + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;"
        );

        // Parte Gráfica (Izquierda)
        VBox graphicContainer = new VBox(5);
        Label graphicTitle = new Label("Opción " + optionNumber);
        graphicTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        graphicTitle.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");
        
        Pane graphicSchedule = createGraphicalSchedule(schedule);
        graphicContainer.getChildren().addAll(graphicTitle, graphicSchedule);

        // Parte Textual (Derecha)
        VBox textDetails = createTextualDetails(schedule);
        HBox.setHgrow(textDetails, Priority.ALWAYS);

        card.getChildren().addAll(graphicContainer, textDetails);
        return card;
    }

    private Pane createGraphicalSchedule(List<Course> schedule) {
        Pane pane = new Pane();
        double timeColWidth = 60; // Ancho columna de horas
        double headerHeight = 35; // Altura del encabezado de días
        double colWidth = 110; // Ancho por día
        double startHour = 7; // 7:00 AM
        double endHour = 22; // 10:00 PM
        double pxPerMin = 0.7; // Escala vertical mejorada
        double totalHeight = headerHeight + ((endHour - startHour + 1) * 60 * pxPerMin);
        double totalWidth = timeColWidth + (colWidth * 6);
        
        pane.setPrefSize(totalWidth, totalHeight); 
        pane.setMinSize(totalWidth, totalHeight);
        pane.setStyle("-fx-background-color: #2a2a2a; -fx-border-color: " + PRIMARY_COLOR + "; -fx-border-width: 2;");

        // Fondo para área de horas
        Rectangle timeBg = new Rectangle(0, 0, timeColWidth, totalHeight);
        timeBg.setFill(javafx.scene.paint.Color.web("#1f1f1f"));
        pane.getChildren().add(timeBg);

        // Fondo para encabezado de días
        Rectangle headerBg = new Rectangle(timeColWidth, 0, totalWidth - timeColWidth, headerHeight);
        headerBg.setFill(javafx.scene.paint.Color.web("#1f1f1f"));
        pane.getChildren().add(headerBg);

        // Etiquetas de días
        String[] days = {"LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            dayLabel.setTextFill(javafx.scene.paint.Color.web(PRIMARY_COLOR));
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPrefWidth(colWidth);
            dayLabel.setLayoutX(timeColWidth + (i * colWidth));
            dayLabel.setLayoutY(8);
            dayLabel.setStyle("-fx-alignment: center;");
            pane.getChildren().add(dayLabel);
        }

        // Línea horizontal separadora del encabezado
        Line headerLine = new Line(0, headerHeight, totalWidth, headerHeight);
        headerLine.setStroke(javafx.scene.paint.Color.web(PRIMARY_COLOR));
        headerLine.setStrokeWidth(2);
        pane.getChildren().add(headerLine);

        // Líneas verticales para separar días
        for (int i = 0; i <= 6; i++) {
            double x = timeColWidth + (i * colWidth);
            Line vLine = new Line(x, headerHeight, x, totalHeight);
            vLine.setStroke(javafx.scene.paint.Color.web("#3a3a3a"));
            vLine.setStrokeWidth(i == 0 ? 2 : 1);
            pane.getChildren().add(vLine);
        }

        // Dibujar líneas de tiempo y etiquetas
        for (int h = (int)startHour; h <= endHour; h++) {
            double y = headerHeight + ((h * 60 - (startHour * 60)) * pxPerMin);
            
            // Etiqueta de hora
            Label timeLabel = new Label(String.format("%02d:00", h));
            timeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
            timeLabel.setTextFill(javafx.scene.paint.Color.web("#CCCCCC"));
            timeLabel.setLayoutX(8);
            timeLabel.setLayoutY(y - 8);
            pane.getChildren().add(timeLabel);

            // Línea horizontal
            Line line = new Line(timeColWidth, y, totalWidth, y);
            line.setStroke(javafx.scene.paint.Color.web("#3a3a3a"));
            line.setStrokeWidth(0.5);
            pane.getChildren().add(line);
            
            // Líneas de media hora (más tenues)
            if (h < endHour) {
                double halfY = y + (30 * pxPerMin);
                Line halfLine = new Line(timeColWidth, halfY, totalWidth, halfY);
                halfLine.setStroke(javafx.scene.paint.Color.web("#2d2d2d"));
                halfLine.setStrokeWidth(0.5);
                halfLine.getStrokeDashArray().addAll(3d, 3d);
                pane.getChildren().add(halfLine);
            }
        }

        // Asignar colores a materias
        Map<String, String> subjectColors = new HashMap<>();
        String[] palette = {"#FF6B6B", "#4ECDC4", "#45B7D1", "#FFA07A", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E2"};
        
        int idx = 0;
        for (Course course : schedule) {
             if (!subjectColors.containsKey(course.getSubject())) {
                 subjectColors.put(course.getSubject(), palette[idx % palette.length]);
                 idx++;
             }
        }

        // Dibujar bloques de cursos
        for (Course course : schedule) {
            String color = subjectColors.get(course.getSubject());
            for (var slot : course.getSchedules()) {
                int dayIdx = getDayIndex(slot.getDay());
                if (dayIdx == -1) continue;

                double x = timeColWidth + (dayIdx * colWidth) + 2;
                double y = headerHeight + ((slot.getStart() - (startHour * 60)) * pxPerMin);
                double height = (slot.getEnd() - slot.getStart()) * pxPerMin;

                // Asegurar que no se salga del panel
                if (y < headerHeight) y = headerHeight;
                if (y + height > totalHeight) height = totalHeight - y;

                // Rectángulo del curso con sombra
                Rectangle shadow = new Rectangle(x + 2, y + 2, colWidth - 6, height);
                shadow.setFill(javafx.scene.paint.Color.web("#000000"));
                shadow.setOpacity(0.3);
                shadow.setArcWidth(8);
                shadow.setArcHeight(8);
                pane.getChildren().add(shadow);

                Rectangle rect = new Rectangle(x, y, colWidth - 6, height);
                rect.setFill(javafx.scene.paint.Color.web(color));
                rect.setArcWidth(8);
                rect.setArcHeight(8);
                rect.setOpacity(0.9);
                rect.setStroke(javafx.scene.paint.Color.web("#ffffff"));
                rect.setStrokeWidth(1.5);
                pane.getChildren().add(rect);

                // Etiqueta del curso
                String courseName = course.getSubject();
                if (courseName.length() > 25) {
                    courseName = courseName.substring(0, 22) + "...";
                }
                
                Label lbl = new Label(courseName + "\nGrupo " + course.getGroup());
                lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
                lbl.setTextFill(javafx.scene.paint.Color.WHITE);
                lbl.setWrapText(true);
                lbl.setMaxWidth(colWidth - 10);
                lbl.setLayoutX(x + 5);
                lbl.setLayoutY(y + 5);
                lbl.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 2, 0, 1, 1);");
                pane.getChildren().add(lbl);
            }
        }

        return pane;
    }

    private VBox createTextualDetails(List<Course> schedule) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setPrefWidth(300);
        
        Label title = new Label("Detalles de Grupos");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        title.setStyle("-fx-text-fill: " + SECONDARY_COLOR + ";");
        box.getChildren().add(title);

        for (Course course : schedule) {
            VBox item = new VBox(3);
            item.setStyle("-fx-border-color: #555; -fx-border-width: 0 0 1 0; -fx-padding: 5;");
            
            Label name = new Label(course.getSubject());
            name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            name.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
            name.setWrapText(true);
            
            Label group = new Label("Grupo: " + course.getGroup());
            group.setStyle("-fx-text-fill: " + ACCENT_COLOR + ";");

            StringBuilder times = new StringBuilder();
            for(var slot : course.getSchedules()) {
                times.append(slot.toString()).append("\n");
            }
            Label timeLbl = new Label(times.toString());
            timeLbl.setStyle("-fx-text-fill: #aaa; -fx-font-size: 11px;");

            item.getChildren().addAll(name, group, timeLbl);
            box.getChildren().add(item);
        }
        return box;
    }

    private int getDayIndex(String day) {
        if (day == null) return -1;
        switch(day.toLowerCase().trim()) {
            case "lunes": return 0;
            case "martes": return 1;
            case "miercoles": return 2;
            case "miércoles": return 2;
            case "jueves": return 3;
            case "viernes": return 4;
            case "sabado": return 5;
            case "sábado": return 5;
            default: return -1;
        }
    }

    private void clearSelection() {
        subjectCheckBoxes.values().forEach(cb -> cb.setSelected(false));
        resultsContainer.getChildren().clear();
        
        Label placeholder = new Label("Selecciona las materias y presiona 'Generar'");
        placeholder.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        placeholder.setStyle("-fx-text-fill: " + TEXT_COLOR + "; -fx-opacity: 0.6;");
        resultsContainer.getChildren().add(placeholder);
        
        statusLabel.setText("");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
