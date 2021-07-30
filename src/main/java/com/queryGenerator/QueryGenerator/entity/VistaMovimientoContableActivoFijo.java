/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.queryGenerator.QueryGenerator.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import org.hibernate.annotations.Subselect;

/**
 *
 * @author Luis Andrés Fallas Valenciano
 */
@Entity
@Subselect("select * from VISTA_MOV_CONTA_ACTIVO_FIJO")
public class VistaMovimientoContableActivoFijo implements Serializable {

    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "FECHA_APLICACION")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAplicacion;

    @Column(name = "NUMERO_ASIENTO")
    private String numeroAsiento;

    public VistaMovimientoContableActivoFijo() {
    }

    public VistaMovimientoContableActivoFijo(Date fechaAplicacion, String numeroAsiento) {
        this.fechaAplicacion = fechaAplicacion;
        this.numeroAsiento = numeroAsiento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(Date fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public String getNumeroAsiento() {
        return numeroAsiento;
    }

    public void setNumeroAsiento(String numeroAsiento) {
        this.numeroAsiento = numeroAsiento;
    }
}
