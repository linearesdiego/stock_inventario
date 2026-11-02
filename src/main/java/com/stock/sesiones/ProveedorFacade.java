package com.stock.sesiones;

import com.stock.entidades.Proveedor;
import com.stock.util.NegocioException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ProveedorFacade extends AbstractFacade<Proveedor> {

    @PersistenceContext(unitName = "StockControlPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProveedorFacade() {
        super(Proveedor.class);
    }

    public List<Proveedor> findAllOrdenado() {
        return em.createQuery("SELECT p FROM Proveedor p ORDER BY p.razonSocial", Proveedor.class).getResultList();
    }

    /** No se borra un proveedor que todavía entrega productos. */
    public void eliminar(Integer id) {
        Proveedor p = find(id);
        if (p == null) return;
        long cant = em.createQuery("SELECT COUNT(pr) FROM Producto pr WHERE pr.proveedor = :p", Long.class)
                .setParameter("p", p).getSingleResult();
        if (cant > 0) {
            throw new NegocioException("No se puede eliminar el proveedor '" + p.getRazonSocial()
                    + "' porque tiene " + cant + " producto(s) asociados.");
        }
        em.remove(p);
    }
}
