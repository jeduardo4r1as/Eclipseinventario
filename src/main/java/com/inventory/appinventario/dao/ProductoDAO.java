package com.inventory.appinventario.dao;

import com.inventory.appinventario.model.Producto;
import com.inventory.appinventario.util.ConexionBD;
import javafx.geometry.Pos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
  private ConexionBD conexionbd;

  public ProductoDAO(ConexionBD conexionbd){
      this.conexionbd=conexionbd;
  }

  public List<Producto> getProdusctos() throws SQLException {

      List<Producto> productos=new ArrayList<>();

      Producto p=null;
      ResultSet rs=conexionbd.CONSULTAR("Select * from producto");

      while (rs.next()){
         p=new Producto();

          p.setIdproducto(rs.getInt("idproducto"));
          p.setNombreproducto(rs.getString("nombreproducto").trim());
          p.setCodigodebarras(rs.getString("codigodebarras").trim());
          p.setReferencia(rs.getString("referencia").trim());
          p.setStock(rs.getDouble("stock"));
          p.setStockminimo(rs.getDouble("stockminimo"));
          p.setDescripcion(rs.getString("descripcion").trim());
          p.setTalla(rs.getString("talla").trim());
          p.setGenero(rs.getString("genero").trim());
          p.setEstado(rs.getString("estado").trim());
          Timestamp timestamp = rs.getTimestamp("fechaderegistro");
          if (timestamp != null) {
              p.setFechaderegistro(timestamp.toLocalDateTime());
          }
          p.setPreciounitario(rs.getDouble("precio_unitario"));
          p.setPreciomayorista(rs.getDouble("precio_mayorista"));
          p.setPreciodistribuidor(rs.getDouble("precio_distribuidor"));
          p.setImagen(rs.getBytes("imagen"));

          productos.add(p);

      }

      return  productos;

  }

    public int guardar(Producto pro) throws SQLException {
        // Validar si ya existe un producto con mismo nombre y talla (excepto si es el mismo id)
        if (existeProductoConNombreYTalla(pro.getNombreproducto(), pro.getTalla(), pro.getIdproducto(), pro.getGenero())) {
            org.controlsfx.control.Notifications.create()
                    .title("Aviso")
                    .text("No se pudo guardar el producto.\nYa existe un ítem con el mismo nombre, talla y género.\nSi deseas guardarlo, usa una talla diferente.")
                    .position(Pos.CENTER)
                    .showError();
            return -1;
        }


        String sql;
        boolean esNuevo = (pro.getIdproducto() == 0);

        if (esNuevo) {
            sql = "INSERT INTO producto (codigodebarras, referencia, nombreproducto, stock, stockminimo, descripcion, talla, genero, estado, fechaderegistro, precio_unitario, precio_mayorista, precio_distribuidor, imagen) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVO', ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE producto SET codigodebarras=?, referencia=?, nombreproducto=?, stock=?, stockminimo=?, descripcion=?, talla=?, genero=?, "
                    + "precio_unitario=?, precio_mayorista=?, precio_distribuidor=?, imagen=? WHERE idproducto=?";
        }

        PreparedStatement pst = conexionbd.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        if (esNuevo) {
            pst.setString(1, pro.getCodigodebarras());
            pst.setString(2, pro.getReferencia());
            pst.setString(3, pro.getNombreproducto());
            pst.setDouble(4, pro.getStock());
            pst.setDouble(5, pro.getStockminimo());
            pst.setString(6, pro.getDescripcion());
            pst.setString(7, pro.getTalla());
            pst.setString(8, pro.getGenero());
            pst.setDate(9, java.sql.Date.valueOf(pro.getFechaderegistro().toLocalDate())); // Asegúrate que sea LocalDate
            pst.setDouble(10, pro.getPreciounitario());
            pst.setDouble(11, pro.getPreciomayorista());
            pst.setDouble(12, pro.getPreciodistribuidor());
            pst.setBytes(13, pro.getImagen());
        } else {
            pst.setString(1, pro.getCodigodebarras());
            pst.setString(2, pro.getReferencia());
            pst.setString(3, pro.getNombreproducto());
            pst.setDouble(4, pro.getStock());
            pst.setDouble(5, pro.getStockminimo());
            pst.setString(6, pro.getDescripcion());
            pst.setString(7, pro.getTalla());
            pst.setString(8, pro.getGenero());
            pst.setDouble(9, pro.getPreciounitario());
            pst.setDouble(10, pro.getPreciomayorista());
            pst.setDouble(11, pro.getPreciodistribuidor());
            pst.setBytes(12, pro.getImagen());
            pst.setInt(13, pro.getIdproducto());
        }

        int resultado = pst.executeUpdate();

        if (esNuevo) {
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                resultado = rs.getInt(1);
            }
            rs.close();
        }

        pst.close();
        return resultado;
    }


    private boolean existeProductoConNombreYTalla(String nombre, String talla, int idProducto, String genero) throws SQLException {
        String sql = "SELECT COUNT(*) FROM producto WHERE nombreproducto = ? AND talla = ? AND genero = ? AND idproducto != ?";

        PreparedStatement pst = conexionbd.getConexion().prepareStatement(sql);
        pst.setString(1, nombre);
        pst.setString(2, talla);
        pst.setString(3, genero);
        pst.setInt(4, idProducto); // Para que al editar no se compare consigo mismo


        ResultSet rs = pst.executeQuery();
        rs.next();
        int count = rs.getInt(1);

        rs.close();
        pst.close();

        return count > 0;
    }

    public Producto getById(int idproducto) throws SQLException {
        Producto p = null;
        ResultSet rs = this.conexionbd.CONSULTAR("SELECT * FROM producto WHERE idproducto="+idproducto);
        if(rs.next()){
            p = new Producto();
            p.setIdproducto(idproducto);
            p.setCodigodebarras(rs.getString("codigodebarras").trim());
            p.setReferencia(rs.getString("referencia").trim());
            p.setNombreproducto(rs.getString("nombreproducto").trim());
            p.setStock(rs.getDouble("stock"));
            p.setStockminimo(rs.getDouble("stockminimo"));
            p.setDescripcion(rs.getString("descripcion").trim());
            p.setImagen(rs.getBytes("imagen"));
            p.setEstado(rs.getString("estado").trim());
            p.setTalla(rs.getString("talla").trim());
            p.setGenero(rs.getString("genero").trim());
            p.setPreciounitario(rs.getDouble("precio_unitario"));
            p.setPreciomayorista(rs.getDouble("precio_mayorista"));
            p.setPreciodistribuidor(rs.getDouble("precio_distribuidor"));

        }
        return p;
    }

  //  public boolean delete(int idproducto) throws SQLException{
   //     String sql = "DELETE FROM producto WHERE idproducto="+idproducto;
    //    return conexionbd.GUARDAR(sql);
   // }

    public boolean delete(int idproducto) throws SQLException{
        String sql = "UPDATE  producto SET estado='INACTIVO' WHERE idproducto="+idproducto;
        return conexionbd.GUARDAR(sql);
    }


    public boolean activar(int idproducto) throws SQLException{
        String sql = "UPDATE  producto SET estado='ACTIVO' WHERE idproducto="+idproducto;
        return conexionbd.GUARDAR(sql);
    }

    public List<Producto> getAll() throws SQLException{
        List<Producto> lista = new ArrayList<>();
        Producto p = null;
        ResultSet rs = this.conexionbd.CONSULTAR("SELECT idproducto, codigodebarras, referencia, nombreproducto, stock, stockminimo, descripcion, talla, genero, estado, fechaderegistro, precio_unitario, precio_mayorista, precio_distribuidor\n" +
                "	FROM public.producto;");
        while(rs.next()){
            p = new Producto();
            p.setIdproducto(rs.getInt("idproducto"));
            p.setCodigodebarras(rs.getString("codigodebarras").trim());
            p.setReferencia(rs.getString("referencia").trim());
            p.setNombreproducto(rs.getString("nombreproducto").trim());
            p.setStock(rs.getDouble("stock"));
            p.setStockminimo(rs.getDouble("stockminimo"));
            p.setDescripcion(rs.getString("descripcion").trim());
            p.setEstado(rs.getString("estado").trim());
            p.setTalla(rs.getString("talla").trim());
            p.setGenero(rs.getString("genero").trim());
            p.setPreciounitario(rs.getDouble("precio_unitario"));
            p.setPreciomayorista(rs.getDouble("precio_mayorista"));
            p.setPreciodistribuidor(rs.getDouble("precio_distribuidor"));

            lista.add(p);
        }
        return lista;

    }
}
