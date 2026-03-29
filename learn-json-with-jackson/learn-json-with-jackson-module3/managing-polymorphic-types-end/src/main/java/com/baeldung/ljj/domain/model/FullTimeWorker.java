package com.baeldung.ljj.domain.model;

public class FullTimeWorker extends Worker {
    private double monthlySalary;

    public FullTimeWorker() {
    }

    public FullTimeWorker(int id, String name, double monthlySalary) {
        super(id, name);
        this.monthlySalary = monthlySalary;
    }

    public double getMonthlySalary() {
        return monthlySalary;
    }
}
