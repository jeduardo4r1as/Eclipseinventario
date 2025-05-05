package com.inventory.appinventario.dao;

import com.inventory.appinventario.model.Comercio;
import com.inventory.appinventario.util.ConexionBD;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ComercioDAO {

    private ConexionBD conexionBD = new ConexionBD();

    public ComercioDAO() {
    }

    public Comercio getComercio() throws SQLException {
        Comercio c = new Comercio();
        this.conexionBD.conectar();
        ResultSet rs = this.conexionBD.CONSULTAR("SELECT * FROM comercio;");
        if (rs.next()) {
            c.setIdcomercio(rs.getInt("idcomercio"));
            c.setNombre(rs.getString("nombre"));
            c.setNit(rs.getString("nit"));
            c.setDireccion(rs.getString("direccion"));
            c.setTelefono(rs.getString("telefono"));
            c.setIva(rs.getInt("iva"));
        }
        this.conexionBD.CERRAR();
        return c;
    }


}
