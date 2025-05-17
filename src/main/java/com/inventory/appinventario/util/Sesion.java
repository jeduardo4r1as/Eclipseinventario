//package com.inventory.appinventario.util;
//
//
//import com.inventory.appinventario.model.Usuario;
//
//public class Sesion {
//
//    private static Usuario usuario;
//
//    public static Usuario getSesion(Usuario user) {
//        if (usuario == null) {
//            usuario = user;
//        }
//        return usuario;
//    }
//}


package com.inventory.appinventario.util;

import com.inventory.appinventario.model.Usuario;

public class Sesion {

    private static Usuario sesionActual;

    public static Usuario getSesion(Usuario u) {
        if (u != null) {
            sesionActual = u;
        }
        return sesionActual;
    }

    public static String getRol() {
        return sesionActual != null ? sesionActual.getRol() : null;
    }

    public static String getNombre() {
        return sesionActual != null ? sesionActual.getNombre() : null;
    }

    public static int getIdUsuario() {
        return sesionActual != null ? sesionActual.getIdusuario() : -1;
    }

    public static void cerrarSesion() {
        sesionActual = null;
    }
}
