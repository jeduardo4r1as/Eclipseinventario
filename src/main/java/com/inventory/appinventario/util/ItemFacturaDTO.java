package com.inventory.appinventario.util;

public class ItemFacturaDTO {
    private Double cantidad;
    private String descripcion;
    private String talla;
    private Double precioUnitario;
    private Double total;

    public ItemFacturaDTO(Double cantidad, String descripcion, String talla, Double precioUnitario, Double total) {
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.talla = talla;
        this.precioUnitario = precioUnitario;
        this.total = total;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}

