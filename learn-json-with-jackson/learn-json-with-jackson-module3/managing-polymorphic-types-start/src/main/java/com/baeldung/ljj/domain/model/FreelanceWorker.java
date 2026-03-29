package com.baeldung.ljj.domain.model;

public class FreelanceWorker extends Worker {
    private double hourlyRate;

    public FreelanceWorker() {
    }

    public FreelanceWorker(int id, String name, double hourlyRate) {
        super(id, name);
        this.hourlyRate = hourlyRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}
