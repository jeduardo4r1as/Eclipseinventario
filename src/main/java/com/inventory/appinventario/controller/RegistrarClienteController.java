package com.inventory.appinventario.controller;

import animatefx.animation.Tada;
import com.inventory.appinventario.dao.ClienteDAO;
import com.inventory.appinventario.model.Cliente;
import com.inventory.appinventario.model.Usuario;
import com.inventory.appinventario.util.ConexionBD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistrarClienteController implements Initializable {


    @FXML
    private Button btnGuardar;

    @FXML
    private TextField cjApellido;

    @FXML
    private TextField cjDireccion;

    @FXML
    private TextField cjDocumento;

    @FXML
    private TextField cjNombre;

    @FXML
    private TextField cjTelefono;

    @FXML
    private TextField cjCorreo;

    @FXML
    private HBox header;

    @FXML
    private AnchorPane root;

    @FXML
    private Text txtCliente;

    private ConexionBD conexionBD = new ConexionBD();


    private Cliente cliente;

    private ClienteDAO clienteDAO;

    private boolean STATUS = false;

    public boolean isSTATUS() {
        return STATUS;
    }

    public void setSTATUS(boolean STATUS) {
        this.STATUS = STATUS;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    @FXML
    void guardar(ActionEvent event) {
        if (cjNombre.getText().isEmpty()) {
            new Tada(cjNombre).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese el nombre del cliente").position(Pos.CENTER).showWarning();
            return;
        }
        if (cjDocumento.getText().isEmpty()) {
            new Tada(cjDocumento).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese el numero de documento de identificacion").position(Pos.CENTER).showWarning();
            return;
        }

        if (cjApellido.getText().isEmpty()) {
            new Tada(cjApellido).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese los apellidos del cliente").position(Pos.CENTER).showWarning();
            return;
        }
        if (cjDireccion.getText().isEmpty()) {
            new Tada(cjDireccion).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese la direccion del cliente").position(Pos.CENTER).showWarning();
            return;
        }
        if (cjTelefono.getText().isEmpty()) {
            new Tada(cjTelefono).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese el numero de documento de identificacion").position(Pos.CENTER).showWarning();
            return;
        }
        if (cjCorreo.getText().isEmpty()) {
            new Tada(cjCorreo).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese el correo del cliente").position(Pos.CENTER).showWarning();
            return;
        }

        if (!cjCorreo.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            new Tada(cjCorreo).play();
            org.controlsfx.control.Notifications.create()
                    .title("Correo inválido")
                    .text("Ingrese un correo electrónico válido")
                    .position(Pos.CENTER)
                    .showError();
            return;
        }

        if(getCliente()==null){
            cliente = new Cliente();
        }

        cliente.setNombrecliente(cjNombre.getText());
        cliente.setApellidocliente(cjApellido.getText());
        cliente.setNumerodocumento(cjDocumento.getText());
        cliente.setDireccioncliente(cjDireccion.getText());
        cliente.setTelefonocliente(cjTelefono.getText());
        cliente.setCorreo(cjCorreo.getText());

        this.conexionBD.conectar();
        clienteDAO = new ClienteDAO(conexionBD);

        try {

            int guardar = clienteDAO.guardar(cliente);

            if (guardar>0) {
                setSTATUS(true);
                com.inventory.appinventario.util.Metodos.closeEffect(root);
            }

        } catch (SQLException ex) {
            Logger.getLogger(RegistrarClienteController.class.getName()).log(Level.SEVERE, null, ex);
            org.controlsfx.control.Notifications.create().title("Aviso").text("NO SE PUDO GUARDAR EL CLIENTE\n" + ex.getMessage()).position(Pos.CENTER).showError();
        } finally {
            this.conexionBD.CERRAR();
        }
    }


    @FXML
    void salir(ActionEvent event) {

    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;

        cjNombre.setText(cliente.getNombrecliente().trim());
        cjApellido.setText(cliente.getApellidocliente().trim());
        cjDocumento.setText(cliente.getNumerodocumento().trim());
       cjDireccion.setText(cliente.getDireccioncliente());
        cjTelefono.setText(cliente.getTelefonocliente());
        cjCorreo.setText(cliente.getCorreo());

    }



    public Cliente getCliente() {
        return cliente;
    }


}
