package principalCliente;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 * @author Angel
 */
public class Carrito extends javax.swing.JFrame {

    private String bd = "jdbc:hsqldb:hsql://localhost";
    private String usuario = "SA";
    private String contraseña = "";
    private ArrayList<Producto> list;
    private double total = 0;
    int serial;
    private DefaultTableModel modelo = new DefaultTableModel() {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    /**
     * Creates new form Carrito
     *
     * @param list ArrayList con los datos necesarios para gestionar el carrito.
     */
    public Carrito(ArrayList<Producto> list) {
        this.list = list;
        crearTablas();
        initComponents();
        labelTotal.setText("Total: " + total + "€");
    }

    /**
     * Metodo usado para establecer una conexion con la base de datos.
     * @return La conexion.
     */
    public Connection conexBase() {
        Connection conexion = null;
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            conexion = DriverManager.getConnection(bd, usuario, contraseña);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TiendaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(TiendaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conexion;
    }

    /**
     * Estructura la tabla con la lista de productos.
     */
    public void crearTablas() {
        modelo.addColumn("Nombre");
        modelo.addColumn("Categoria");
        modelo.addColumn("Precio");
        modelo.addColumn("Cantidad");
        for (Producto producto : this.list) {
            Object[] fila = {producto.getNombre(), producto.getCategoria(), producto.getPrecio(), producto.getCantidad()};
            modelo.addRow(fila);
            total += (producto.getPrecio() * producto.getCantidad());
        }

    }

    /**
     * Getter del numero de productos en el ArrayList.
     * @return Numero de productos.
     */
    public int getNumProductos() {
        return list.size();
    }

    /**
     * Getter del Array list.
     * @return El ArrayList
     */
    public ArrayList<Producto> getList() {
        return list;
    }

    /**
     * Elimina una fila en base a su indice.
     * @param fila El indice de la fila
     */
    public void eliminarFila(int fila) {
        if (fila != -1) {
            DefaultTableModel modelo = (DefaultTableModel) tableCar.getModel();
            modelo.removeRow(fila);
        }
    }

    /**
     * Genera un numero aleatoria para usarlo como serial de la compra.
     * @return El serial
     */
    public int generarSerial() {
        Random random = new Random();
        return random.nextInt(999999) + 1;
    }

    /**
     * Comprueba que el seria generado no exista ya en la base de datos.
     * @param serial el serial a comprobar
     * @return True si el serial existe y False si no existe
     */
    public boolean existeSerial(int serial) {
        try {
            Statement sentencia = conexBase().createStatement();
            String sql = "SELECT COUNT(*) FROM products_sells WHERE serial = " + serial;
            ResultSet resul = sentencia.executeQuery(sql);
            if (resul.next()) {
                int count = resul.getInt(1);
                if (count > 0) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Carrito.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Muestra una ventana de confirmacion y en caso de aceptar finaliza la compra, realiza los cambios correspondientes en la base de datos y genera la factura.
     */
    public void ventanaConfirmacion() {
        int opcion = JOptionPane.showConfirmDialog(this, "¿Deseas confirmar el pago?", "Confirmación de Pago", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                Statement sentencia = conexBase().createStatement();
                guardarVenta(sentencia);
                for (Producto producto : this.list) {
                    System.out.println(producto.getId() + " - " + producto.getCantidad());
                    String sql = "UPDATE product_stock SET stock = stock - " + producto.getCantidad() + " WHERE product_id =" + producto.getId();
                    sentencia.executeUpdate(sql);
                }
                list.clear();
                this.dispose();
                informeFactura();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    /**
     * Guarda la informacion de la venta en la base de datos.
     * @param sentencia La sentencia
     */
    public void guardarVenta(Statement sentencia) {
        try {
            java.util.Date javaDate = new java.util.Date();
            java.sql.Date mySQLDate = new java.sql.Date(javaDate.getTime());
            do {
                serial = generarSerial();
            } while (existeSerial(serial));
            for (Producto producto : this.list) {
                String sql = "INSERT INTO products_sells (product_id, serial, amount, total, date) "
                        + "VALUES (" + producto.getId() + ", " + String.format("%06d", serial) + ", " + producto.getCantidad() + ", "
                        + "(SELECT price * " + producto.getCantidad() + " "
                        + "FROM products WHERE product_id = " + producto.getId() + "), '" + mySQLDate + "')";
                sentencia.executeUpdate(sql);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera un informe de Jaspersoft con los datos de la compra que funciona como factura.
     */
    public void informeFactura() {
        JasperReport jr = null;
        try {
            InputStream ruta = null;
            ruta = getClass().getResourceAsStream("factura/Factura.jrxml");
            String ruta_imagen = "principalCliente/factura/Javastock.png";
            Map<String, Object> miMapa = new HashMap<>();
            miMapa.put("serial", "" + serial + "");
            miMapa.put("imagen", ruta_imagen);
            jr = JasperCompileManager.compileReport(ruta);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jr, miMapa, conexBase());
            JasperViewer visor = new JasperViewer(jasperPrint, false);
            visor.setVisible(true);
        } catch (JRException ex) {
            Logger.getLogger(Carrito.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableCar = new javax.swing.JTable(modelo);
        buttonPay = new javax.swing.JButton();
        buttonBack = new javax.swing.JButton();
        labelTotal = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setTitle("Javastock(Carrito)");

        tableCar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableCarMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableCar);

        buttonPay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/dollar.png"))); // NOI18N
        buttonPay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPayActionPerformed(evt);
            }
        });

        buttonBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/back.png"))); // NOI18N
        buttonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBackActionPerformed(evt);
            }
        });

        labelTotal.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        labelTotal.setForeground(new java.awt.Color(255, 0, 0));
        labelTotal.setText("Total:");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel1.setText("*Doble click en un producto para eliminarlo.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonBack, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelTotal)
                            .addComponent(buttonPay, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelTotal)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(buttonBack)
                    .addComponent(buttonPay))
                .addGap(18, 18, 18))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Oculta la ventana actual para volver a la principal.
     * @param evt El evento
     */
    private void buttonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBackActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_buttonBackActionPerformed

    /**
     * Muestra la ventana de confirmacion para concretar la venta.
     * @param evt El evento
     */
    private void buttonPayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPayActionPerformed
        if (!list.isEmpty()) {
            ventanaConfirmacion();
        } else {
            JOptionPane.showMessageDialog(this, "No hay productos en el carrito.", "¡Atencion!", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_buttonPayActionPerformed

    /**
     * Elimina un producto del carrito al realizarse doble click en el.
     * @param evt El evento
     */
    private void tableCarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableCarMouseClicked
        System.out.println(tableCar.getSelectedRow());
        System.out.println(list.get(tableCar.getSelectedRow()));
        if (evt.getClickCount() == 2 && tableCar.getSelectedRow() != -1 && tableCar.getValueAt(tableCar.getSelectedRow(), tableCar.getSelectedColumn()) != null) {
            int filaSeleccionada = tableCar.getSelectedRow();
            list.remove(filaSeleccionada);
            eliminarFila(filaSeleccionada);
            total = 0;
            for (Producto producto : this.list) {
                total += (producto.getPrecio() * producto.getCantidad());
            }
            labelTotal.setText("Total: " + total + "€");
            System.out.println(list.toString());
            //falta que el total refleje los cambios al eliminar un producto
        }
    }//GEN-LAST:event_tableCarMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonBack;
    private javax.swing.JButton buttonPay;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelTotal;
    private javax.swing.JTable tableCar;
    // End of variables declaration//GEN-END:variables
}
