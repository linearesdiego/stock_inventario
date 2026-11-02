package com.stock.entidades;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "proveedor")
public class Proveedor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "razon_social", nullable = false, length = 120)
    private String razonSocial;

    @Column(unique = true, length = 13)
    private String cuit;

    @Column(length = 30)
    private String telefono;

    @Column(length = 120)
    private String email;

    // Productos que entrega este proveedor
    @OneToMany(mappedBy = "proveedor")
    private List<Producto> productos;

    public Proveedor() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }

    @Override
    public String toString() {
        return razonSocial;
    }
}
