package com.inventory.appinventario.dao;

import com.inventory.appinventario.model.Factura;
import com.inventory.appinventario.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAO {

    private final ConexionBD conexionBD;

    public FacturaDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
        this.conexionBD.conectar(); // Asegura que se conecte al instanciar el DAO
    }

    public List<Factura> listarFacturas() throws SQLException {
        List<Factura> facturas = new ArrayList<>();

        // Validar que la conexión esté activa
        Connection conn = conexionBD.getConexion();
        if (conn == null) {
            throw new SQLException("❌ No se pudo establecer conexión a la base de datos.");
        }

        String sql = """
            SELECT c.nombrecliente, v.numerofactura, v.fechadeventa, v.total
            FROM cliente c
            INNER JOIN venta v ON c.idcliente = v.idcliente
        """;

        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Factura factura = new Factura(
                        rs.getString("nombrecliente"),
                        rs.getString("numerofactura"),
                        rs.getString("fechadeventa"),
                        rs.getDouble("total")
                );
                facturas.add(factura);
            }
        }

        return facturas;
    }
}
