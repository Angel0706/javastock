package principalCliente;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @author Angel
 */
public class TiendaPrincipal extends javax.swing.JFrame {

    private String bd = "jdbc:hsqldb:hsql://localhost";
    private String usuario = "SA";
    private String contraseña = "";
    private Object[][] myTable;
    private Carrito car;
    private ArrayList<Producto> list = new ArrayList<>();
    private int numPro = 0;

    /**
     * Creates new form TiendaPrincipal
     */
    public TiendaPrincipal() {
        initComponents();
        cargarDatosTabla();
        cargarComboBox();
        //TableHeaderListener();
        OcultarColumna(tablePro, "ID");
    }

    /**
     * Carga los datos de la base de datos en una columna del jTable en base al indice del mismo.
     * @param sentenciaSQL La sentencia de MySQL que se usara para rellenar los datos de la columna(SELECT).
     * @param column Indice de la columna para cargar los datos.
     */
    public void cargarSentencia(String sentenciaSQL, int column) {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            Connection conexion = DriverManager.getConnection(bd, usuario, contraseña);
            Statement sentencia = conexion.createStatement();
            String sql = sentenciaSQL;
            ResultSet resul = sentencia.executeQuery(sql);
            int contador = 0;
            while (resul.next()) {
                String value = (resul.getString(1)).substring(0, 1).toUpperCase() + (resul.getString(1)).substring(1);
                if (value.contains(".")) {
                    try {
                        double parseDouble = Double.parseDouble(value);
                        value = String.valueOf(parseDouble);
                    } catch (NumberFormatException err) {
                    }
                }
                tablePro.setValueAt(value, contador, column);
                contador++;
            }
            resul.close();
            sentencia.close();
            conexion.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.getMessage();
        }
    }

    /**
     * Metodo usado para estructurar el jTable con los datos necesarios de la base de datos.
     */
    public void cargarDatosTabla() {
        DefaultTableModel dtm = (DefaultTableModel) tablePro.getModel();
        dtm.setRowCount(0);
        tablePro.setModel(new javax.swing.table.DefaultTableModel(myTable,
                new String[]{
                    "Nombre", "Categoria", "Precio", "Cantidad disponible", "ID"
                }
        ));
        cargarSentencia("SELECT name FROM products ORDER BY name", 0);
        cargarSentencia("SELECT cat.name FROM products pro, product_category cat "
                + "WHERE pro.category_id = cat.category_id ORDER BY pro.name", 1);
        cargarSentencia("SELECT price FROM products ORDER BY name", 2);
        cargarSentencia("SELECT stk.stock FROM products pro, product_stock stk "
                + "WHERE stk.product_id = pro.product_id ORDER BY pro.name", 3);
        cargarSentencia("SELECT product_id FROM products ORDER BY name", 4);
        OcultarColumna(tablePro, "ID");
    }

    /**
     * Metodo usado para estructurar el jComboBox con los datos necesarios de la base de datos.
     */
    public void cargarComboBox() {
        try {
            DefaultComboBoxModel modelo = (DefaultComboBoxModel) comboBoxFiltro.getModel();
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            Connection conexion = DriverManager.getConnection(bd, usuario, contraseña);
            Statement sentencia = conexion.createStatement();
            ResultSet resul = sentencia.executeQuery("SELECT name FROM product_category ORDER BY name");
            int contador = 0;
            while (resul.next()) {
                modelo.addElement(resul.getString(1));
            }
            comboBoxFiltro.setModel(modelo);
            resul.close();
            sentencia.close();
            conexion.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TiendaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(TiendaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Actualiza la informacion mostrada en las tablas.
     */
    public void actualizarTablas() {
        cargarDatosTabla();
        DefaultTableModel dtm = (DefaultTableModel) tablePro.getModel();
        dtm.fireTableStructureChanged();
        OcultarColumna(tablePro, "ID");
    }

    /*public void TableHeaderListener() {
        tablePro.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tablePro.columnAtPoint(e.getPoint());
                String columnName = tablePro.getColumnName(col);
            }
        });
    }*/

    /**
     * Oculta una columna del jTable.
     * @param tab El jTable a modificar.
     * @param col La columna a ocultar.
     */
    public void OcultarColumna(JTable tab, String col) {
        int columnIndex = tab.getColumnModel().getColumnIndex(col);
        if (columnIndex != -1) {
            tab.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
            tab.getColumnModel().getColumn(columnIndex).setMinWidth(0);
            tab.getColumnModel().getColumn(columnIndex).setWidth(0);
            tab.getColumnModel().getColumn(columnIndex).setPreferredWidth(0);
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
        tablePro = new javax.swing.JTable(){
            public boolean isCellEditable(int row, int column){
                return false;
            };
        };
        labelBuscador = new javax.swing.JLabel();
        textFieldBuscador = new javax.swing.JTextField();
        buttonCar = new javax.swing.JButton();
        comboBoxFiltro = new javax.swing.JComboBox<>();
        labelFiltro = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Javastock");
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });

        myTable = new Object[][] {
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null, null, null, null},
            {null},
        };
        tablePro.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tablePro.setModel(new javax.swing.table.DefaultTableModel(
            myTable,
            new String[] {
                "Nombre", "Categoria", "Precio", "Cantidad disponible"
            }
        ));
        tablePro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableProMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablePro);

        labelBuscador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search.png"))); // NOI18N

        textFieldBuscador.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        textFieldBuscador.setText("¿Que estas buscando?");
        textFieldBuscador.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldBuscadorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textFieldBuscadorFocusLost(evt);
            }
        });
        textFieldBuscador.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                textFieldBuscadorKeyTyped(evt);
            }
        });

        buttonCar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        buttonCar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/shop.png"))); // NOI18N
        buttonCar.setText("("+numPro+")");
        buttonCar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCarActionPerformed(evt);
            }
        });

        comboBoxFiltro.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        comboBoxFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selecciona una categoria" }));
        comboBoxFiltro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBoxFiltroItemStateChanged(evt);
            }
        });

        labelFiltro.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelFiltro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/filter.png"))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel1.setText("*Doble click en un producto para añadirlo al carrito.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 712, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelBuscador)
                            .addComponent(labelFiltro))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(comboBoxFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(textFieldBuscador))
                        .addGap(18, 18, 18)
                        .addComponent(buttonCar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labelBuscador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(textFieldBuscador))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboBoxFiltro)
                            .addComponent(labelFiltro)))
                    .addComponent(buttonCar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * Abre la ventana del carro de compras con los productos seleccionados por el usuario y actualiza la tabla de productos al cerrarse dicha ventana.
     * @param evt El evento.
     */
    private void buttonCarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCarActionPerformed
        car = new Carrito(list);
        car.setVisible(true);
        car.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                numPro = 0;
                buttonCar.setText("(" + numPro + ")");
                list.clear();
                actualizarTablas();
            }
        });
    }//GEN-LAST:event_buttonCarActionPerformed
    
    /**
     * Detecta cuando el usuario hace doble click en un producto y lo añade al carrito en caso de tener existencias del mismo.
     * @param evt El evento.
     */
    private void tableProMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableProMouseClicked
        if (evt.getClickCount() == 2 && tablePro.getSelectedRow() != -1 && tablePro.getValueAt(tablePro.getSelectedRow(), tablePro.getSelectedColumn()) != null) {
            if (Integer.parseInt((String) tablePro.getValueAt(tablePro.getSelectedRow(), 3)) > 0) {
                String nombre = (String) tablePro.getValueAt(tablePro.getSelectedRow(), 0);
                String categoria = (String) tablePro.getValueAt(tablePro.getSelectedRow(), 1);
                double precio = Double.parseDouble((String) tablePro.getValueAt(tablePro.getSelectedRow(), 2));
                int id = Integer.parseInt((String) tablePro.getValueAt(tablePro.getSelectedRow(), 4));
                boolean productoExistente = false;
                for (Producto producto : list) {
                    if (producto.getId() == id) {
                        producto.addCantidad();
                        numPro++;
                        productoExistente = true;
                        break;
                    }
                }
                if (!productoExistente) {
                    list.add(new Producto(nombre, categoria, precio, id));
                    numPro++;
                }
                buttonCar.setText("(" + numPro + ")");
            } else {
                JOptionPane.showMessageDialog(this, "Producto agotado.", "¡Atencion!", JOptionPane.WARNING_MESSAGE);
            }

        }
    }//GEN-LAST:event_tableProMouseClicked

    /**
     * Actualiza la informacion del jTable en base a lo lo buscado por el usuarion cuando se pulsa la tecla "enter".
     * @param evt El evento.
     */
    private void textFieldBuscadorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldBuscadorKeyTyped
        if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
            String nombre = textFieldBuscador.getText().toLowerCase();
            DefaultTableModel dtm = (DefaultTableModel) tablePro.getModel();
            dtm.setRowCount(0);
            tablePro.setModel(new javax.swing.table.DefaultTableModel(myTable,
                    new String[]{
                        "Nombre", "Categoria", "Precio", "Cantidad disponible", "ID"
                    }
            ));
            cargarSentencia("SELECT name FROM products WHERE name LIKE '" + nombre + "%' ORDER BY name", 0);
            cargarSentencia("SELECT cat.name FROM products pro, product_category cat "
                    + "WHERE pro.category_id = cat.category_id AND pro.name LIKE '" + nombre + "%' ORDER BY pro.name", 1);
            cargarSentencia("SELECT price FROM products WHERE name LIKE '" + nombre + "%' ORDER BY name", 2);
            cargarSentencia("SELECT stk.stock FROM products pro, product_stock stk "
                    + "WHERE stk.product_id = pro.product_id AND pro.name LIKE '" + nombre + "%' ORDER BY pro.name", 3);
            cargarSentencia("SELECT product_id FROM products WHERE name LIKE '" + nombre + "%' ORDER BY name", 4);
            OcultarColumna(tablePro, "ID");
        }
    }//GEN-LAST:event_textFieldBuscadorKeyTyped

    /**
     * Detecta cuando el buscador obtiene el foco y elimina el texto por defecto.
     * @param evt El evento.
     */
    private void textFieldBuscadorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldBuscadorFocusGained
        if (textFieldBuscador.getText().equals("¿Que estas buscando?")) {
            textFieldBuscador.setText("");
        }
    }//GEN-LAST:event_textFieldBuscadorFocusGained

    /**
     * Detecta cuando el buscador pierde el foco y le devuelve el texto por defecto en caso de que el usuario no escribiera nada.
     * @param evt El evento.
     */
    private void textFieldBuscadorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldBuscadorFocusLost
        if (textFieldBuscador.getText().equals("")) {
            textFieldBuscador.setText("¿Que estas buscando?");
        }
    }//GEN-LAST:event_textFieldBuscadorFocusLost

    /**
     * Actualiza la informacion de las tablas en base a la categoria seleccionada por el usuario en el filtro.
     * @param evt El evento.
     */
    private void comboBoxFiltroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboBoxFiltroItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            String categoria = (comboBoxFiltro.getSelectedIndex() == 0) ? "%" : (String) comboBoxFiltro.getSelectedItem();
            DefaultTableModel dtm = (DefaultTableModel) tablePro.getModel();
            dtm.setRowCount(0);
            tablePro.setModel(new javax.swing.table.DefaultTableModel(myTable,
                    new String[]{
                        "Nombre", "Categoria", "Precio", "Cantidad disponible", "ID"
                    }
            ));
            cargarSentencia("SELECT pro.name FROM products pro, product_category cat WHERE pro.category_id = cat.category_id AND cat.name LIKE '" + categoria + "' ORDER BY pro.product_id", 0);
            cargarSentencia("SELECT cat.name FROM products pro, product_category cat "
                    + "WHERE pro.category_id = cat.category_id AND cat.name LIKE '" + categoria + "' ORDER BY pro.product_id", 1);
            cargarSentencia("SELECT pro.price FROM products pro, product_category cat WHERE pro.category_id = cat.category_id AND cat.name LIKE '" + categoria + "' ORDER BY pro.product_id", 2);
            cargarSentencia("SELECT stk.stock FROM products pro, product_stock stk, product_category cat "
                    + "WHERE stk.product_id = pro.product_id AND cat.category_id = pro.category_id AND cat.name LIKE '" + categoria + "' ORDER BY pro.product_id", 3);
            cargarSentencia("SELECT pro.product_id FROM products pro, product_category cat WHERE pro.category_id = cat.category_id AND cat.name LIKE '" + categoria + "' ORDER BY product_id", 4);
            OcultarColumna(tablePro, "ID");
            System.out.println(comboBoxFiltro.getSelectedIndex());
        }
    }//GEN-LAST:event_comboBoxFiltroItemStateChanged

    /**
     * Actualiza la infromacion de la aplicacion cuando esta obtiene el foco.
     * @param evt El evento.
     */
    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        if (!(car == null)) {
            list = car.getList();
            numPro = list.size();
            if (!list.isEmpty()) {
                buttonCar.setText("(" + car.getNumProductos() + ")");
                System.out.println(car.getNumProductos());
            } else {
                buttonCar.setText("(0)");
            }
        }
    }//GEN-LAST:event_formWindowGainedFocus

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TiendaPrincipal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TiendaPrincipal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TiendaPrincipal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TiendaPrincipal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TiendaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCar;
    private javax.swing.JComboBox<String> comboBoxFiltro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelBuscador;
    private javax.swing.JLabel labelFiltro;
    private javax.swing.JTable tablePro;
    private javax.swing.JTextField textFieldBuscador;
    // End of variables declaration//GEN-END:variables
}
