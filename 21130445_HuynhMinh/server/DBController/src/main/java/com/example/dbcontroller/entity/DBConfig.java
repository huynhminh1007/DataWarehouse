package com.example.dbcontroller.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "db_configs")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DBConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String dbName;
    String url;
    String username;
    String password;
    String driverClassName;
}
