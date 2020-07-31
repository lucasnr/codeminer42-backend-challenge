package com.codeminer42.trz.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class InventoryEntryId implements Serializable {
    @Column(name= "survivor_id")
    private Long survivorId;

    @Column(name= "item_id")
    private Long itemId;
}
