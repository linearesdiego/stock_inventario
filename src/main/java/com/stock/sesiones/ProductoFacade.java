package com.stock.sesiones;

import com.stock.entidades.Producto;
import com.stock.entidades.TipoMovimiento;
import com.stock.entidades.Usuario;
import com.stock.util.NegocioException;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ProductoFacade extends AbstractFacade<Producto> {

    @PersistenceContext(unitName = "StockControlPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @EJB
    private MovimientoFacade movimientoFacade;

    public ProductoFacade() {
        super(Producto.class);
    }

    /** Productos activos, con filtro opcional por texto (código o nombre) y categoría. */
    public List<Producto> buscarActivos(String texto, Integer idCategoria) {
        String q = "SELECT p FROM Producto p WHERE p.activo = true"
                + (texto != null && !texto.isBlank()
                        ? " AND (LOWER(p.codigo) LIKE :t OR LOWER(p.nombre) LIKE :t)" : "")
                + (idCategoria != null ? " AND p.categoria.id = :cat" : "")
                + " ORDER BY p.nombre";
        var query = em.createQuery(q, Producto.class);
        if (texto != null && !texto.isBlank()) {
            query.setParameter("t", "%" + texto.trim().toLowerCase() + "%");
        }
        if (idCategoria != null) {
            query.setParameter("cat", idCategoria);
        }
        return query.getResultList();
    }

    public List<Producto> findActivos() {
        return buscarActivos(null, null);
    }

    public List<Producto> findInactivos() {
        return em.createQuery("SELECT p FROM Producto p WHERE p.activo = false ORDER BY p.nombre", Producto.class)
                .getResultList();
    }

    public Producto findByCodigo(String codigo) {
        List<Producto> r = em.createQuery("SELECT p FROM Producto p WHERE p.codigo = :c", Producto.class)
                .setParameter("c", codigo.trim()).getResultList();
        return r.isEmpty() ? null : r.get(0);
    }

    /** Regla 3: productos activos con stock_actual <= stock_minimo (los más críticos primero). */
    public List<Producto> findBajoMinimo() {
        return em.createQuery("SELECT p FROM Producto p WHERE p.activo = true "
                + "AND p.stockActual <= p.stockMinimo "
                + "ORDER BY (p.stockActual - p.stockMinimo), p.nombre", Producto.class)
                .getResultList();
    }

    public long contarBajoMinimo() {
        return em.createQuery("SELECT COUNT(p) FROM Producto p WHERE p.activo = true "
                + "AND p.stockActual <= p.stockMinimo", Long.class).getSingleResult();
    }

    /** Valor total del inventario: suma de precio x stock de los productos activos. */
    public BigDecimal valorizacionTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Producto p : findActivos()) {
            total = total.add(p.getValorizado());
        }
        return total;
    }

    /** Alta o modificación con las validaciones del producto. */
    public void guardar(Producto p) {
        if (p.getCodigo() == null || p.getCodigo().isBlank()) {
            throw new NegocioException("El código es obligatorio.");
        }
        if (p.getNombre() == null || p.getNombre().isBlank()) {
            throw new NegocioException("El nombre es obligatorio.");
        }
        if (p.getPrecio() == null || p.getPrecio().signum() < 0) {
            throw new NegocioException("El precio no puede ser negativo.");
        }
        if (p.getStockMinimo() < 0 || p.getStockActual() < 0) {
            throw new NegocioException("El stock no puede ser negativo.");
        }
        p.setCodigo(p.getCodigo().trim());
        // Regla 4: código único
        Producto otro = findByCodigo(p.getCodigo());
        if (otro != null && !otro.getId().equals(p.getId())) {
            throw new NegocioException("Ya existe un producto con el código " + p.getCodigo()
                    + " (" + otro.getNombre() + ").");
        }
        if (p.getId() == null) {
            em.persist(p);
        } else {
            em.merge(p);
        }
    }

    /**
     * Alta de un producto nuevo. Si trae stock inicial, se registra como una ENTRADA
     * para que quede en el historial. Todo en la misma transacción: si falla una parte, no se guarda nada.
     */
    public void crear(Producto p, int stockInicial, Usuario usuario) {
        if (stockInicial < 0) {
            throw new NegocioException("El stock inicial no puede ser negativo.");
        }
        p.setStockActual(0);
        guardar(p);
        if (stockInicial > 0) {
            em.flush(); // asegura que el producto ya tenga id
            movimientoFacade.registrar(p.getId(), usuario, TipoMovimiento.ENTRADA,
                    stockInicial, "AJUSTE", "Stock inicial al dar de alta el producto");
        }
    }

    /**
     * Regla 5: si el producto tiene movimientos se marca como inactivo (baja lógica);
     * si nunca se movió, se borra de verdad.
     * @return true si se borró físicamente, false si quedó inactivo.
     */
    public boolean eliminar(Integer id) {
        Producto p = find(id);
        if (p == null) return true;
        long movs = em.createQuery("SELECT COUNT(m) FROM Movimiento m WHERE m.producto = :p", Long.class)
                .setParameter("p", p).getSingleResult();
        if (movs > 0) {
            p.setActivo(false);
            return false;
        }
        em.remove(p);
        return true;
    }

    public void reactivar(Integer id) {
        Producto p = find(id);
        if (p != null) p.setActivo(true);
    }
}
