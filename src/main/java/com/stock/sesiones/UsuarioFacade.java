package com.stock.sesiones;

import com.stock.entidades.Usuario;
import com.stock.util.NegocioException;
import com.stock.util.Seguridad;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "StockControlPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }

    public List<Usuario> findAllOrdenado() {
        return em.createQuery("SELECT u FROM Usuario u ORDER BY u.nombre", Usuario.class).getResultList();
    }

    public Usuario findByEmail(String email) {
        List<Usuario> r = em.createQuery("SELECT u FROM Usuario u WHERE LOWER(u.email) = :e", Usuario.class)
                .setParameter("e", email == null ? "" : email.trim().toLowerCase())
                .getResultList();
        return r.isEmpty() ? null : r.get(0);
    }

    /** Devuelve el usuario si el email y la contraseña son correctos y está activo; si no, null. */
    public Usuario login(String email, String password) {
        Usuario u = findByEmail(email);
        if (u != null && u.isActivo() && Seguridad.verificar(password, u.getPasswordHash())) {
            return u;
        }
        return null;
    }

    /** Alta o modificación. Si password viene vacío al editar, se conserva la anterior. */
    public void guardar(Usuario u, String password) {
        u.setEmail(u.getEmail().trim().toLowerCase());
        Usuario otro = findByEmail(u.getEmail());
        if (otro != null && !otro.getId().equals(u.getId())) {
            throw new NegocioException("Ya existe un usuario con el email " + u.getEmail() + ".");
        }
        if (password != null && !password.isBlank()) {
            if (password.length() < 6) {
                throw new NegocioException("La contraseña debe tener al menos 6 caracteres.");
            }
            u.setPasswordHash(Seguridad.hashear(password));
        } else if (u.getId() == null) {
            throw new NegocioException("La contraseña es obligatoria para un usuario nuevo.");
        }
        if (u.getId() == null) {
            em.persist(u);
        } else {
            em.merge(u);
        }
    }

    /** Si el usuario registró movimientos no se borra: se desactiva (no puede volver a entrar). */
    public void eliminar(Integer id) {
        Usuario u = find(id);
        if (u == null) return;
        long movs = em.createQuery("SELECT COUNT(m) FROM Movimiento m WHERE m.usuario = :u", Long.class)
                .setParameter("u", u).getSingleResult();
        if (movs > 0) {
            u.setActivo(false);
        } else {
            em.remove(u);
        }
    }
}
