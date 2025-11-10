package com.vetcarepro.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vaccines")
public class Vaccine {

    @Id
    private String id;

    private String name;

    private String manufacturer;

    private String description;

    /**
     * Number of days the vaccine remains valid.
     */
    @Builder.Default
    private int validityDays = 365;

    /**
     * Number of days before expiration to trigger reminders.
     */
    @Builder.Default
    private int reminderWindowDays = 7;
}
