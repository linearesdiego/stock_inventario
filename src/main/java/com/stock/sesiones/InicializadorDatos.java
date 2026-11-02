package com.stock.sesiones;

import com.stock.entidades.Rol;
import com.stock.entidades.Usuario;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.util.logging.Logger;

/**
 * Se ejecuta una sola vez al desplegar la aplicación.
 * Si la tabla usuario está vacía crea un administrador y un empleado de prueba,
 * así siempre se puede entrar la primera vez (las contraseñas se guardan hasheadas).
 */
@Singleton
@Startup
public class InicializadorDatos {

    private static final Logger LOG = Logger.getLogger(InicializadorDatos.class.getName());

    @EJB
    private UsuarioFacade usuarioFacade;

    @PostConstruct
    public void iniciar() {
        try {
            if (usuarioFacade.count() == 0) {
                crear("Administrador", "admin@stock.com", "admin123", Rol.ADMIN);
                crear("Empleado", "empleado@stock.com", "empleado123", Rol.EMPLEADO);
                LOG.info("StockControl: usuarios iniciales creados (admin@stock.com / empleado@stock.com)");
            }
        } catch (Exception e) {
            LOG.warning("StockControl: no se pudieron crear los usuarios iniciales: " + e.getMessage());
        }
    }

    private void crear(String nombre, String email, String password, Rol rol) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setRol(rol);
        usuarioFacade.guardar(u, password);
    }
}
