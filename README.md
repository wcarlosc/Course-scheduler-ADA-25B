# Sistema de Planificación de Horarios Académicos

Sistema que genera horarios académicos personalizados utilizando **4 algoritmos diferentes** con comparación de rendimiento.

## 🎯 Características

- **4 Algoritmos Implementados**:
  - 🔄 **Backtracking** (NP-Complete): Búsqueda exhaustiva con retroceso
  - ✂️ **Divide & Conquer**: División recursiva del problema
  - 🎯 **Algoritmo Goloso**: Selección por heurística local  
  - 📊 **Programación Dinámica**: Optimización con memorización

- **Sistema de Benchmark**: Comparación automática de:
  - ⏱️ Tiempo de ejecución
  - 💾 Uso de memoria
  - 🔢 Número de soluciones encontradas
  - ⭐ Calidad de las soluciones

- **GUI Moderna**: Interfaz gráfica con JavaFX (Naranja + Negro)
- **Múltiples horarios**: Un curso puede tener varias sesiones semanales
- **Detección de conflictos**: Identifica automáticamente solapamientos

## 📋 Requisitos

- Java 17 o superior
- Maven 3.6+

## 🚀 Ejecución

### Modo GUI (Recomendado):
```bash
cd scheduler-project
mvn clean javafx:run
```

### Modo Consola:
```bash
mvn exec:java -Dexec.args="--console"
```

## 📂 Estructura del Proyecto

```
scheduler-project/
├── src/main/
│   ├── java/com/scheduler/
│   │   ├── Main.java                    # Punto de entrada
│   │   ├── gui/
│   │   │   └── SchedulerGUI.java        # Interfaz gráfica
│   │   ├── model/
│   │   │   ├── Course.java              # Modelo de curso
│   │   │   └── TimeSlot.java            # Modelo de bloque horario
│   │   ├── logic/
│   │   │   ├── DataLoader.java          # Carga de datos JSON
│   │   │   └── Scheduler.java           # Backtracking
│   │   └── algorithm/
│   │       ├── AlgorithmType.java       # Enum de algoritmos
│   │       ├── BenchmarkResult.java     # Resultados de benchmark
│   │ Uso de la GUI

1. **Seleccionar Algoritmo**: Elige uno de los 4 algoritmos disponibles
2. **Comparar Todos**: Activa el checkbox para ejecutar benchmark completo
3. **Seleccionar Materias**: Marca las materias que deseas cursar
4. **Generar**: Click en "🔍 Generar" para obtener resultados
5. **Ver Resultados**: 
   - Modo individual: Muestra horarios + métricas
   - Modo comparación: Tabla comparativa + gráficos + análisis

## 🔧 Personalización

### Agregar cursos
Edita [courses.json](src/main/resources/courses.json)
**Descripción**: Búsqueda exhaustiva con retroceso  
**Ventajas**: Encuentra todas las soluciones posibles  
**Desventajas**: Alto costo temporal en problemas grandes  
**Complejidad**: O(k^n) donde n = materias, k = grupos

### 2. Divide & Conquer
**Descripción**: Divide las materias en subgrupos, resuelve recursivamente y combina  
**Estrategia**:
- DIVIDE: Partir materias en dos mitades
- CONQUER: Resolver cada mitad recursivamente
- COMBINE: Unir soluciones compatibles

**Ventajas**: Paralelizable, bueno para conjuntos grandes  
**Complejidad**: O(n·k^n) en peor caso

### 3. Algoritmo Goloso (Greedy)
**Descripción**: Sel GUI

```
1. Seleccionar: "Comparar todos los algoritmos" ✓
2. Materias: Matematica ✓, Fisica ✓, Programacion ✓
3. Click: "🔍 Generar"

