package com.stock.sesiones;

import com.stock.entidades.Movimiento;
import com.stock.entidades.Producto;
import com.stock.entidades.TipoMovimiento;
import com.stock.entidades.Usuario;
import com.stock.util.NegocioException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class MovimientoFacade extends AbstractFacade<Movimiento> {

    @PersistenceContext(unitName = "StockControlPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MovimientoFacade() {
        super(Movimiento.class);
    }

    /**
     * Registra una entrada o salida y actualiza el stock del producto.
     *
     * Regla 2: todo pasa en UNA transacción. El método es de un @Stateless, así que el
     * contenedor (JTA) abre la transacción al entrar y hace commit al salir. Si se lanza
     * NegocioException se hace rollback: no queda ni el movimiento ni el cambio de stock.
     *
     * El producto se lee con bloqueo (PESSIMISTIC_WRITE = SELECT ... FOR UPDATE) para que
     * dos empleados que venden a la vez no dejen el stock negativo.
     */
    public Movimiento registrar(Integer idProducto, Usuario usuario, TipoMovimiento tipo,
                                int cantidad, String motivo, String observacion) {
        if (tipo == null) {
            throw new NegocioException("Elegí si es una entrada o una salida.");
        }
        if (cantidad <= 0) {
            throw new NegocioException("La cantidad tiene que ser mayor a cero.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new NegocioException("El motivo es obligatorio.");
        }
        Producto p = em.find(Producto.class, idProducto, LockModeType.PESSIMISTIC_WRITE);
        if (p == null || !p.isActivo()) {
            throw new NegocioException("El producto no existe o está dado de baja.");
        }

        if (tipo == TipoMovimiento.SALIDA) {
            // Regla 1: no se puede sacar más de lo que hay
            if (cantidad > p.getStockActual()) {
                throw new NegocioException("Stock insuficiente de " + p.getNombre()
                        + ": hay " + p.getStockActual() + " y querés sacar " + cantidad + ".");
            }
            p.setStockActual(p.getStockActual() - cantidad);
        } else {
            p.setStockActual(p.getStockActual() + cantidad);
        }

        Movimiento m = new Movimiento();
        m.setProducto(p);
        m.setUsuario(em.getReference(Usuario.class, usuario.getId()));
        m.setTipo(tipo);
        m.setCantidad(cantidad);
        m.setMotivo(motivo);
        m.setObservacion(observacion == null || observacion.isBlank() ? null : observacion.trim());
        m.setFecha(LocalDateTime.now());
        em.persist(m);
        return m;
    }

    /** Anula un movimiento cargado por error: lo marca como anulado y revierte el stock. */
    public void anular(Integer idMovimiento) {
        Movimiento m = find(idMovimiento);
        if (m == null) {
            throw new NegocioException("El movimiento no existe.");
        }
        if (m.isAnulado()) {
            throw new NegocioException("El movimiento ya estaba anulado.");
        }
        Producto p = em.find(Producto.class, m.getProducto().getId(), LockModeType.PESSIMISTIC_WRITE);
        if (m.getTipo() == TipoMovimiento.ENTRADA) {
            if (m.getCantidad() > p.getStockActual()) {
                throw new NegocioException("No se puede anular: esa mercadería ya salió y el stock quedaría negativo.");
            }
            p.setStockActual(p.getStockActual() - m.getCantidad());
        } else {
            p.setStockActual(p.getStockActual() + m.getCantidad());
        }
        m.setAnulado(true);
    }

    /** Movimientos entre dos fechas (incluidas), con filtro opcional por producto. */
    public List<Movimiento> findEntreFechas(LocalDate desde, LocalDate hasta, Integer idProducto) {
        String q = "SELECT m FROM Movimiento m WHERE m.fecha >= :desde AND m.fecha < :hasta"
                + (idProducto != null ? " AND m.producto.id = :prod" : "")
                + " ORDER BY m.fecha DESC";
        var query = em.createQuery(q, Movimiento.class)
                .setParameter("desde", desde.atStartOfDay())
                .setParameter("hasta", hasta.plusDays(1).atStartOfDay());
        if (idProducto != null) {
            query.setParameter("prod", idProducto);
        }
        return query.getResultList();
    }

    public List<Movimiento> findUltimos(int cantidad) {
        return em.createQuery("SELECT m FROM Movimiento m ORDER BY m.fecha DESC", Movimiento.class)
                .setMaxResults(cantidad).getResultList();
    }

    /**
     * Reporte: productos más vendidos en un período (salidas con motivo VENTA, sin anuladas).
     * Cada fila es Object[]: [0] = código, [1] = nombre, [2] = unidades vendidas.
     */
    public List<Object[]> masVendidos(LocalDate desde, LocalDate hasta, int limite) {
        return em.createQuery("SELECT p.codigo, p.nombre, SUM(m.cantidad) "
                + "FROM Movimiento m JOIN m.producto p "
                + "WHERE m.tipo = :tipo AND m.motivo = 'VENTA' AND m.anulado = false "
                + "AND m.fecha >= :desde AND m.fecha < :hasta "
                + "GROUP BY p.id, p.codigo, p.nombre "
                + "ORDER BY SUM(m.cantidad) DESC", Object[].class)
                .setParameter("tipo", TipoMovimiento.SALIDA)
                .setParameter("desde", desde.atStartOfDay())
                .setParameter("hasta", hasta.plusDays(1).atStartOfDay())
                .setMaxResults(limite)
                .getResultList();
    }
}
