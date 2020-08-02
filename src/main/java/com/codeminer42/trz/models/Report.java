package com.codeminer42.trz.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name= "report")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report implements Serializable {
    @EmbeddedId
    private ReportId id;
}
