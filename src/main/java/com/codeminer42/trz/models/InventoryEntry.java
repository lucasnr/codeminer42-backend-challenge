package com.codeminer42.trz.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "inventory_entry")
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class InventoryEntry implements Serializable {

    @EmbeddedId
    private InventoryEntryId id;

    @Column(columnDefinition = "int2")
    @Setter private Integer amount;

    @MapsId("itemId")
    @ManyToOne(fetch = FetchType.EAGER)
    @Setter private Item item;

    @Builder
    private InventoryEntry(Integer amount, Item item) {
        this.amount = amount;
        this.item = item;
    }

    public void increase(Integer amount) {
        this.setAmount(this.amount + amount);
    }

    public void decrease(Integer amount) {
        this.setAmount(this.amount - amount);
    }

    public void setSurvivorId(Long survivorId) {
        this.id = new InventoryEntryId(survivorId, this.item.getId());
    }
}


