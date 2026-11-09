package com.example.models;

/**
 * Datos del producto que llegan en la peticion.
 * fechaCompra se recibe con el formato yyyy-MM-dd (ej. "2026-10-15").
 */
public class ProductoDTO {

    private String nombre;

    private String marca;

    private String fechaCompra;

    public ProductoDTO() {

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

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(String fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

}
