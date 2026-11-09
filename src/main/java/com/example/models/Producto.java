package com.example.models;

import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;

/**
 * Entidad Producto: se almacena en su propia coleccion (PRODUCTO) de MongoDB.
 */
@NoSql(dataFormat = DataFormatType.MAPPED)
@Entity
@XmlRootElement
public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Field(name = "_id")
    private String id;

    private String nombre;

    private String marca;

    @Temporal(TemporalType.DATE)
    private Calendar fechaCompra;

    public Producto() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Calendar getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Calendar fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

}
