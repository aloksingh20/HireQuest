package com.alok91340.gethired.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Employee name cannot be blank")
    private String employeeName;

    @NotBlank(message = "HR person name cannot be blank")
    private String hrPersonName;

    @NotNull(message = "Time cannot be null")
    private LocalTime meetingTime;

    @NotNull(message = "Date cannot be null")
    private LocalDate meetingDate;

    // Example URL pattern validation
    // Adjust the regex as needed
    @NotBlank(message = "Link cannot be blank")
    private String meetingLink;

    private boolean hasTakenPlace; // Better reflects the intention

    // Constructors, getters, setters
}
