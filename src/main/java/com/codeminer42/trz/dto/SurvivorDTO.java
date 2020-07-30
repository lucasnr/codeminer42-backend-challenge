package com.codeminer42.trz.dto;

import com.codeminer42.trz.models.Survivor.Gender;
import com.codeminer42.trz.models.Survivor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class SurvivorDTO {

    protected Long id;

    @NotNull
    @Size(min = 3, max = 50)
    protected String name;

    @NotNull
    @Positive
    @Min(5)
    @Max(999)
    protected Integer age;

    @NotNull
    protected Gender gender;

    @NotNull
    @Valid
    protected Location location;

    public SurvivorDTO(Survivor survivor) {
        this.id = survivor.getId();
        this.name = survivor.getName();
        this.age = survivor.getAge();
        this.gender = survivor.getGender();
        this.location = Location.builder()
                .latitude(survivor.getLatitude())
                .longitude(survivor.getLongitude())
                .build();
    }
}

@Data
@Builder
class Location {
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
}
