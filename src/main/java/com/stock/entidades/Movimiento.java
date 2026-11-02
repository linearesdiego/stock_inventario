package com.stock.entidades;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "movimiento")
public class Movimiento implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Quién registró el movimiento
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoMovimiento tipo;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private LocalDateTime fecha;

    // COMPRA, DEVOLUCION, AJUSTE (entradas) / VENTA, ROTURA, VENCIMIENTO, AJUSTE (salidas)
    @Column(nullable = false, length = 30)
    private String motivo;

    @Column(length = 255)
    private String observacion;

    // Un admin puede anular un movimiento cargado por error (se revierte el stock)
    @Column(nullable = false)
    private boolean anulado = false;

    public Movimiento() {
    }

    /** Fecha lista para mostrar en la JSP (fmt:formatDate no acepta LocalDateTime). */
    public String getFechaTexto() {
        return fecha == null ? "" : fecha.format(FORMATO);
    }

    public boolean isEntrada() {
        return tipo == TipoMovimiento.ENTRADA;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public boolean isAnulado() { return anulado; }
    public void setAnulado(boolean anulado) { this.anulado = anulado; }
}
