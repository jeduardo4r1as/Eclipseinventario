package com.inventory.appinventario.dao;

import com.inventory.appinventario.util.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class EstadisticasDAO {
    private final Connection connection;

    public EstadisticasDAO(Connection connection) {
        this.connection = connection;
    }

    public int obtenerTotalPrendasVendidas(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT SUM(cantidad) FROM detalleventa dv " +
                "JOIN venta v ON dv.idventa = v.idventa " +
                "WHERE v.fechadeventa BETWEEN ? AND ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(desde));
            stmt.setDate(2, Date.valueOf(hasta));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public double obtenerTotalIngresos(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT SUM(total) FROM venta " +
                "WHERE fechadeventa BETWEEN ? AND ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(desde));
            stmt.setDate(2, Date.valueOf(hasta));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        }
        return 0.0;
    }

    public Map<String, Integer> obtenerVentasPorCategoria(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT p.genero, SUM(dv.cantidad) AS total " +
                "FROM detalleventa dv " +
                "JOIN producto p ON dv.idproducto = p.idproducto " +
                "JOIN venta v ON dv.idventa = v.idventa " +
                "WHERE v.fechadeventa BETWEEN ? AND ? " +
                "GROUP BY p.genero";
        Map<String, Integer> datos = new HashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(desde));
            stmt.setDate(2, Date.valueOf(hasta));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                datos.put(rs.getString("genero"), rs.getInt("total"));
            }
        }
        return datos;
    }

    public Map<LocalDate, Double> obtenerVentasPorDia(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT DATE(fechadeventa) AS fecha, SUM(total) AS total_dia " +
                "FROM venta WHERE fechadeventa BETWEEN ? AND ? " +
                "GROUP BY fecha ORDER BY fecha;";
        Map<LocalDate, Double> datos = new LinkedHashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(desde));
            stmt.setDate(2, Date.valueOf(hasta));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                datos.put(rs.getDate("fecha").toLocalDate(), rs.getDouble("total_dia"));
            }
        }
        return datos;
    }

    public Map<String, Double> obtenerVentasPorUsuario(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT u.nombre, SUM(v.total) AS total " +
                "FROM venta v JOIN usuario u ON v.idusuario = u.idusuario " +
                "WHERE v.fechadeventa BETWEEN ? AND ? GROUP BY u.nombre;";
        Map<String, Double> datos = new LinkedHashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(desde));
            stmt.setDate(2, Date.valueOf(hasta));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                datos.put(rs.getString("nombre"), rs.getDouble("total"));
            }
        }
        return datos;
    }

    public Map<String, Integer> obtenerTopProductos(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT p.nombreproducto, SUM(dv.cantidad) AS total " +
                "FROM detalleventa dv JOIN producto p ON dv.idproducto = p.idproducto " +
                "JOIN venta v ON dv.idventa = v.idventa " +
                "WHERE v.fechadeventa BETWEEN ? AND ? GROUP BY p.nombreproducto " +
                "ORDER BY total DESC LIMIT 5;";
        Map<String, Integer> datos = new LinkedHashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(desde));
            stmt.setDate(2, Date.valueOf(hasta));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                datos.put(rs.getString("nombreproducto"), rs.getInt("total"));
            }
        }
        return datos;
    }

    public double obtenerTicketPromedio(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT AVG(total) AS promedio FROM venta WHERE fechadeventa BETWEEN ? AND ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(desde));
            stmt.setDate(2, Date.valueOf(hasta));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble("promedio");
        }
        return 0.0;
    }

    public Map<String, Integer> obtenerFormasDePago(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = "SELECT formadepago, COUNT(*) AS cantidad FROM venta " +
                "WHERE fechadeventa BETWEEN ? AND ? GROUP BY formadepago;";
        Map<String, Integer> datos = new LinkedHashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(desde));
            stmt.setDate(2, Date.valueOf(hasta));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                datos.put(rs.getString("formadepago"), rs.getInt("cantidad"));
            }
        }
        return datos;
    }
}
