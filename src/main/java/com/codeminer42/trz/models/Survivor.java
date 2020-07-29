package com.codeminer42.trz.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "survivor")
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Survivor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(columnDefinition = "int2")
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Double latitude;
    private Double longitude;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "survivor_id", insertable = false, updatable = false)
    private Set<InventoryEntry> inventory = new HashSet<>();

    @Builder
    public Survivor(Long id, String name, Integer age, Gender gender, Double latitude, Double longitude, Set<InventoryEntry> inventory) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.latitude = latitude;
        this.longitude = longitude;
        this.inventory = inventory;
    }

    public void addItem(Item item) {
        inventory.forEach(entry -> {
            if (entry.getItem().getId().equals(item.getId())) {
                entry.increase();
                return;
            }
        });

        inventory.add(InventoryEntry.builder().amount(1).item(item).build());
    }

    public void removeItem(Item item) {
        inventory.forEach(entry -> {
            if (entry.getItem().getId().equals(item.getId())) {
                entry.decrease();
                if (entry.getAmount() == 0)
                    inventory.remove(entry);

                return;
            }
        });
    }

    public boolean isInfected() {
        return false;
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}

