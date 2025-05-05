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

        String sql = null;
        if (c.getIdcliente() == 0) {
            sql = "INSERT INTO public.cliente(\n"
                    + "	nombrecliente, apellidocliente, direccioncliente, telefonocliente, numerodocumento, correo)\n"
                    + "	VALUES ('"+c.getNombrecliente()+"', '"+c.getApellidocliente()+"', '"+c.getDireccioncliente()+"', '"+c.getTelefonocliente()+"', '"+c.getNumerodocumento()+"', '"+c.getCorreo()+"');";
        } else {
            sql = "UPDATE public.cliente\n" +
                    "	SET nombrecliente='"+c.getNombrecliente()+"', apellidocliente='"+c.getApellidocliente()+"', direccioncliente='"+c.getDireccioncliente()+"', "
                    + "telefonocliente='"+c.getTelefonocliente()+"', numerodocumento='"+c.getNumerodocumento()+"', correo='"+c.getCorreo()+"'\n"+
                    "	WHERE idcliente="+c.getIdcliente()+";";
        }
        System.out.println(sql);

        PreparedStatement pst = this.conexionBD.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        int n = pst.executeUpdate();
        if (c.getIdcliente() == 0) {
            ResultSet rs = pst.getGeneratedKeys();
            rs.next();
            n = rs.getInt(1);
        }
        return n;
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
