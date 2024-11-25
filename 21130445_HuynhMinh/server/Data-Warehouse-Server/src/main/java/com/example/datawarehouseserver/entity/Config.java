package com.example.datawarehouseserver.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "configs")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "source_path", length = 255)
    String sourcePath;

    @Column(name = "backup_path", length = 255)
    String backupPath;

    @ManyToOne
    @JoinColumn(name = "staging_config")
    DBConfig stagingConfig;

    @ManyToOne
    @JoinColumn(name = "datawarehouse_config")
    DBConfig datawarehouseConfig;

    @Column(name = "staging_table", length = 50)
    String stagingTable;

    @Column(name = "datawarehouse_table", length = 50)
    String datawarehouseTable;

    @Column
    Long period;

    @Column(length = 50)
    String version;

    @Column(name = "is_active")
    Boolean isActive;

    @Column(name = "insert_date", updatable = false)
    Timestamp insertDate;

    @Column(name = "update_date")
    Timestamp updateDate;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "config", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<Log> log;
}
