package com.queryGenerator.QueryGenerator.entity;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "ESTADO_ACTIVO_FIJO")
//@AttributeOverride(name = "id", column = @Column(name = "ID_ESTADO_ACTIVO_FIJO"))
//@SequenceGenerator(name = "sequence", sequenceName = "SQ_ESTADO_ACTIVO_FIJO", allocationSize = 1)

public class EstadoActivoFijo implements Serializable {

    @Id
    @Column(name = "ID_ESTADO_ACTIVO_FIJO")
    private int id;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "ACTIVO")
    private boolean activo;

    public EstadoActivoFijo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "EstadoActivoFijo{" + "id=" + id + ", nombre=" + nombre + ", activo=" + activo + '}';
    }


}
