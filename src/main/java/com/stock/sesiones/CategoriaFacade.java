package com.stock.sesiones;

import com.stock.entidades.Categoria;
import com.stock.util.NegocioException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CategoriaFacade extends AbstractFacade<Categoria> {

    @PersistenceContext(unitName = "StockControlPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoriaFacade() {
        super(Categoria.class);
    }

    public List<Categoria> findAllOrdenado() {
        return em.createQuery("SELECT c FROM Categoria c ORDER BY c.nombre", Categoria.class).getResultList();
    }

    /** No se borra una categoría que todavía tiene productos. */
    public void eliminar(Integer id) {
        Categoria c = find(id);
        if (c == null) return;
        long cant = em.createQuery("SELECT COUNT(p) FROM Producto p WHERE p.categoria = :c", Long.class)
                .setParameter("c", c).getSingleResult();
        if (cant > 0) {
            throw new NegocioException("No se puede eliminar la categoría '" + c.getNombre()
                    + "' porque tiene " + cant + " producto(s).");
        }
        em.remove(c);
    }
}
