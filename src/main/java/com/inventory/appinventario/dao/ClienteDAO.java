package com.inventory.appinventario.dao;

import com.inventory.appinventario.model.Cliente;
import com.inventory.appinventario.model.Usuario;
import com.inventory.appinventario.util.ConexionBD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private ConexionBD conexionBD;

    public ClienteDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    public int guardar(Cliente c) throws SQLException {

        String validacionSql = "SELECT idcliente FROM cliente WHERE (LOWER(correo) = LOWER(?) OR numerodocumento = ?)";
        if (c.getIdcliente() != 0) {
            validacionSql += " AND idcliente != ?";
        }

        PreparedStatement validarStmt = conexionBD.getConexion().prepareStatement(validacionSql);
        validarStmt.setString(1, c.getCorreo());
        validarStmt.setString(2, c.getNumerodocumento());
        if (c.getIdcliente() != 0) {
            validarStmt.setInt(3, c.getIdcliente());
        }

        ResultSet rsVal = validarStmt.executeQuery();
        if (rsVal.next()) {
            rsVal.close();
            validarStmt.close();
            throw new SQLException("Ya existe un cliente con el mismo correo o número de documento.");
        }
        rsVal.close();
        validarStmt.close();

        // 2. Crear o actualizar cliente
        String sql;
        if (c.getIdcliente() == 0) {
            sql = "INSERT INTO cliente(nombrecliente, apellidocliente, direccioncliente, telefonocliente, numerodocumento, correo) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE cliente SET nombrecliente = ?, apellidocliente = ?, direccioncliente = ?, "
                    + "telefonocliente = ?, numerodocumento = ?, correo = ? WHERE idcliente = ?";
        }

        PreparedStatement pst = conexionBD.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pst.setString(1, c.getNombrecliente());
        pst.setString(2, c.getApellidocliente());
        pst.setString(3, c.getDireccioncliente());
        pst.setString(4, c.getTelefonocliente());
        pst.setString(5, c.getNumerodocumento());
        pst.setString(6, c.getCorreo());

        if (c.getIdcliente() != 0) {
            pst.setInt(7, c.getIdcliente());
        }

        int resultado = pst.executeUpdate();

        if (c.getIdcliente() == 0) {
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                resultado = rs.getInt(1);
            }
            rs.close();
        }

        pst.close();
        return resultado;
    }

    public List<Cliente> getAll() throws SQLException{
        List<Cliente> lista = new ArrayList<>();
        Cliente c = null;
        ResultSet rs = this.conexionBD.CONSULTAR("SELECT idcliente, nombrecliente, apellidocliente FROM cliente;");
        while(rs.next()){
            c = new Cliente();
            c.setIdcliente(rs.getInt("idcliente"));
            c.setNombrecliente(rs.getString("nombrecliente").trim());
            c.setApellidocliente(rs.getString("apellidocliente").trim());

            lista.add(c);
        }
        return lista;
    }

    public List<Cliente> ListCliente() throws SQLException {

        List<Cliente> cliente=new ArrayList<>();

        Cliente p=null;
        ResultSet rs=conexionBD.CONSULTAR("Select * from cliente");

        while (rs.next()){
            p=new Cliente();

            p.setIdcliente(rs.getInt("idcliente"));
            p.setNombrecliente(rs.getString("nombrecliente").trim());
            p.setApellidocliente(rs.getString("apellidocliente").trim());
            p.setDireccioncliente(rs.getString("direccioncliente").trim());
            p.setTelefonocliente(rs.getString("telefonocliente").trim());
            p.setNumerodocumento(rs.getString("numerodocumento").trim());
            p.setCorreo(rs.getString("correo").trim());


            cliente.add(p);

        }

        return  cliente;


    }

    public Cliente getById(int idcliente) throws SQLException {
        Cliente p = null;
        ResultSet rs = this.conexionBD.CONSULTAR("SELECT * FROM cliente WHERE idcliente="+idcliente);
        if(rs.next()){
            p = new Cliente();
            p.setIdcliente(idcliente);
            p.setNombrecliente(rs.getString("nombrecliente").trim());
            p.setApellidocliente(rs.getString("apellidocliente").trim());
            p.setDireccioncliente(rs.getString("direccioncliente").trim());
            p.setTelefonocliente(rs.getString("telefonocliente").trim());
            p.setNumerodocumento(rs.getString("numerodocumento").trim());
            p.setCorreo(rs.getString("correo").trim());
        }
        return p;
    }

}
