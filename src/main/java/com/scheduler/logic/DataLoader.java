package com.scheduler.logic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scheduler.model.Course;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataLoader {

    /**
     * Carga la lista de cursos desde un archivo JSON
     * @param filename nombre del archivo JSON en resources
     * @return lista de cursos disponibles
     */
    public List<Course> loadCourses(String filename) {
        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(filename)))) {
            Gson gson = new Gson();
            Type courseListType = new TypeToken<ArrayList<Course>>(){}.getType();
            return gson.fromJson(reader, courseListType);
        } catch (Exception e) {
            System.err.println("Error al cargar el archivo JSON: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
