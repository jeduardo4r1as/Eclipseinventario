package com.inventory.appinventario.dao;

import com.inventory.appinventario.model.Producto;
import com.inventory.appinventario.util.ConexionBD;

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
      String sql = "";
      if (pro.getIdproducto() == 0) {
          sql = "INSERT INTO producto ( ";
          sql += " codigodebarras, referencia, nombreproducto, stock, stockminimo, descripcion, talla, genero, estado, fechaderegistro, precio_unitario, precio_mayorista, precio_distribuidor, imagen ";
          sql += ") VALUES (";
          sql += " '"+pro.getCodigodebarras()+"' , '"+pro.getReferencia()+"' , '"+pro.getNombreproducto()+"' , '"+pro.getStock()+"' , ";
          sql += " '"+pro.getStockminimo()+"' , '"+pro.getDescripcion()+"' , '"+pro.getTalla()+"' , '"+pro.getGenero()+"' , 'ACTIVO' , '"+pro.getFechaderegistro()+"',"+pro.getPreciounitario()+" , "+pro.getPreciomayorista()+","+pro.getPreciodistribuidor();
          sql += ", ? ";
          sql += ")";
      } else {
          sql = "UPDATE producto SET \n"
                  + "	codigodebarras='"+pro.getCodigodebarras()+"', referencia='"+pro.getReferencia()+"', nombreproducto='"+pro.getNombreproducto()+"', \n"
                  + " stock=" + pro.getStock() + ", "+"stockminimo="+pro.getStockminimo()+", descripcion='"+pro.getDescripcion()+"', talla='"+pro.getTalla()+"', genero='"+pro.getGenero()+"', precio_unitario="+pro.getPreciounitario()+", \n"
                  + "precio_mayorista="+pro.getPreciomayorista()+", precio_distribuidor="+pro.getPreciodistribuidor()+" \n"
                  +" , imagen=? WHERE idproducto="+pro.getIdproducto()+";";
      }

      PreparedStatement pst = conexionbd.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

      pst.setBytes(1, pro.getImagen());

      int insert = pst.executeUpdate();
      if (pro.getIdproducto() == 0) {
          ResultSet rs = pst.getGeneratedKeys();
          rs.next();
          insert = rs.getInt(1);
          rs.close();
      }
      pst.close();
      return insert;

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
