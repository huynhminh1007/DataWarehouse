package com.example.datawarehouseserver.entity.datawarehouse;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Table(name = "dim_dates")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimDate {

    @Id
    @Column(name = "date_sk")
    Integer dateSk;

    @Column(name = "full_date")
    @Temporal(TemporalType.DATE)
    Date fullDate;

    @Column(name = "day_since_2005")
    Integer daySince2005;

    @Column(name = "month_since_2005")
    Integer monthSince2005;

    @Column(name = "day_of_week", length = 10)
    String dayOfWeek;

    @Column(name = "calendar_month", length = 15)
    String calendarMonth;

    @Column(name = "calendar_year")
    Integer calendarYear;

    @Column(name = "calendar_year_month", length = 255)
    String calendarYearMonth;

    @Column(name = "day_of_month")
    Integer dayOfMonth;

    @Column(name = "day_of_year")
    Integer dayOfYear;

    @Column(name = "week_of_year_sunday")
    Integer weekOfYearSunday;

    @Column(name = "year_week_sunday", length = 255)
    String yearWeekSunday;

    @Column(name = "week_sunday_start")
    @Temporal(TemporalType.DATE)
    Date weekSundayStart;

    @Column(name = "week_of_year_monday")
    Integer weekOfYearMonday;

    @Column(name = "year_week_monday", length = 255)
    String yearWeekMonday;

    @Column(name = "week_monday_start")
    @Temporal(TemporalType.DATE)
    Date weekMondayStart;

    @Column(name = "quarter_of_year", length = 255)
    String quarterOfYear;

    @Column(name = "quarter_since_2005")
    Integer quarterSince2005;

    @Column(name = "holiday", length = 255)
    String holiday;

    @Column(name = "date_type", length = 15)
    String dateType;
}