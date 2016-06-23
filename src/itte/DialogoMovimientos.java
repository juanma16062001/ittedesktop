/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itte;

import itte.clases.modelos.Conceptos;
import itte.clases.Conexion;
import itte.clases.modelos.Cursos;
import itte.clases.Funciones;
import itte.clases.modelos.Horarios;
import itte.clases.modelos.Matriculas;
import itte.clases.MesesString;
import itte.clases.modelos.Movimientos;
import itte.otros.CheckRenderer;
import itte.otros.TableButton;
import itte.otros.TableButtonListener;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Juan M S
 */
public class DialogoMovimientos extends javax.swing.JDialog {

    private Matriculas m;
    private DefaultTableModel modeloTablaMovimientos;
    /**
     * Creates new form DialogoMovimientos
     */
    public DialogoMovimientos() {
        super();
        initComponents();
        Conexion conexion = new Conexion("academia");
        conexion.comando("CREATE TABLE if not exists `detalles_notas_debitocredito` (`id` INT NOT NULL AUTO_INCREMENT,`concepto` VARCHAR(10) NULL,`detalle` VARCHAR(60) NULL,PRIMARY KEY (`id`))");
    }
    
    public DialogoMovimientos(int matricula) {
        super();
        m = new Matriculas(matricula);
        initComponents();
        setearTablaMovimientos();
    }
    
