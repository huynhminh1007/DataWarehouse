package com.example.datawarehouseserver.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Entity
@Table(name = "logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Log implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id")
    Config config;

    @Column(name = "status", length = 100)
    String status;

    @Column(name = "message", columnDefinition = "TEXT")
    String message;

    @Column(name = "begin_date", updatable = false)
    Timestamp beginDate;

    @Column(name = "update_date")
    Timestamp updateDate;

    @Column(name = "level", length = 100)
    String level;

    @Override
    public Log clone() throws CloneNotSupportedException {
        return new Log(
                this.id,
                this.config,
                this.status,
                this.message,
                this.beginDate,
                this.updateDate,
                this.level
        );
    }
}
