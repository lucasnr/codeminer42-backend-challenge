package com.codeminer42.trz.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @Setter
    private Long id;
    private String name;

    @Column(columnDefinition = "int2")
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Setter
    private Double latitude;
    @Setter
    private Double longitude;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "survivor_id", insertable = false, updatable = false)
    @Setter
    private Set<InventoryEntry> inventory = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "reported_id", insertable = false, updatable = false)
    @Setter
    private Set<Report> reports = new HashSet<>();

    @Builder
    private Survivor(Long id, String name, Integer age, Gender gender, Double latitude, Double longitude, Set<InventoryEntry> inventory) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.latitude = latitude;
        this.longitude = longitude;
        this.inventory = inventory;
        this.reports = new HashSet<>();
    }

    public void addItem(Item item, Integer amount) {
        for (InventoryEntry entry : inventory) {
            if (entry.getItem().equals(item)) {
                entry.increase(amount);
                return;
            }
        }

        InventoryEntry entry = InventoryEntry.builder().amount(amount).item(item).build();
        entry.setSurvivorId(this.id);
        inventory.add(entry);
    }

    public void removeItem(Item item, Integer amount) {
        for (InventoryEntry entry : inventory) {
            if (entry.getItem().equals(item)) {
                entry.decrease(amount);
                break;
            }
        }
    }

    public boolean isInfected() {
        return this.reports.size() >= 5;
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}

