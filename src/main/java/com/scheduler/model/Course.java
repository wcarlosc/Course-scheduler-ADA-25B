package com.scheduler.model;

import java.util.List;

public class Course {
    private String id;
    private String subject;
    private String group;
    private int year;
    private List<TimeSlot> schedules;

    public Course(String id, String subject, String group, int year, List<TimeSlot> schedules) {
        this.id = id;
        this.subject = subject;
        this.group = group;
        this.year = year;
        this.schedules = schedules;
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getGroup() {
        return group;
    }

    public int getYear() {
        return year;
    }

    public List<TimeSlot> getSchedules() {
        return schedules;
    }

    /**
     * Verifica si este curso tiene conflicto de horario con otro curso
     * Retorna true si alguno de los bloques horarios se solapan
     */
    public boolean overlaps(Course other) {
        for (TimeSlot ts1 : this.schedules) {
            for (TimeSlot ts2 : other.schedules) {
                if (ts1.overlaps(ts2)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(subject).append(" (").append(group).append("): ");
        for (int i = 0; i < schedules.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(schedules.get(i));
        }
        return sb.toString();
    }
}
