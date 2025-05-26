package com.inventory.appinventario.dao;

import com.inventory.appinventario.model.Caja;
import com.inventory.appinventario.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CajaDAO {

    private ConexionBD conexionBD;

    public CajaDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    public List<Caja> listarFacturas(LocalDate fecha) throws SQLException {
        List<Caja> lista = new ArrayList<>();
        String sql = "SELECT v.numerofactura, c.nombrecliente, c.correo, v.fechadeventa, u.nombre, v.subtotal, v.total " +
                "FROM cliente c " +
                "INNER JOIN venta v ON c.idcliente = v.idcliente " +
                "INNER JOIN usuario u ON u.idusuario = v.idusuario " +
                "WHERE DATE(v.fechadeventa) = ?";

        try (PreparedStatement pst = conexionBD.getConexion().prepareStatement(sql)) {
            pst.setDate(1, java.sql.Date.valueOf(fecha));
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Caja(
                            rs.getString("nombrecliente"),
                            rs.getString("correo"),
                            rs.getString("numerofactura"),
                            rs.getString("fechadeventa"),
                            rs.getDouble("total"),
                            rs.getString("nombre"),
                            rs.getDouble("subtotal")
                    ));
                }
            }
        }
        return lista;
    }
}