RESULTADOS:
╔═══════════════════════════════════════════════════════════╗
║              COMPARACIÓN DE ALGORITMOS                    ║
╠═══════════════════════════════════════════════════════════╣
║ Backtracking        │ 4 sols │  12ms │ 1.2MB │ 85.5     ║
║ Divide & Conquer    │ 4 sols │   8ms │ 2.1MB │ 85.5     ║
║ Greedy              │ 3 sols │   2ms │ 0.5MB │ 72.0     ║
║ Dynamic Programming │ 4 sols │  15ms │ 3.8MB │ 85.5     ║
╠═══════════════════════════════════════════════════════════╣
║ ⚡ MÁS RÁPIDO: Greedy (2ms)                               ║
║ 💾 MENOS MEMORIA: Greedy (0.5MB)                          ║
║ ⭐ MEJOR CALIDAD: Backtracking (85.5)                     ║
╚═══════════════════════════════════════════════════════════╝
```

## 🎨 Diseño GUI

- **Colores**: Naranja (#FF6B35, #F7931E) + Negro (#1a1a1a)
- **Layout**: Panel izquierdo (configuración) + Panel derecho (resultados)
- **Efectos**: Hover animations, tarjetas con sombras
- **Responsive**: Scrollbars automáticos

## 📈 Experimentos Sugeridos

### Experimento 1: Escalabilidad
```
Materias: 3, 4, 5, 6, 7
Medir: Tiempo vs. Número de materias
Resultado esperado: Crecimiento exponencial en Backtracking/DP,
                   lineal en Greedy
```

### Experimento 2: Densidad de Conflictos
```
Escenario A: Muchos grupos sin conflictos
Escenario B: Pocos grupos con muchos conflictos
Comparar: Calidad de soluciones entre algoritmos
```

### Experimento 3: Uso de Memoria
```
Materias: 6 con 4 grupos cada una
Monitorear: Memoria pico durante ejecución
Resultado esperado: DP > Backtracking > Divide&Conquer > Greedy
```

## 🧮 Complejidad Comparativa

| Algoritmo | Tiempo | Espacio | Soluciones |
|-----------|--------|---------|------------|
| Backtracking | O(k^n) | O(n) | Todas |
| Divide & Conquer | O(n·k^n) | O(n log n) | Todas |
| Greedy | O(n·k log k) | O(n) | Parciales (1-3) |
| Dynamic Prog. | O(n·2^k) | O(n·2^k) | Todas |

*n = número de materias, k = promedio de grupos por materia*
| Métrica | Descripción |
|---------|-------------|
| **Tiempo** | Milisegundos de ejecución |
| **Memoria** | MB consumidos durante ejecución |
| **Soluciones** | Cantidad de horarios válidos encontrados |
| **Calidad** | Score basado en diversidad y distribución (0-100) |

### Cálculo de Calidad
```
Calidad = (Diversidad × 0.6) + (Dispersión × 0.4)

Diversidad = min(num_soluciones × 10, 100)
Dispersión = 100 - (desviación_estándar_días × 20)
```

## 💡 Cómo funciona

1. **Carga de datos**: Lee los cursos disponibles desde `courses.json`
2. **Entrada del usuario**: Solicita las materias deseadas
3. **Backtracking**: Explora todas las combinaciones de grupos
4. **Validación**: Descarta combinaciones con conflictos de horario
5. **Resultados**: Muestra todas las opciones válidas

## 🔧 Personalización

### Agregar cursos
Edita `src/main/resources/courses.json`:

```json
{
  "id": "CURSO-GRUPO",
  "subject": "NombreMateria",
  "group": "A",
  "schedules": [
    { "day": "Lunes", "start": 8, "end": 10 },
    { "day": "Miercoles", "start": 8, "end": 10 }
  ]
}
```

## 📊 Complejidad

- **Temporal**: O(k^n) donde n = número de materias, k = promedio de grupos
- **Espacial**: O(n) para la pila de recursión

## 📝 Ejemplo de uso

```
Materias disponibles:
  • Matematica (2 grupos)
  • Fisica (2 grupos)
  • Programacion (2 grupos)

Ingrese las materias: Matematica, Fisica

Horarios encontrados: 3
Tiempo: 5ms

📅 Opción 1:
   Matematica (A): Lunes 8:00-10:00, Miercoles 8:00-10:00
   Fisica (B): Martes 10:00-12:00, Jueves 10:00-12:00
```

## 🧮 Algoritmo NP

El problema de planificación de horarios es **NP-Completo**. Este proyecto usa:

- **Backtracking**: Búsqueda exhaustiva con poda
- **Exploración del espacio de soluciones**: Árbol de decisión
- **Poda por restricciones**: Descarta ramas inválidas tempranamente
