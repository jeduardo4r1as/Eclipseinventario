package com.inventory.appinventario.dao;

import com.inventory.appinventario.model.Producto;
import com.inventory.appinventario.model.Usuario;
import com.inventory.appinventario.util.ConexionBD;
import com.inventory.appinventario.util.Sesion;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private ConexionBD conexionBD;

    private String mensajeLogin;

    public String getMensajeLogin() {
        return mensajeLogin;
    }

    public UsuarioDAO(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    public Usuario getUsuario(String username, String password) throws SQLException {
        Usuario usuario = null;
        mensajeLogin = null; // Reiniciar el mensaje cada vez que se hace login

        String sql = "SELECT * FROM usuario WHERE username = ?";
        PreparedStatement pst = conexionBD.getConexion().prepareStatement(sql);
        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            // El usuario existe
            String passwordDB = rs.getString("password");
            String estadoDB = rs.getString("estado");

            if (!passwordDB.equals(password)) {
                mensajeLogin = "Contraseña incorrecta.";
            } else if (!"ACTIVO".equalsIgnoreCase(estadoDB)) {
                mensajeLogin = "El usuario está inactivo.";
            } else {
                // Usuario correcto, todo válido
                usuario = new Usuario();
                usuario.setIdusuario(rs.getInt("idusuario"));
                usuario.setUsername(rs.getString("username"));
                usuario.setPassword(rs.getString("password"));
                usuario.setRol(rs.getString("rol"));
                usuario.setNombre(rs.getString("nombre"));

                Sesion.getSesion(usuario);
            }
        } else {
            mensajeLogin = "El usuario no existe.";
        }

        rs.close();
        pst.close();

        return usuario;
    }

    public boolean delete(int idusuario) throws SQLException{
        String sql = "UPDATE  usuario SET estado='INACTIVO' WHERE idusuario="+idusuario+"";
        return conexionBD.GUARDAR(sql);
    }


    public boolean activar(int idusuario) throws SQLException{
        String sql = "UPDATE  usuario SET estado='ACTIVO' WHERE idusuario="+idusuario+"";
        return conexionBD.GUARDAR(sql);
    }





    public List<Usuario> ListUsuarios() throws SQLException {

        List<Usuario> usuario=new ArrayList<>();

        Usuario p=null;
        ResultSet rs=conexionBD.CONSULTAR("Select * from usuario");

        while (rs.next()){
            p=new Usuario();

            p.setIdusuario(rs.getInt("idusuario"));
            p.setUsername(rs.getString("username").trim());
            p.setPassword(rs.getString("password").trim());
            p.setNombre(rs.getString("nombre").trim());
            p.setRol(rs.getString("rol").trim());
            p.setEstado(rs.getString("estado").trim());


            usuario.add(p);

        }

        return  usuario;

    }

    public int guardar(Usuario usu) throws SQLException {
        String sql = "";
        if (usu.getIdusuario() == 0) {
            sql = "INSERT INTO usuario ( ";
            sql += " username, password, nombre ,rol , estado";
            sql += ") VALUES (";
            sql += " '"+usu.getUsername()+"' , '"+usu.getPassword()+"' , '"+usu.getNombre()+"', '"+usu.getRol()+"' , 'ACTIVO'";
            sql += ")";
        } else {
            sql = "UPDATE usuario SET \n"
                    + "	username='"+usu.getUsername()+"' , password='"+usu.getPassword()+"', nombre='"+usu.getNombre()+"' "+", rol='"+usu.getRol()+"' \n"
                    + " WHERE idusuario="+usu.getIdusuario()+";";
        }

        PreparedStatement pst = conexionBD.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);



        int insert = pst.executeUpdate();
        if (usu.getIdusuario() == 0) {
            ResultSet rs = pst.getGeneratedKeys();
            rs.next();
            insert = rs.getInt(1);
            rs.close();
        }
        return insert;

    }

    public Usuario getById(int idusuario) throws SQLException {
        Usuario p = null;
        ResultSet rs = this.conexionBD.CONSULTAR("SELECT * FROM usuario WHERE idusuario="+idusuario);
        if(rs.next()){
            p = new Usuario();
            p.setIdusuario(idusuario);
            p.setUsername(rs.getString("username").trim());
            p.setPassword(rs.getString("password").trim());
            p.setNombre(rs.getString("nombre").trim());
            p.setRol(rs.getString("rol").trim());
        }
        return p;
    }



}