    private void setearDatosCursos(){
        try {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
            ResultSet res =  m.obtenerCursosInscriptos();
            while(res.next()){
                model.addElement(String.format("%d-%s", res.getInt("id_curso"),res.getString("nombreCurso")));
            }
            cbCursos.setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(DialogoMovimientos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setearDatosConceptos(){
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
        Conceptos conceptosVisibles = new Conceptos();
        conceptosVisibles.obtenerVisibles();
        while(conceptosVisibles.sig()){
            model.addElement(String.format("%d-%s", conceptosVisibles.getId_concepto(),conceptosVisibles.getConcepto()));
        }
        cbConcepto.setModel(model);
    }
    
    private void setearDatosDetalle() {
        try {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
            model.addElement("");
            
            String concepto = cbConcepto.getSelectedItem().toString();
            concepto = concepto.contains("Débito")?"debito":"credito";
            Conexion conexion = new Conexion("academia");
            ResultSet rs = conexion.consulta("select * from detalles_notas_debitocredito where concepto = '"+concepto+"'");
            while(rs.next()){
                model.addElement(rs.getString("detalle"));
            }
            cbDetalle.setModel(model);
        } catch (SQLException ex) {
            Logger.getLogger(DialogoMovimientos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void habilitarBtnGuardar(){
        if(!txtMonto.getText().isEmpty() && !cbConcepto.getSelectedItem().toString().isEmpty() && !cbDetalle.getSelectedItem().toString().isEmpty())
            btnGuardarPago.setEnabled(true);
        else
            btnGuardarPago.setEnabled(false);
    }
    
    private void setearTablaMovimientos(){
        if(modeloTablaMovimientos.getRowCount()>0){
            for(int i = modeloTablaMovimientos.getRowCount() - 1; i>=0; i--){
                modeloTablaMovimientos.removeRow(i);
            }
        }
        
        ListSelectionModel model = tablaMovimientos.getSelectionModel();
        model.clearSelection();
        
        double saldoTotal=0;
        
        Movimientos movimientos = new Movimientos(m.getMatricula());
        while(movimientos.sig()){
            Object [] args = new Object[6];
            args[0] = String.format("%d",movimientos.getId_movimiento());
            args[1] = String.format("%s", movimientos.getFecha().toString());
            args[2] = String.format("%s", movimientos.getConcepto());
            args[3] = String.format("%.2f", movimientos.getDebe());
            args[4] = String.format("%.2f", movimientos.getHaber());
            args[5] = String.format("%.2f", movimientos.getSaldo());
            modeloTablaMovimientos.addRow(args);
            if(movimientos.getSaldado()==0){
                model.addSelectionInterval(modeloTablaMovimientos.getRowCount()-1, modeloTablaMovimientos.getRowCount()-1);
            }
            saldoTotal += movimientos.getHaber() - movimientos.getDebe();
        }
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaMovimientos.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tablaMovimientos.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        tablaMovimientos.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        
        lblSaldoTotal.setText(String.format("%.2f",saldoTotal));
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cbMeses = new javax.swing.JComboBox<>();
        cbAnio = new javax.swing.JComboBox<>();
        cbCursos = new javax.swing.JComboBox<>();
        rbTodosAlumnos = new javax.swing.JCheckBox();
        btnGenerarCuotas = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtMonto = new javax.swing.JTextField();
        cbConcepto = new javax.swing.JComboBox<>();
        btnGuardarPago = new javax.swing.JButton();
        btnAbmDetalles = new javax.swing.JButton();
        cbDetalle = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        lblDatosMatricula = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaMovimientos = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        lblSaldoTotal = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(0,0,((Dimension)(Toolkit.getDefaultToolkit().getScreenSize())).width,((Dimension)(Toolkit.getDefaultToolkit().getScreenSize())).height);
        setSize((Dimension)(Toolkit.getDefaultToolkit().getScreenSize()));

        jPanel2.setBackground(new java.awt.Color(180, 193, 224));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Generar Cuota");

        cbMeses.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
        for(int i = 0; i < 12; i++){
            model.addElement(MesesString.obtenerMes(i));
        }
        cbMeses.setModel(model);

        cbAnio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { java.time.Year.now().toString(),java.time.Year.now().plusYears(1).toString() }));

        cbCursos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        setearDatosCursos();

        rbTodosAlumnos.setBackground(new java.awt.Color(180, 193, 224));
        rbTodosAlumnos.setText("Todos los Alumnos!!");

        btnGenerarCuotas.setText("Generar Cuotas...");
        btnGenerarCuotas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarCuotasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGenerarCuotas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cbMeses, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbAnio, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(rbTodosAlumnos)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cbCursos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbMeses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(cbCursos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(rbTodosAlumnos)
                .addGap(18, 18, 18)
                .addComponent(btnGenerarCuotas)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 37, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );

        jPanel4.setBackground(new java.awt.Color(180, 193, 224));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Generar otros pagos");

        jLabel4.setText("Monto");

        jLabel5.setText("Detalle");

        jLabel6.setText("Concepto");

        jLabel7.setText("$");

        txtMonto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMontoFocusLost(evt);
            }
        });

        cbConcepto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbConcepto.setEnabled(false);
        setearDatosConceptos();
        cbConcepto.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbConceptoItemStateChanged(evt);
            }
        });

        btnGuardarPago.setText("Guardar");
        btnGuardarPago.setEnabled(false);
        btnGuardarPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarPagoActionPerformed(evt);
            }
        });

        btnAbmDetalles.setText("Detalles");
        btnAbmDetalles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbmDetallesActionPerformed(evt);
            }
        });

        cbDetalle.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbDetalle.setEnabled(false);
        cbDetalle.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbDetalleItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAbmDetalles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuardarPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(47, 47, 47)
                                .addComponent(jLabel7))
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbConcepto, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(cbDetalle, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7)
                    .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cbConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cbDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnAbmDetalles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardarPago)
                .addContainerGap())
        );

        setearDatosDetalle();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(267, 267, 267)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setBackground(new java.awt.Color(145, 153, 170));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(java.awt.Color.white);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("M O V I M I E N T O S");
        jLabel1.setOpaque(true);

        jPanel7.setBackground(new java.awt.Color(0, 51, 204));

        lblDatosMatricula.setBackground(java.awt.Color.white);
        lblDatosMatricula.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblDatosMatricula.setForeground(new java.awt.Color(255, 255, 255));
        lblDatosMatricula.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDatosMatricula.setText("Apellido, Nombre (DNI: 11222333)");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblDatosMatricula, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblDatosMatricula)
        );

        tablaMovimientos.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        tablaMovimientos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Fecha Mov", "Detalle", "Debe", "Haber", "Saldo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tablaMovimientos);
        if (tablaMovimientos.getColumnModel().getColumnCount() > 0) {
            tablaMovimientos.getColumnModel().getColumn(0).setMinWidth(40);
            tablaMovimientos.getColumnModel().getColumn(0).setPreferredWidth(40);
            tablaMovimientos.getColumnModel().getColumn(0).setMaxWidth(40);
            tablaMovimientos.getColumnModel().getColumn(1).setMinWidth(70);
            tablaMovimientos.getColumnModel().getColumn(1).setPreferredWidth(70);
            tablaMovimientos.getColumnModel().getColumn(1).setMaxWidth(70);
            tablaMovimientos.getColumnModel().getColumn(2).setMinWidth(350);
            tablaMovimientos.getColumnModel().getColumn(2).setPreferredWidth(350);
            tablaMovimientos.getColumnModel().getColumn(2).setMaxWidth(350);
        }
        modeloTablaMovimientos = (DefaultTableModel)tablaMovimientos.getModel();

        jButton1.setText("Facturar");

        lblSaldoTotal.setBackground(new java.awt.Color(153, 153, 153));
        lblSaldoTotal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblSaldoTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblSaldoTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSaldoTotal.setText("-700");
        lblSaldoTotal.setOpaque(true);

        jRadioButton1.setText("jRadioButton1");

        jRadioButton2.setText("jRadioButton2");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jRadioButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButton1)
                        .addGap(18, 18, 18)
                        .addComponent(lblSaldoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(lblSaldoTotal)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerarCuotasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarCuotasActionPerformed
        try {
            Movimientos movimientos = new Movimientos();
            movimientos.generarCuotasTodas("2016-03-01");
            JOptionPane.showMessageDialog(this, "Cuota/s generada/s con exito.");
        } catch (SQLException ex) {
            Logger.getLogger(DialogoMovimientos.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Hubo un error al generar cuotas: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnGenerarCuotasActionPerformed

    private void btnAbmDetallesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbmDetallesActionPerformed
        DialogoABMDetalles abm = new DialogoABMDetalles();
            
        abm.setTitle("Detalles");
        abm.pack();
        abm.setAlwaysOnTop(true);
        abm.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        abm.setLocationRelativeTo(this);
        //dmov.matricula = m.getMatricula();
        abm.setVisible(true);
    }//GEN-LAST:event_btnAbmDetallesActionPerformed

    private void cbConceptoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbConceptoItemStateChanged
        setearDatosDetalle();
        if(!cbConcepto.getSelectedItem().toString().isEmpty()){
            cbDetalle.setEnabled(true);
        } else {
            cbDetalle.setEnabled(false);
        }
        habilitarBtnGuardar();
    }//GEN-LAST:event_cbConceptoItemStateChanged

    private void btnGuardarPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarPagoActionPerformed
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            
           
            
            if(cbConcepto.getSelectedItem().toString().contains("Débito")){
                Movimientos m = new Movimientos();
                m.setId_movimiento(0);
                m.setMatricula(this.m.getMatricula());
                m.setId_alumno(0);
                m.setDebe(Double.parseDouble(txtMonto.getText()));
                m.setHaber(0);
                m.setFecha(calendar.getTime());
                calendar.add(Calendar.MONTH, 1);
                m.setFecha_a_pagar(calendar.getTime());
                m.setId_factura(0);
                m.setId_recibo(0);
                m.setId_nota_credito(0);
                m.setSaldado(0);
                m.setFacturado(0);
                m.setConcepto(cbConcepto.getSelectedItem().toString().substring(3)+"."+cbDetalle.getSelectedItem().toString());
                m.setId_concepto(Integer.parseInt(cbConcepto.getSelectedItem().toString().substring(0, 2)));
                m.setId_movimiento_contra(0);

                m.setHora(Time.valueOf(LocalTime.now()));
                m.setSaldo(Movimientos.obtenerSaldoMatricula(m.getMatricula())-m.getDebe()+m.getHaber());
                m.guardarComoNueva();
                
                Object [] args = new Object[6];
                args[0] = String.format("%d",m.getId_movimiento());
                args[1] = String.format("%s", m.getFecha().toString());
                args[2] = String.format("%s", m.getConcepto());
                args[3] = String.format("%.2f", m.getDebe());
                args[4] = String.format("%.2f", m.getHaber());
                args[5] = String.format("%.2f", m.getSaldo());
                modeloTablaMovimientos.addRow(args);
            } else {
                DefaultTableModel model = new DefaultTableModel();
                
                Object[] columnas = new Object[5];
                columnas[0]="Id";
                columnas[2]="Mes";
                columnas[3]="Curso/Año";
                columnas[1]="Tipo Mov";
                columnas[4]="Saldo";

                Object[][] filas = new Object[1][5];
                filas[0][0] = "";
                filas[0][1] = "NOTACREDITO";
                filas[0][2] = MesesString.obtenerMes(calendar.get(Calendar.MONTH));
                filas[0][3] = cbDetalle.getSelectedItem().toString();
                filas[0][4] = txtMonto.getText();
                
                model.setDataVector(filas,columnas);
                
                DialogoFacturar df = new DialogoFacturar(m,"factura",model);
                df.setTitle("Facturar");
                df.pack();
                df.setAlwaysOnTop(true);
                df.setModal(true);
                df.setLocationRelativeTo(rootPane);
                df.setVisible(true);
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(DialogoMovimientos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGuardarPagoActionPerformed

    private void cbDetalleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbDetalleItemStateChanged
        habilitarBtnGuardar();
    }//GEN-LAST:event_cbDetalleItemStateChanged

    private void txtMontoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMontoFocusLost
        if(!txtMonto.getText().isEmpty()){
            cbConcepto.setEnabled(true);
        } else {
            cbConcepto.setEnabled(false);
        }
        habilitarBtnGuardar();
            
    }//GEN-LAST:event_txtMontoFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbmDetalles;
    private javax.swing.JButton btnGenerarCuotas;
    private javax.swing.JButton btnGuardarPago;
    private javax.swing.JComboBox<String> cbAnio;
    private javax.swing.JComboBox<String> cbConcepto;
    private javax.swing.JComboBox<String> cbCursos;
    private javax.swing.JComboBox<String> cbDetalle;
    private javax.swing.JComboBox<String> cbMeses;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDatosMatricula;
    private javax.swing.JLabel lblSaldoTotal;
    private javax.swing.JCheckBox rbTodosAlumnos;
    private javax.swing.JTable tablaMovimientos;
    private javax.swing.JTextField txtMonto;
    // End of variables declaration//GEN-END:variables
}
