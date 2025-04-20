package com.example.demo.Evaluation.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="Average_Evaluation")
public class Average {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="average_evaluation_id")
    private int id;
    @Column(name="counselor_id")
    private int counselorId;
    @Column(name="average_rating")
    private double average;
    @Column(name="evaluation_count")
    private int count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(int counselorId) {
        this.counselorId = counselorId;
    }

    public int getCount() {
        return count;
    }

    public void setCount() {
        this.count ++;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(int rating) {
        this.average = (this.count*this.average+rating) / (this.count+1);
    }

    @Override
    public String toString() {
        return "Average{" +
                "id=" + id +
                ", counselorId=" + counselorId +
                ", average=" + average +
                ", count=" + count +
                '}';
    }
}
