package com.inventory.appinventario.util;


import com.inventory.appinventario.model.Usuario;

public class Sesion {

    private static Usuario usuario;

    public static Usuario getSesion(Usuario user) {
        if (usuario == null) {
            usuario = user;
        }
        return usuario;
    }
}
