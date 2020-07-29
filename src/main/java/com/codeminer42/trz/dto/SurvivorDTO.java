package com.codeminer42.trz.dto;

import com.codeminer42.trz.models.Survivor.Gender;
import com.codeminer42.trz.models.Survivor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SurvivorDTO {

    protected Long id;
    protected String name;
    protected Integer age;
    protected Gender gender;
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
    private Double latitude;
    private Double longitude;
}
