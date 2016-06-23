/*
 * To change this license header, choose License Headers in Project Properties.
 * To
    @Override
    protected void computeTime() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void computeFields() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void roll(int i, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getMinimum(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getMaximum(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getGreatestMinimum(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getLeastMaximum(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itte;

import itte.clases.modelos.Clientes;
import itte.clases.Configuraciones;
import itte.clases.modelos.Cursos;
import itte.clases.modelos.CursosNombres;
import itte.clases.Funciones;
import itte.clases.Log;
import itte.clases.MesesString;
import itte.clases.modelos.Certificado;
import itte.clases.modelos.Horarios;
import itte.clases.modelos.Matriculas;
import itte.clases.modelos.Movimientos;
import itte.clases.modelos.PlanPagoCuotas;
import itte.clases.modelos.Profesores;
import itte.clases.modelos.Usuarios;
import itte.menus.ContextCursosQueRealiza;
import itte.menus.ManejadorClicDerecho;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RootPaneContainer;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import jdk.nashorn.internal.parser.DateParser;
import org.jdesktop.swingx.JXDatePicker;


/**
 *
 * @author Toska
 */
public class ItteVentana extends javax.swing.JFrame {
    
    private final int CAMPO_ID      = 0;
    private final int CAMPO_MOV     = 1;
    private final int CAMPO_MES     = 2;
    private final int CAMPO_CURSO   = 3;
    private final int CAMPO_SALDO   = 4;

    private final static String MASTER_PASS = "adminpazos275";
    private int idSiguienteCliente;
    private int idSiguienteMatricula;
    private Usuarios sesion = null;
    private Component aux;
    private JXDatePicker jXDatePicker = new JXDatePicker(new Date());
    private boolean clienteSeleccionado = false;
    private DefaultTableModel matriculaModeloTablaCurso;
    private DefaultTableModel modeloTablaMovimientos;
    
    private PanelProfesores tabProfes;
    private PanelExamenes tabExam;
    private PanelCursos tabCursos;
    private PanelPagos tabPagos;
    
    
    /**
     * Creates new form ItteVentana
     */
    public ItteVentana() {
        
        initComponents();

        
        tabCursos = new PanelCursos();
        jTabbedPane1.addTab("Cursos", tabCursos);
        
        tabProfes = new PanelProfesores();
        jTabbedPane1.addTab("Profesores", tabProfes);
        
        tabExam = new PanelExamenes();
        jTabbedPane1.addTab("Exámenes", tabExam);
        
        
        iniciarSesion();

        if (sesion == null) {
            System.exit(0);
        }
        Configuraciones configuraciones = new Configuraciones();
        configuraciones.setSesion(sesion.getNombre_usuario());
        configuraciones.guardar();
        
        obtenerUltimas30Matriculas();
        seteosVarios();

        idSiguienteCliente = new Clientes().getIdUltimoCliente();
        idSiguienteMatricula = new Matriculas().getIdUltimaMatricula();
        matriculaLblMatricula.setText(String.valueOf(idSiguienteMatricula));
        
        ContextCursosQueRealiza cCQR = new ContextCursosQueRealiza();
        ManejadorClicDerecho manejador = new ManejadorClicDerecho(cCQR);
        matriculaTablaCursosInscriptos.addMouseListener(manejador);

    }
// <editor-fold defaultstate="collapsed" desc="Metodos"> 
    private void abrirDialogoBusqueda(Component referente, String busqueda) {
        dialogoBusqueda.setTitle(busqueda);
        dialogoBusqueda.pack();
        dialogoBusqueda.setAlwaysOnTop(true);
        dialogoBusqueda.setModal(true);
        dialogoBusqueda.setLocationRelativeTo(referente);
        dialogoBusqueda.setVisible(true);
    }
    
    private void abrirDialogoDatePicker() {
        dialogoDatePicker.setTitle("Fecha");
        dialogoDatePicker.pack();
        dialogoDatePicker.setAlwaysOnTop(true);
        dialogoDatePicker.setModal(true);
        dialogoDatePicker.setLocationRelativeTo(jTabbedPane1);
        dialogoDatePicker.setVisible(true);
    }

    private void abrirDialogoConfig() {
        dialogoConfiguracionSistema.setTitle("Configuración del Sistema");
        dialogoConfiguracionSistema.pack();
        dialogoConfiguracionSistema.setAlwaysOnTop(true);
        dialogoConfiguracionSistema.setModal(true);
        dialogoConfiguracionSistema.setLocationRelativeTo(jTabbedPane1);
        Configuraciones config = new Configuraciones();
        dialogoConfiguracionSistemaTxtServidor.setText(config.getServidor());
        dialogoConfiguracionSistema.setVisible(true);

    }
    private void seteardatepicker(){
        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(dialogoDatePicker.getContentPane());
        dialogoDatePicker.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jXDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jXDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }
    private void abrirDialogoSelecColor(JTextField jtf) {
        dialogoSelecColor.setTitle("Selección Color");
        aux = jtf;
        dialogoSelecColor.pack();
        dialogoSelecColor.setAlwaysOnTop(true);
        dialogoSelecColor.setModal(true);
        dialogoSelecColor.setLocationRelativeTo(jTabbedPane1);
        dialogoSelecColor.setVisible(true);
        
    }
    
    private void setearTablaMovimientos(Matriculas m) throws SQLException{
        if(modeloTablaMovimientos.getRowCount()>0){
            for(int i = modeloTablaMovimientos.getRowCount() - 1; i>=0; i--){
                modeloTablaMovimientos.removeRow(i);
            }
        }
        
        ListSelectionModel model = tablaMovimientos.getSelectionModel();
        model.clearSelection();
        
        double saldoTotal=0;
        
        //boolean soloAFacturar = true;
        //Movimientos movimientos = new Movimientos(m.getMatricula(),soloAFacturar);
        
        PlanPagoCuotas ppc = new PlanPagoCuotas();
        ResultSet rs = ppc.obtenerCuotasAFacturar(m.getMatricula());
        //rs: tipomov, movid, fechapagoorderby, idalumno, valorcuota
        Calendar calendar = Calendar.getInstance();
        
        while(rs.next()){
            Object [] args = new Object[5];
            
            if(rs.getString("fechapagoorderby")!=null){
                calendar.setTime(Funciones.obtenerDate(rs.getString("fechapagoorderby")));
                args[CAMPO_MES] = String.format("%s", MesesString.obtenerMes(calendar.get(Calendar.MONTH)));
            } else {
                args[CAMPO_MES] = "-";
            }
            
            args[CAMPO_ID] = rs.getString("movid");
            
            
            
            args[CAMPO_MOV] = rs.getString("tipomov");
            
            if(args[1].equals("CUOTA") || args[1].equals("CERTIFICADO"))
                args[CAMPO_CURSO] = Cursos.obtenerNombreSegunIdAlumno(rs.getInt("idalumno"));
            else
                args[CAMPO_CURSO] = "-";
            
            args[CAMPO_SALDO] = rs.getString("valorcuota");
            
            modeloTablaMovimientos.addRow(args);
            /*if(movimientos.getSaldado()==0){
                model.addSelectionInterval(modeloTablaMovimientos.getRowCount()-1, modeloTablaMovimientos.getRowCount()-1);
            }*/
            saldoTotal += rs.getDouble("valorcuota");
        }
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaMovimientos.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tablaMovimientos.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        //tablaMovimientos.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        
        lblSaldoTotal.setText(String.format("%.2f",saldoTotal));
    }
    
    private void setearTablaMovimientos(String idAlumno) throws SQLException{
        if(modeloTablaMovimientos.getRowCount()>0){
            for(int i = modeloTablaMovimientos.getRowCount() - 1; i>=0; i--){
                modeloTablaMovimientos.removeRow(i);
            }
        }
        
        ListSelectionModel model = tablaMovimientos.getSelectionModel();
        model.clearSelection();
        
        double saldoTotal=0;
        
        //boolean soloAFacturar = true;
        //Movimientos movimientos = new Movimientos(m.getMatricula(),soloAFacturar);
        
        PlanPagoCuotas ppc = new PlanPagoCuotas();
        ResultSet rs = ppc.obtenerCuotasAFacturar(idAlumno);
        //rs: tipomov, movid, fechapagoorderby, idalumno, valorcuota
        Calendar calendar = Calendar.getInstance();
        
        while(rs.next()){
            Object [] args = new Object[5];
            
            if(rs.getString("fechapagoorderby")!=null){
                calendar.setTime(Funciones.obtenerDate(rs.getString("fechapagoorderby")));
                args[CAMPO_MES] = String.format("%s", MesesString.obtenerMes(calendar.get(Calendar.MONTH)));
            } else {
                args[CAMPO_MES] = "-";
            }
            
            args[CAMPO_ID] = rs.getString("movid");
            
            
            
            args[CAMPO_MOV] = rs.getString("tipomov");
            
            if(args[1].equals("CUOTA") || args[1].equals("CERTIFICADO"))
                args[CAMPO_CURSO] = Cursos.obtenerNombreSegunIdAlumno(rs.getInt("idalumno"));
            else
                args[CAMPO_CURSO] = "-";
            
            args[CAMPO_SALDO] = rs.getString("valorcuota");
            
            modeloTablaMovimientos.addRow(args);
            /*if(movimientos.getSaldado()==0){
                model.addSelectionInterval(modeloTablaMovimientos.getRowCount()-1, modeloTablaMovimientos.getRowCount()-1);
            }*/
            saldoTotal += rs.getDouble("valorcuota");
        }
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaMovimientos.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tablaMovimientos.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        //tablaMovimientos.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        
        lblSaldoTotal.setText(String.format("%.2f",saldoTotal));
    }

    private boolean checkMasterPass(String p) {
        boolean r = false;
        if (p.equals(MASTER_PASS)) {
            r = true;
        }
        return r;
    }

    private void abrirDialogoMasterPass() {
        dialogoMasterPass.setTitle("Acceso Restringido");
        dialogoMasterPass.pack();
        dialogoMasterPass.setAlwaysOnTop(true);
        dialogoMasterPass.setModal(true);
        dialogoMasterPass.setLocationRelativeTo(jTabbedPane1);
        dialogoMasterPass.setVisible(true);
    }

    private void iniciarSesion() {
        dialogoInicioSesion.setTitle("Iniciar Sesion");
        dialogoInicioSesion.pack();
        dialogoInicioSesion.setAlwaysOnTop(true);
        dialogoInicioSesion.setModal(true);
        dialogoInicioSesion.setLocationRelativeTo(jTabbedPane1);
        dialogoInicioSesion.setVisible(true);
        
    }

    private void setearDatePicker(JXDatePicker jxdp){
        jxdp.setDate(new java.sql.Date((Calendar.getInstance()).getTimeInMillis()));

        jxdp.setFormats(new SimpleDateFormat("yyyy-MM-dd"));

        jxdp.setTimeZone(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
    }
    
    private void mostrarDatosClienteEnFormulario(Clientes c) {
        if (c != null) {
            dialogoNuevoClienteIdCliente.setText(String.valueOf(c.getId_cliente()));
            ctxtRazonSocial1.setText(c.getRazonSocial());
            ctxtLocalidad1.setText(c.getLocalidad());
            ctxtActividad1.setText(c.getActividad());
            ctxtDireccion1.setText(c.getDireccion());
            ctxtEmail1.setText(c.getEmail());
            jXDatePicker6.getEditor().setText(c.getFechaAlta());
            //ctxtFechaAlta.setText(Funciones.convertirFecha(c.getFechaAlta()));
            ctxtProvincia1.setText(c.getProvincia());
            String clienteTelefono = c.getTelefono();
            ctxtTelefono1.setText(clienteTelefono);
            String cuit;
            cuit = c.getCuit();
            if (cuit.contains("-")) {
                String[] aCuit = cuit.split("-");
                ctxtCuit4.setText(aCuit[0]);
                ctxtCuit5.setText(aCuit[1]);
                ctxtCuit6.setText(aCuit[2]);
            } else {
                ctxtCuit4.setText("");
                ctxtCuit5.setText(cuit);
                ctxtCuit6.setText("");
            }

            int ci = c.getCondicionIva();
            switch (ci) {
                case 0:
                    crbConsumidorFinal1.setSelected(true);
                    break;
                case 1:
                    crbMonotributista1.setSelected(true);
                    break;
                case 2:
                    crbResponsableInscripto1.setSelected(true);
                    break;
                case 3:
                    crbExento1.setSelected(true);
                    break;
            }

            int cv = c.getCondicionVenta();
            switch (cv) {
                case 0:
                    crbContado1.setSelected(true);
                    break;
                case 1:
                    crbTarjeta1.setSelected(true);
                    break;
                case 2:
                    crbOtra1.setSelected(true);
                    break;
            }
        }
    }

    private void seteosVarios() {
        matriculaLblFechaAlta.setText(Funciones.getFechaActual());
        matriculaCheckActivo.setSelected(true);
        matriculaCheckActivo.setEnabled(false);
        //ctxtFechaAlta.setText(Funciones.getFechaActual());
        itteVentanaLblUsuario.setText(sesion.getNombre());
        if (sesion.getAdministrador() != 3) {
            itteVentanaBtnConfigSistema.setEnabled(false);
        }
        matriculaModeloTablaCurso = (DefaultTableModel)matriculaTablaCursosInscriptos.getModel();
        
    }

    private void obtenerUltimas30Matriculas() throws HeadlessException {
        limpiarLista();
        Matriculas matriculas = new Matriculas();
        matriculas.mostrarUltimasParaLista();
        List<String> lista = new ArrayList<String>();
        while (matriculas.sigN()) {
            try {
                matriculas.getDatosParaLista();
                String item = matriculas.getDni() + " - " + matriculas.getApellido() + " " + matriculas.getNombre();
                lista.add(item);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
        armarLista(lista,bLista,jScrollPane1);
    }
    
    
    

    private void armarLista(List<String> lista, JList<String> jList, JScrollPane jScrollPane) {
        jList.setModel(new javax.swing.AbstractListModel<String>() {
            public int getSize() {
                return lista.size();
            }

            public String getElementAt(int i) {
                return lista.get(i);
            }
        });
        jScrollPane.setViewportView(jList);
    }
    
    
    
    private void limpiarLista() {
        List<String> lista = new ArrayList<String>();
        armarLista(lista,bLista,jScrollPane1);
    }

    private void armarListaSegunBusqueda(String s) {
        limpiarLista();
        Matriculas matriculas = new Matriculas();
        matriculas.busquedaParaLista(s);
        List<String> lista = new ArrayList<String>();
        while (matriculas.sigN()) {
            try {
                matriculas.getDatosParaLista();
                String item = matriculas.getDni() + " - " + matriculas.getApellido() + " " + matriculas.getNombre();
                lista.add(item);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
        armarLista(lista,bLista,jScrollPane1);
    }
    
    private void deshabilitarContenido(Container padre) {
        for (int i = padre.getComponentCount() - 1; i >= 0; i--) {
            final Component hijo = padre.getComponent(i);
            if(hijo != null){
                if (hijo instanceof JTextField) {
                    hijo.setEnabled(false); 
                } else {
                    if(hijo instanceof Container){
                        deshabilitarContenido((Container) hijo);
                    }

                }
            }
        }
    }
    
    private void habilitarContenido(Container padre) {
        for (int i = padre.getComponentCount() - 1; i >= 0; i--) {
            final Component hijo = padre.getComponent(i);
            if(hijo != null){
                if (hijo instanceof JTextField) {
                    hijo.setEnabled(true); 
                } else {
                    if(hijo instanceof Container){
                        habilitarContenido((Container) hijo);
                    }

                }
            }
        }
    }
    
    private boolean noHayCuotasIntermedias(){
        boolean r = true;
        boolean noEsta = true;
        
        String id;
        String tipoMov;
        
        int [] filas = tablaMovimientos.getSelectedRows();
        
        for(int i : filas){
            id = modeloTablaMovimientos.getValueAt(i, 0).toString();
            tipoMov = modeloTablaMovimientos.getValueAt(i, 3).toString();
            noEsta = false;
            switch(tipoMov){
                case "CUOTA":
                    PlanPagoCuotas ppc = new PlanPagoCuotas();
                    PlanPagoCuotas ppcTemp = new PlanPagoCuotas();
                    
                    ppc.obtenerSegunId(Integer.parseInt(id));
                    ppc.sig();
                    if(ppcTemp.hayCuotaAnteriorSinPagar(ppc.getIdplanpago(),ppc.getCuota()-1)){
                        noEsta = true;
                        String idControl;
                        String tipoMovControl;
                        for(int j : filas){
                            idControl = modeloTablaMovimientos.getValueAt(j, 0).toString();
                            tipoMovControl = modeloTablaMovimientos.getValueAt(j, 3).toString();
                            if(tipoMovControl.equals(tipoMov) && ppcTemp.getId() == Integer.parseInt(idControl)){
                                noEsta = false;
                                break;
                            }
                        }
                    } else {
                        noEsta = false;
                    }
                break;
                case "CERTIFICADO":
                    Certificado cert = new Certificado();
                    Certificado certTemp = new Certificado();
                    
                    String [] idYmes = id.split("-");
                    cert.obtenerSegunIdAlumnoYMes(idYmes[0], idYmes[1]);
                    cert.sig();
                    
                    if(certTemp.hayCertificadoAnteriorSinPagar(cert.getId_alumno(),cert.getMes())){
                        noEsta = true;
                        String idControl;
                        String tipoMovControl;
                        for(int j : filas){
                            idControl = modeloTablaMovimientos.getValueAt(j, 0).toString();
                            tipoMovControl = modeloTablaMovimientos.getValueAt(j, 3).toString();
                            if(tipoMovControl.equals(tipoMov) && String.format("%d-%d",certTemp.getId_alumno(),certTemp.getMes()).equals(idControl)){
                                noEsta = false;
                                break;
                            }
                        }
                    } else {
                        noEsta = false;
                    }
                break;
            }
            if(noEsta){
                r = false;
                break;
            }
        }
        return r;
    }
// </editor-fold>
    
// <editor-fold defaultstate="collapsed" desc="Eventos"> 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        condIva = new javax.swing.ButtonGroup();
        condVenta = new javax.swing.ButtonGroup();
        dialogoBusqueda = new javax.swing.JDialog();
        jLabel100 = new javax.swing.JLabel();
        dialogoBusquedaTxtParametro = new javax.swing.JTextField();
        dialogoBusquedaBtnBuscar = new javax.swing.JButton();
        dialogoInicioSesion = new javax.swing.JDialog();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        dialogoInicioSesionTxtUsuario = new javax.swing.JTextField();
        dialogoInicioSesionBtnIniciarSesion = new javax.swing.JButton();
        dialogoInicioSesionBtnConfig = new javax.swing.JButton();
        dialogoInicioSesionTxtPassword = new javax.swing.JPasswordField();
        dialogoConfiguracionSistema = new javax.swing.JDialog();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel19 = new javax.swing.JPanel();
        dialogoConfiguracionSistemaTxtServidor = new javax.swing.JTextField();
        dialogoConfiguracionSistemaBtnGuardar = new javax.swing.JButton();
        dialogoConfiguracionSistemaBtnCancelar = new javax.swing.JButton();
        jLabel104 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        dialogoConfiguracionSistemaTxtUsuarioBD = new javax.swing.JTextField();
        jLabel106 = new javax.swing.JLabel();
        dialogoConfiguracionSistemaTxtPassBD = new javax.swing.JPasswordField();
        jPanel20 = new javax.swing.JPanel();
        dialogoMasterPass = new javax.swing.JDialog();
        jLabel107 = new javax.swing.JLabel();
        dialogoMasterPassTxtPass = new javax.swing.JPasswordField();
        dialogoMasterPassBtnAceptar = new javax.swing.JButton();
        dialogoSelecColor = new javax.swing.JDialog();
        dialogoSelecColorColores = new javax.swing.JColorChooser();
        dialogoSelecColorBtnCancelar = new javax.swing.JButton();
        dialogoSelecColorBtnAceptar = new javax.swing.JButton();
        dialogoSelecColorLblTextField = new javax.swing.JLabel();
        dialogoDatePicker = new javax.swing.JDialog();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        dialogoNuevoCliente = new javax.swing.JDialog();
        clientes1 = new javax.swing.JPanel();
        jLabel115 = new javax.swing.JLabel();
        jLabel116 = new javax.swing.JLabel();
        jLabel117 = new javax.swing.JLabel();
        jLabel118 = new javax.swing.JLabel();
        jLabel119 = new javax.swing.JLabel();
        jLabel120 = new javax.swing.JLabel();
        ctxtRazonSocial1 = new javax.swing.JTextField();
        ctxtLocalidad1 = new javax.swing.JTextField();
        ctxtTelefono1 = new javax.swing.JTextField();
        ctxtCuit4 = new javax.swing.JTextField();
        ctxtCuit5 = new javax.swing.JTextField();
        ctxtCuit6 = new javax.swing.JTextField();
        crbConsumidorFinal1 = new javax.swing.JRadioButton();
        crbMonotributista1 = new javax.swing.JRadioButton();
        crbResponsableInscripto1 = new javax.swing.JRadioButton();
        crbExento1 = new javax.swing.JRadioButton();
        ctxtDireccion1 = new javax.swing.JTextField();
        jLabel121 = new javax.swing.JLabel();
        jLabel122 = new javax.swing.JLabel();
        jLabel123 = new javax.swing.JLabel();
        jLabel124 = new javax.swing.JLabel();
        ctxtProvincia1 = new javax.swing.JTextField();
        ctxtEmail1 = new javax.swing.JTextField();
        ctxtActividad1 = new javax.swing.JTextField();
        jLabel125 = new javax.swing.JLabel();
        crbContado1 = new javax.swing.JRadioButton();
        crbTarjeta1 = new javax.swing.JRadioButton();
        crbOtra1 = new javax.swing.JRadioButton();
        jXDatePicker6 = new org.jdesktop.swingx.JXDatePicker();
        jLabel7 = new javax.swing.JLabel();
        dialogoNuevoClienteIdCliente = new javax.swing.JLabel();
        dialogoNuevoClienteBtnGuardarSeleccionar = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        dialogoNuevoClienteLimpiarFormulario = new javax.swing.JButton();
        dialogoNuevoClienteModificar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        itteVentanaLblUsuario = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        tabMatriculas = new javax.swing.JPanel();
        busqueda = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        bLista = new javax.swing.JList<>();
        jPanel7 = new javax.swing.JPanel();
        btxtBusqueda = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        matriculaLblMatricula = new javax.swing.JLabel();
        matriculaCheckActivo = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        matriculaLblFechaAlta = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        matriculasBtnIguales = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        matriculaTxtApellido = new javax.swing.JTextField();
        matriculaTxtNombre = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        matriculaTxtLocalidad = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        matriculaTxtDni = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        matriculaTxtDomicilio = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        matriculaTxtZona = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        matriculaTxtProvincia = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        matriculaTxtCelular = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        matriculaTxtTelefono = new javax.swing.JTextField();
        matriculaTxtObservaciones = new javax.swing.JTextField();
        matriculaTxtComoLLego = new javax.swing.JTextField();
        matriculaTxtEmail = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        matriculaTxtCodigoPostal = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        matriculasBtnGuardarNuevaMatricula = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        matriculaTablaCursosInscriptos = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        matriculaAgregarMatACurso = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaMovimientos = new javax.swing.JTable();
        btnFacturar = new javax.swing.JButton();
        chkSoloCuotas = new javax.swing.JCheckBox();
        lblSaldoTotal = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        itteVentanaBtnConfigSistema = new javax.swing.JButton();

        condIva.add(crbConsumidorFinal1);
        condIva.add(crbMonotributista1);
        condIva.add(crbResponsableInscripto1);
        condIva.add(crbExento1);

        condVenta.add(crbContado1);
        condVenta.add(crbTarjeta1);
        condVenta.add(crbOtra1);

        jLabel100.setText("Parametro de Busqueda:");

        dialogoBusquedaBtnBuscar.setText("Buscar");
        dialogoBusquedaBtnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogoBusquedaBtnBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dialogoBusquedaLayout = new javax.swing.GroupLayout(dialogoBusqueda.getContentPane());
        dialogoBusqueda.getContentPane().setLayout(dialogoBusquedaLayout);
        dialogoBusquedaLayout.setHorizontalGroup(
            dialogoBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogoBusquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel100)
                .addGap(18, 18, 18)
                .addGroup(dialogoBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dialogoBusquedaTxtParametro, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogoBusquedaLayout.createSequentialGroup()
                        .addComponent(dialogoBusquedaBtnBuscar)
                        .addGap(138, 138, 138)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dialogoBusquedaLayout.setVerticalGroup(
            dialogoBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogoBusquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogoBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel100)
                    .addComponent(dialogoBusquedaTxtParametro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(dialogoBusquedaBtnBuscar)
                .addContainerGap())
        );

        jLabel101.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel101.setText("Inicio de Sesión");

        jLabel102.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel102.setText("Usuario:");

        jLabel103.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel103.setText("Password:");

        dialogoInicioSesionTxtUsuario.setText("juan");
        dialogoInicioSesionTxtUsuario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dialogoInicioSesionTxtUsuarioKeyPressed(evt);
            }
        });

        dialogoInicioSesionBtnIniciarSesion.setText("Iniciar Sesión");
        dialogoInicioSesionBtnIniciarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogoInicioSesionBtnIniciarSesionActionPerformed(evt);
            }
        });

        dialogoInicioSesionBtnConfig.setText("Config");
        dialogoInicioSesionBtnConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogoInicioSesionBtnConfigActionPerformed(evt);
            }
        });

        dialogoInicioSesionTxtPassword.setText("jsolaro123");
        dialogoInicioSesionTxtPassword.setToolTipText("");
        dialogoInicioSesionTxtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dialogoInicioSesionTxtPasswordKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout dialogoInicioSesionLayout = new javax.swing.GroupLayout(dialogoInicioSesion.getContentPane());
        dialogoInicioSesion.getContentPane().setLayout(dialogoInicioSesionLayout);
        dialogoInicioSesionLayout.setHorizontalGroup(
            dialogoInicioSesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogoInicioSesionLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel101)
                .addGap(106, 106, 106))
            .addGroup(dialogoInicioSesionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogoInicioSesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogoInicioSesionLayout.createSequentialGroup()
                        .addGroup(dialogoInicioSesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel102)
                            .addComponent(jLabel103))
                        .addGap(18, 18, 18)
                        .addGroup(dialogoInicioSesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dialogoInicioSesionTxtUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                            .addComponent(dialogoInicioSesionTxtPassword))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogoInicioSesionLayout.createSequentialGroup()
                        .addComponent(dialogoInicioSesionBtnConfig)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dialogoInicioSesionBtnIniciarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        dialogoInicioSesionLayout.setVerticalGroup(
            dialogoInicioSesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogoInicioSesionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel101)
                .addGap(18, 18, 18)
                .addGroup(dialogoInicioSesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel102)
                    .addComponent(dialogoInicioSesionTxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(dialogoInicioSesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel103)
                    .addComponent(dialogoInicioSesionTxtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(dialogoInicioSesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dialogoInicioSesionBtnIniciarSesion)
                    .addComponent(dialogoInicioSesionBtnConfig))
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Configuración del Sistema");

        dialogoConfiguracionSistemaBtnGuardar.setText("Guardar");
        dialogoConfiguracionSistemaBtnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogoConfiguracionSistemaBtnGuardarActionPerformed(evt);
            }
        });

        dialogoConfiguracionSistemaBtnCancelar.setText("Cancelar");
        dialogoConfiguracionSistemaBtnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogoConfiguracionSistemaBtnCancelarActionPerformed(evt);
            }
        });

        jLabel104.setText("Nombre o Ip del Servidor");

        jLabel105.setText("Setear Usuario BD (dejar vacio si no desea modificar esta opción)");

        jLabel106.setText("Setear Password de Usuario BD (dejar vacio si no se desea modificar esta opción)");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(dialogoConfiguracionSistemaBtnGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dialogoConfiguracionSistemaBtnCancelar))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel104)
                            .addComponent(dialogoConfiguracionSistemaTxtServidor, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel105)
                            .addComponent(dialogoConfiguracionSistemaTxtUsuarioBD, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel106)
                            .addComponent(dialogoConfiguracionSistemaTxtPassBD, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel104)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogoConfiguracionSistemaTxtServidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel105)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogoConfiguracionSistemaTxtUsuarioBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel106)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogoConfiguracionSistemaTxtPassBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dialogoConfiguracionSistemaBtnCancelar)
                    .addComponent(dialogoConfiguracionSistemaBtnGuardar)))
        );

        jTabbedPane2.addTab("Varios", jPanel19);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 410, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 240, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Usuarios Sistema", jPanel20);

        javax.swing.GroupLayout dialogoConfiguracionSistemaLayout = new javax.swing.GroupLayout(dialogoConfiguracionSistema.getContentPane());
        dialogoConfiguracionSistema.getContentPane().setLayout(dialogoConfiguracionSistemaLayout);
        dialogoConfiguracionSistemaLayout.setHorizontalGroup(
            dialogoConfiguracionSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogoConfiguracionSistemaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jTabbedPane2)
        );
        dialogoConfiguracionSistemaLayout.setVerticalGroup(
            dialogoConfiguracionSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogoConfiguracionSistemaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2))
        );

        jLabel107.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel107.setText("Ingrese Master Pass");

        dialogoMasterPassBtnAceptar.setText("Aceptar");
        dialogoMasterPassBtnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogoMasterPassBtnAceptarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dialogoMasterPassLayout = new javax.swing.GroupLayout(dialogoMasterPass.getContentPane());
        dialogoMasterPass.getContentPane().setLayout(dialogoMasterPassLayout);
        dialogoMasterPassLayout.setHorizontalGroup(
            dialogoMasterPassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogoMasterPassLayout.createSequentialGroup()
                .addGroup(dialogoMasterPassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogoMasterPassLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(dialogoMasterPassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel107, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                            .addComponent(dialogoMasterPassTxtPass)))
                    .addGroup(dialogoMasterPassLayout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addComponent(dialogoMasterPassBtnAceptar)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dialogoMasterPassLayout.setVerticalGroup(
            dialogoMasterPassLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogoMasterPassLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel107)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogoMasterPassTxtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogoMasterPassBtnAceptar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dialogoSelecColorBtnCancelar.setText("Cancelar");

        dialogoSelecColorBtnAceptar.setText("Aceptar");
        dialogoSelecColorBtnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogoSelecColorBtnAceptarActionPerformed(evt);
            }
        });

        dialogoSelecColorLblTextField.setText("jLabel108");

        javax.swing.GroupLayout dialogoSelecColorLayout = new javax.swing.GroupLayout(dialogoSelecColor.getContentPane());
        dialogoSelecColor.getContentPane().setLayout(dialogoSelecColorLayout);
        dialogoSelecColorLayout.setHorizontalGroup(
            dialogoSelecColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogoSelecColorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogoSelecColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogoSelecColorLayout.createSequentialGroup()
                        .addComponent(dialogoSelecColorColores, javax.swing.GroupLayout.PREFERRED_SIZE, 601, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogoSelecColorLayout.createSequentialGroup()
                        .addComponent(dialogoSelecColorLblTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dialogoSelecColorBtnAceptar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dialogoSelecColorBtnCancelar)))
                .addContainerGap())
        );
        dialogoSelecColorLayout.setVerticalGroup(
            dialogoSelecColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogoSelecColorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dialogoSelecColorColores, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dialogoSelecColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dialogoSelecColorBtnCancelar)
                    .addComponent(dialogoSelecColorBtnAceptar)
                    .addComponent(dialogoSelecColorLblTextField))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jXDatePicker1.setDate(new java.sql.Date((Calendar.getInstance()).getTimeInMillis()));
        jXDatePicker1.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
        jXDatePicker1.setTimeZone(TimeZone.getTimeZone("America/Argentina/Buenos_Aires")
        );

        javax.swing.GroupLayout dialogoDatePickerLayout = new javax.swing.GroupLayout(dialogoDatePicker.getContentPane());
        dialogoDatePicker.getContentPane().setLayout(dialogoDatePickerLayout);
        dialogoDatePickerLayout.setHorizontalGroup(
            dialogoDatePickerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogoDatePickerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jXDatePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dialogoDatePickerLayout.setVerticalGroup(
            dialogoDatePickerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogoDatePickerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jXDatePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(161, Short.MAX_VALUE))
        );

        clientes1.setBackground(new java.awt.Color(255, 255, 204));

        jLabel115.setText("Razón Social");

        jLabel116.setText("Localidad");

        jLabel117.setText("Teléfono");

        jLabel118.setText("Fecha Alta");

        jLabel119.setText("Cuit");

        jLabel120.setText("Cond. IVA");

        ctxtRazonSocial1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ctxtRazonSocial1KeyReleased(evt);
            }
        });

        crbConsumidorFinal1.setBackground(new java.awt.Color(255, 255, 204));
        crbConsumidorFinal1.setText("C.F.");

        crbMonotributista1.setBackground(new java.awt.Color(255, 255, 204));
        crbMonotributista1.setText("Mono");

        crbResponsableInscripto1.setBackground(new java.awt.Color(255, 255, 204));
        crbResponsableInscripto1.setText("R.I.");

        crbExento1.setBackground(new java.awt.Color(255, 255, 204));
        crbExento1.setText("Exen");

        jLabel121.setText("Dirección");

        jLabel122.setText("Provincia");

        jLabel123.setText("Email");

        jLabel124.setText("Actividad");

        jLabel125.setText("Cond. Venta");

        crbContado1.setBackground(new java.awt.Color(255, 255, 204));
        crbContado1.setText("Conta");

        crbTarjeta1.setBackground(new java.awt.Color(255, 255, 204));
        crbTarjeta1.setText("Tarj");

        crbOtra1.setBackground(new java.awt.Color(255, 255, 204));
        crbOtra1.setText("Otra");

        setearDatePicker(jXDatePicker6);

        jLabel7.setText("ID Cliente");

        dialogoNuevoClienteIdCliente.setText("-");

        javax.swing.GroupLayout clientes1Layout = new javax.swing.GroupLayout(clientes1);
        clientes1.setLayout(clientes1Layout);
        clientes1Layout.setHorizontalGroup(
            clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientes1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(clientes1Layout.createSequentialGroup()
                        .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel115, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel116, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel117, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel118, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel119, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(clientes1Layout.createSequentialGroup()
                                .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(clientes1Layout.createSequentialGroup()
                                        .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(ctxtLocalidad1, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                            .addComponent(ctxtTelefono1))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                    .addGroup(clientes1Layout.createSequentialGroup()
                                        .addComponent(ctxtRazonSocial1)
                                        .addGap(10, 10, 10)))
                                .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel121)
                                    .addComponent(jLabel122)
                                    .addGroup(clientes1Layout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addComponent(jLabel123))))
                            .addGroup(clientes1Layout.createSequentialGroup()
                                .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(clientes1Layout.createSequentialGroup()
                                        .addComponent(ctxtCuit4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ctxtCuit5, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ctxtCuit6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jXDatePicker6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel124)
                                    .addComponent(jLabel7)))))
                    .addGroup(clientes1Layout.createSequentialGroup()
                        .addComponent(jLabel120)
                        .addGap(18, 18, 18)
                        .addComponent(crbConsumidorFinal1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(crbMonotributista1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(crbResponsableInscripto1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(crbExento1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(clientes1Layout.createSequentialGroup()
                            .addComponent(jLabel125)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(crbContado1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(crbTarjeta1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(crbOtra1))
                        .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(ctxtEmail1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                            .addComponent(ctxtProvincia1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ctxtDireccion1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ctxtActividad1)))
                    .addComponent(dialogoNuevoClienteIdCliente))
                .addGap(0, 11, Short.MAX_VALUE))
        );
        clientes1Layout.setVerticalGroup(
            clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientes1Layout.createSequentialGroup()
                .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(clientes1Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ctxtLocalidad1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel122)
                            .addComponent(ctxtProvincia1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ctxtTelefono1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ctxtEmail1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel123))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ctxtActividad1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel118)
                            .addComponent(jLabel124)
                            .addComponent(jXDatePicker6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ctxtCuit5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ctxtCuit4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ctxtCuit6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel119)
                            .addComponent(jLabel7)
                            .addComponent(dialogoNuevoClienteIdCliente)))
                    .addGroup(clientes1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ctxtRazonSocial1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ctxtDireccion1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel121)
                            .addComponent(jLabel115))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel116)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel117)))
                .addGap(18, 18, 18)
                .addGroup(clientes1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel120)
                    .addComponent(crbConsumidorFinal1)
                    .addComponent(crbMonotributista1)
                    .addComponent(crbResponsableInscripto1)
                    .addComponent(crbExento1)
                    .addComponent(jLabel125)
                    .addComponent(crbContado1)
                    .addComponent(crbTarjeta1)
                    .addComponent(crbOtra1))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        dialogoNuevoClienteBtnGuardarSeleccionar.setText("Guardar/ Seleccionar");
        dialogoNuevoClienteBtnGuardarSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogoNuevoClienteBtnGuardarSeleccionarActionPerformed(evt);
            }
        });

        jButton5.setText("Cancelar");

        dialogoNuevoClienteLimpiarFormulario.setText("Limpiar Formulario");
        dialogoNuevoClienteLimpiarFormulario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogoNuevoClienteLimpiarFormularioActionPerformed(evt);
            }
        });

        dialogoNuevoClienteModificar.setText("Modificar");
        dialogoNuevoClienteModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogoNuevoClienteModificarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dialogoNuevoClienteLayout = new javax.swing.GroupLayout(dialogoNuevoCliente.getContentPane());
        dialogoNuevoCliente.getContentPane().setLayout(dialogoNuevoClienteLayout);
        dialogoNuevoClienteLayout.setHorizontalGroup(
            dialogoNuevoClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogoNuevoClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogoNuevoClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogoNuevoClienteLayout.createSequentialGroup()
                        .addComponent(clientes1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(dialogoNuevoClienteLayout.createSequentialGroup()
                        .addComponent(dialogoNuevoClienteBtnGuardarSeleccionar)
                        .addGap(18, 18, 18)
                        .addComponent(dialogoNuevoClienteModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dialogoNuevoClienteLimpiarFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dialogoNuevoClienteLayout.setVerticalGroup(
            dialogoNuevoClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogoNuevoClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(clientes1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(dialogoNuevoClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dialogoNuevoClienteBtnGuardarSeleccionar)
                    .addComponent(jButton5)
                    .addComponent(dialogoNuevoClienteLimpiarFormulario)
                    .addComponent(dialogoNuevoClienteModificar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Arial", 3, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(10, 36, 106));
        jLabel1.setText("ITTE");

        jLabel2.setFont(new java.awt.Font("Arial", 3, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(10, 36, 106));
        jLabel2.setText("Instituto de Tecnología Informática");

        itteVentanaLblUsuario.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        itteVentanaLblUsuario.setForeground(new java.awt.Color(18, 152, 21));
        itteVentanaLblUsuario.setText("Mariano");

        jLabel4.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(18, 152, 21));
        jLabel4.setText("Usuario:");

        jLabel5.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(18, 152, 21));
        jLabel5.setText("02/01/2015");

        tabMatriculas.setBackground(new java.awt.Color(255, 255, 255));

        busqueda.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        bLista.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bListaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(bLista);

        jPanel7.setBackground(new java.awt.Color(49, 106, 197));

        btxtBusqueda.setText("Por nombre, apellido, dni o matrícula");
        btxtBusqueda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btxtBusquedaFocusGained(evt);
            }
        });
        btxtBusqueda.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                btxtBusquedaPropertyChange(evt);
            }
        });
        btxtBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btxtBusquedaKeyReleased(evt);
            }
        });

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("<html><b>Buscar</b></html>");
        jLabel11.setToolTipText("");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btxtBusqueda)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btxtBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout busquedaLayout = new javax.swing.GroupLayout(busqueda);
        busqueda.setLayout(busquedaLayout);
        busquedaLayout.setHorizontalGroup(
            busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(busquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                .addContainerGap())
        );
        busquedaLayout.setVerticalGroup(
            busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(busquedaLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(231, 237, 237));

        jLabel6.setText("Matrícula");

        matriculaLblMatricula.setText("0000");

        matriculaCheckActivo.setBackground(new java.awt.Color(231, 237, 237));
        matriculaCheckActivo.setText("Activo");
        matriculaCheckActivo.setSelected(true);

        jLabel8.setText("Fecha Alta:");

        matriculaLblFechaAlta.setText("02/01/2015");

        jLabel27.setText("Fecha Nacim");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel10.setText("DATOS BÁSICOS Y DE FACTURACIÓN");

        matriculasBtnIguales.setText("Cliente Responsable Pago");
        matriculasBtnIguales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matriculasBtnIgualesActionPerformed(evt);
            }
        });

        jLabel19.setText("Apellido");

        jLabel20.setText("Nombre");

        jLabel28.setText("Localidad");

        jLabel26.setText("DNI");

        jLabel21.setText("Domicilio");

        jLabel22.setText("Zona");

        jLabel29.setText("Provincia");

        jLabel30.setText("Celular");

        jLabel23.setText("Teléfono");

        jLabel24.setText("Observaciones");

        jLabel25.setText("Email");

        jLabel31.setText("Cómo llegó");

        jLabel9.setText("Código Postal");

        setearDatePicker(jXDatePicker2);

        matriculasBtnGuardarNuevaMatricula.setText("Guardar");
        matriculasBtnGuardarNuevaMatricula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matriculasBtnGuardarNuevaMatriculaActionPerformed(evt);
            }
        });

        jButton3.setText("Limpiar Formulario");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGap(58, 58, 58)
                                    .addComponent(jLabel25))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jLabel24))
                                .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(matriculaTxtObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(matriculaTxtTelefono, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(matriculaTxtZona, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(matriculaTxtDomicilio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel31)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(matriculaTxtComoLLego, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(matriculaTxtDni, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel27))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addGap(0, 0, Short.MAX_VALUE)
                                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabel29)
                                                .addComponent(jLabel28)
                                                .addComponent(jLabel30)))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(matriculaTxtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel20)))
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(matriculaTxtProvincia, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                                                .addComponent(matriculaTxtCelular)))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addGap(10, 10, 10)
                                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jXDatePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(matriculaTxtLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(matriculaTxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(matriculaLblMatricula)
                                .addGap(18, 18, 18)
                                .addComponent(matriculaCheckActivo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(matriculaLblFechaAlta))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(matriculaTxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(matriculaTxtCodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(matriculasBtnIguales)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(matriculasBtnGuardarNuevaMatricula)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(matriculasBtnIguales)
                    .addComponent(matriculasBtnGuardarNuevaMatricula)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(matriculaLblMatricula)
                    .addComponent(matriculaCheckActivo)
                    .addComponent(matriculaLblFechaAlta)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(matriculaTxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(matriculaTxtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jXDatePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addComponent(matriculaTxtDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(matriculaTxtLocalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28)
                            .addComponent(matriculaTxtDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(matriculaTxtProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(matriculaTxtCelular, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(matriculaTxtComoLLego, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(matriculaTxtZona, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(matriculaTxtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(matriculaTxtObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24))))
                .addGap(11, 11, 11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(matriculaTxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(matriculaTxtCodigoPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        matriculaTablaCursosInscriptos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Curso", "Horario", "Fecha Alta", "Nota Final", "Cuota", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        matriculaTablaCursosInscriptos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                matriculaTablaCursosInscriptosMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(matriculaTablaCursosInscriptos);
        if (matriculaTablaCursosInscriptos.getColumnModel().getColumnCount() > 0) {
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(0).setMinWidth(0);
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(0).setPreferredWidth(0);
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(0).setMaxWidth(0);
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(1).setPreferredWidth(175);
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(2).setPreferredWidth(175);
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(3).setMinWidth(75);
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(3).setPreferredWidth(75);
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(3).setMaxWidth(75);
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(4).setMinWidth(0);
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(4).setPreferredWidth(0);
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(4).setMaxWidth(0);
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(5).setMinWidth(0);
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(5).setPreferredWidth(0);
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(5).setMaxWidth(0);
            matriculaTablaCursosInscriptos.getColumnModel().getColumn(6).setPreferredWidth(30);
        }

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel12.setBackground(new java.awt.Color(232, 92, 0));
        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("CURSOS QUE REALIZA");
        jLabel12.setFocusable(false);
        jLabel12.setOpaque(true);

        matriculaAgregarMatACurso.setText("+");
        matriculaAgregarMatACurso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matriculaAgregarMatACursoActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(0, 102, 0));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setForeground(java.awt.Color.white);
        jButton1.setText("Movimientos");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        tablaMovimientos.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        tablaMovimientos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Mov", "Mes", "Curso/Año", "Saldo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaMovimientos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMovimientosMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tablaMovimientosMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tablaMovimientos);
        if (tablaMovimientos.getColumnModel().getColumnCount() > 0) {
            tablaMovimientos.getColumnModel().getColumn(0).setMinWidth(0);
            tablaMovimientos.getColumnModel().getColumn(0).setPreferredWidth(0);
            tablaMovimientos.getColumnModel().getColumn(0).setMaxWidth(0);
            tablaMovimientos.getColumnModel().getColumn(1).setMinWidth(60);
            tablaMovimientos.getColumnModel().getColumn(1).setPreferredWidth(60);
            tablaMovimientos.getColumnModel().getColumn(1).setMaxWidth(60);
            tablaMovimientos.getColumnModel().getColumn(2).setMinWidth(50);
            tablaMovimientos.getColumnModel().getColumn(2).setPreferredWidth(50);
            tablaMovimientos.getColumnModel().getColumn(2).setMaxWidth(50);
            tablaMovimientos.getColumnModel().getColumn(4).setMinWidth(70);
            tablaMovimientos.getColumnModel().getColumn(4).setPreferredWidth(70);
            tablaMovimientos.getColumnModel().getColumn(4).setMaxWidth(70);
        }
        modeloTablaMovimientos = (DefaultTableModel)tablaMovimientos.getModel();

        btnFacturar.setText("Facturar");
        btnFacturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFacturarActionPerformed(evt);
            }
        });

        chkSoloCuotas.setText("Solo Cuotas");
        chkSoloCuotas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkSoloCuotasItemStateChanged(evt);
            }
        });
        chkSoloCuotas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSoloCuotasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnFacturar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkSoloCuotas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(btnFacturar)
                    .addComponent(chkSoloCuotas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        lblSaldoTotal.setText("0000");

        jLabel14.setText("Saldo:");

        javax.swing.GroupLayout tabMatriculasLayout = new javax.swing.GroupLayout(tabMatriculas);
        tabMatriculas.setLayout(tabMatriculasLayout);
        tabMatriculasLayout.setHorizontalGroup(
            tabMatriculasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabMatriculasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabMatriculasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabMatriculasLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(busqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10))
                    .addGroup(tabMatriculasLayout.createSequentialGroup()
                        .addGroup(tabMatriculasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(tabMatriculasLayout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(matriculaAgregarMatACurso))
                            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabMatriculasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabMatriculasLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel14)
                                .addGap(18, 18, 18)
                                .addComponent(lblSaldoTotal)))
                        .addContainerGap())))
        );
        tabMatriculasLayout.setVerticalGroup(
            tabMatriculasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabMatriculasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabMatriculasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(busqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabMatriculasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabMatriculasLayout.createSequentialGroup()
                        .addGroup(tabMatriculasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(matriculaAgregarMatACurso))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 4, Short.MAX_VALUE))
                    .addGroup(tabMatriculasLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabMatriculasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblSaldoTotal)
                            .addComponent(jLabel14))))
                .addContainerGap())
        );

        tabPagos = new PanelPagos();
        jTabbedPane1.addTab("Pagos", tabPagos);
        jTabbedPane1.addTab("Matrículas", tabMatriculas);

        itteVentanaBtnConfigSistema.setText("Config.");
        itteVentanaBtnConfigSistema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itteVentanaBtnConfigSistemaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(itteVentanaBtnConfigSistema)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(itteVentanaLblUsuario))
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(itteVentanaLblUsuario)
                                    .addComponent(jLabel4)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(itteVentanaBtnConfigSistema)
                        .addGap(17, 17, 17)))
                .addComponent(jTabbedPane1))
        );

        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                String s = sourceTabbedPane.getTitleAt(index);
                switch (s){
                    case "Cursos":
                    tabCursos.obtenerNombresCursosParaComboBox();
                    tabCursos.obtenerCursos();
                    break;
                    case "Profesores":
                    tabProfes.obtenerProfesores();
                    break;
                }
            }
        };
        jTabbedPane1.addChangeListener(changeListener);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dialogoBusquedaBtnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogoBusquedaBtnBuscarActionPerformed
        String busqueda = dialogoBusqueda.getTitle();
        switch (busqueda) {
            case "Cliente-Cuit":
                Clientes cliente = new Clientes();
                if (cliente.buscarPorDniCuit(dialogoBusquedaTxtParametro.getText())) {
                    mostrarDatosClienteEnFormulario(cliente);
                    dialogoBusqueda.setVisible(false);
                } else {
                    dialogoBusquedaTxtParametro.setText("NO ENCONTRADO");
                }
                break;
        }

    }//GEN-LAST:event_dialogoBusquedaBtnBuscarActionPerformed

    private void dialogoInicioSesionBtnIniciarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogoInicioSesionBtnIniciarSesionActionPerformed
        String u, p;
        Usuarios usuario;
        u = dialogoInicioSesionTxtUsuario.getText();
        p = String.valueOf(dialogoInicioSesionTxtPassword.getPassword());
        usuario = new Usuarios(u, p);
        if (usuario.getNombre() != null) {
            if (!usuario.getNombre().equals("")) {
                sesion = usuario;
                dialogoInicioSesion.setVisible(false);
            }
        }
    }//GEN-LAST:event_dialogoInicioSesionBtnIniciarSesionActionPerformed

    private void dialogoInicioSesionTxtUsuarioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dialogoInicioSesionTxtUsuarioKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            dialogoInicioSesionBtnIniciarSesionActionPerformed(null);
        }
    }//GEN-LAST:event_dialogoInicioSesionTxtUsuarioKeyPressed

    private void itteVentanaBtnConfigSistemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itteVentanaBtnConfigSistemaActionPerformed
        abrirDialogoConfig();
    }//GEN-LAST:event_itteVentanaBtnConfigSistemaActionPerformed

    private void dialogoConfiguracionSistemaBtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogoConfiguracionSistemaBtnGuardarActionPerformed
        Configuraciones config = new Configuraciones();
            config.setServidor(dialogoConfiguracionSistemaTxtServidor.getText());
            if (!dialogoConfiguracionSistemaTxtUsuarioBD.getText().equals("") || !String.valueOf(dialogoConfiguracionSistemaTxtPassBD.getPassword()).equals("")) {
                abrirDialogoMasterPass();
                if (checkMasterPass(String.valueOf(dialogoMasterPassTxtPass.getPassword()))) {
                    if (!dialogoConfiguracionSistemaTxtUsuarioBD.getText().equals("")) {
                        config.setUsuario(dialogoConfiguracionSistemaTxtUsuarioBD.getText());
                    }
                    if (!String.valueOf(dialogoConfiguracionSistemaTxtPassBD.getPassword()).equals("")) {
                        config.setPassword(String.valueOf(dialogoConfiguracionSistemaTxtPassBD.getPassword()));
                    }
                }
            }
            dialogoConfiguracionSistema.setVisible(false);
            JOptionPane.showMessageDialog(null, "Tendrá que reiniciar el sistema para que los cambios tengan efecto.");
    }//GEN-LAST:event_dialogoConfiguracionSistemaBtnGuardarActionPerformed

    private void dialogoConfiguracionSistemaBtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogoConfiguracionSistemaBtnCancelarActionPerformed
        dialogoConfiguracionSistema.setVisible(false);
    }//GEN-LAST:event_dialogoConfiguracionSistemaBtnCancelarActionPerformed

    private void dialogoInicioSesionBtnConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogoInicioSesionBtnConfigActionPerformed
        abrirDialogoConfig();
    }//GEN-LAST:event_dialogoInicioSesionBtnConfigActionPerformed

    private void dialogoMasterPassBtnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogoMasterPassBtnAceptarActionPerformed
        dialogoMasterPass.setVisible(false);
    }//GEN-LAST:event_dialogoMasterPassBtnAceptarActionPerformed

    private void dialogoSelecColorBtnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogoSelecColorBtnAceptarActionPerformed
        Color color = dialogoSelecColorColores.getColor();
        String hex = Funciones.toHexString(color);
        ((JTextField)aux).setText(hex);
        ((JTextField)aux).setBackground(color);
        dialogoSelecColor.setVisible(false);
    }//GEN-LAST:event_dialogoSelecColorBtnAceptarActionPerformed

    private void dialogoNuevoClienteLimpiarFormularioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogoNuevoClienteLimpiarFormularioActionPerformed
        ctxtRazonSocial1.setText("");
        ctxtLocalidad1.setText("");
        ctxtActividad1.setText("");
        ctxtDireccion1.setText("");
        ctxtEmail1.setText("");
        //ctxtFechaAlta.setText("");
        ctxtProvincia1.setText("");
        ctxtTelefono1.setText("");

        ctxtCuit4.setText("");
        ctxtCuit5.setText("");
        ctxtCuit6.setText("");

        crbConsumidorFinal1.setSelected(true);

        crbContado1.setSelected(true);
        habilitarContenido(dialogoNuevoCliente);
    }//GEN-LAST:event_dialogoNuevoClienteLimpiarFormularioActionPerformed

    private void dialogoNuevoClienteBtnGuardarSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogoNuevoClienteBtnGuardarSeleccionarActionPerformed
        Clientes c  = new Clientes();
        
        if(bLista.getSelectedIndex()!=-1){
            int opt = JOptionPane.showConfirmDialog(dialogoNuevoCliente, "Hay una matricula seleccionada. Desea modificar los datos de cliente para esa matricula?");
            if(opt == JOptionPane.YES_OPTION){
                String item = bLista.getSelectedValue().toString();
                String dni = item.substring(0, item.indexOf("-") - 1);
                Matriculas m = new Matriculas();
                m.buscarPorDni(dni);
                m.setCliente();
                c = m.getCliente();
            } else {
                if (opt == JOptionPane.CANCEL_OPTION){
                    return;
                }
            }
        }
        

        boolean fallo;
        boolean ok = true;
        String mnsjerror = "Hay campos sin completar.";
        
        fallo = ctxtRazonSocial1.getText().equals("")
                || ctxtLocalidad1.getText().equals("")
                || ctxtActividad1.getText().equals("")
                || ctxtDireccion1.getText().equals("")
                || ctxtEmail1.getText().equals("")
                || jXDatePicker6.getEditor().getText().equals("")
                || ctxtProvincia1.getText().equals("")
                || ctxtTelefono1.getText().equals("");

        if (!fallo) {
            c.setRazonSocial(ctxtRazonSocial1.getText());
            c.setLocalidad(ctxtLocalidad1.getText());
            c.setActividad(ctxtActividad1.getText());
            c.setDireccion(ctxtDireccion1.getText());
            c.setEmail(ctxtEmail1.getText());
            c.setFechaAlta(jXDatePicker6.getEditor().getText());
            c.setProvincia(ctxtProvincia1.getText());
            c.setTelefono(ctxtTelefono1.getText());
        } else {
            ok = false;
        }

        if (ctxtCuit5.getText().equals("")) {
            ok = false;
        } else {
            if (ctxtCuit4.getText().equals("") || ctxtCuit6.getText().equals("")) {
                c.setCuit(ctxtCuit5.getText());

            } else {
                c.setCuit(ctxtCuit4.getText() + "-" + ctxtCuit5.getText() + "-" + ctxtCuit6.getText());
            }
        }

        fallo = !crbContado1.isSelected()
                && !crbTarjeta1.isSelected()
                && !crbOtra1.isSelected();
        if (!fallo) {
            if (crbContado1.isSelected()) {
                c.setCondicionVenta(c.CONTADO);
            }
            if (crbTarjeta1.isSelected()) {
                c.setCondicionVenta(c.TARJETA);
            }
            if (crbOtra1.isSelected()) {
                c.setCondicionVenta(c.OTRA);
            }
        } else {
            ok = false;
        }

        fallo = !crbConsumidorFinal1.isSelected()
                && !crbMonotributista1.isSelected()
                && !crbResponsableInscripto1.isSelected()
                && !crbExento1.isSelected();
        if (!fallo) {
            if (crbConsumidorFinal1.isSelected()) {
                c.setCondicionIva(c.CONSUMIDOR_FINAL);
            }
            if (crbMonotributista1.isSelected()) {
                c.setCondicionIva(c.MONOTRIBUTISTA);
            }
            if (crbResponsableInscripto1.isSelected()) {
                c.setCondicionIva(c.RESPONSABLE_INSCRIPTO);
            }
            if (crbExento1.isSelected()) {
                c.setCondicionIva(c.EXCENTO);
            }
        } else {
            ok = false;
        }
        
        if (ok) {
            clienteSeleccionado = true;
            if(c.getId_cliente()>0){
                c.actualizar();
            } else {
                if(dialogoNuevoClienteIdCliente.equals("-")){
                    c.guardarComoNueva();
                    dialogoNuevoClienteIdCliente.setText(String.valueOf(c.getIdUltimoCliente()-1));
                }
            }
            dialogoNuevoCliente.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(dialogoNuevoCliente, mnsjerror);
        }
    }//GEN-LAST:event_dialogoNuevoClienteBtnGuardarSeleccionarActionPerformed

    private void dialogoInicioSesionTxtPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dialogoInicioSesionTxtPasswordKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            dialogoInicioSesionBtnIniciarSesionActionPerformed(null);
        }
    }//GEN-LAST:event_dialogoInicioSesionTxtPasswordKeyPressed

    private void dialogoNuevoClienteModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogoNuevoClienteModificarActionPerformed
        habilitarContenido(dialogoNuevoCliente);
    }//GEN-LAST:event_dialogoNuevoClienteModificarActionPerformed

    private void ctxtRazonSocial1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ctxtRazonSocial1KeyReleased
        int kc = evt.getKeyCode();
        switch(kc){
            case KeyEvent.VK_ENTER:
                if(ctxtRazonSocial1.getSelectedText().length()>0){
                    String b = ctxtRazonSocial1.getText();
                    Clientes cliente = new Clientes();
                    if(cliente.obtenerSegunRazonSocialCompleto(b)){
                        mostrarDatosClienteEnFormulario(cliente);
                    }
                }
                break;
            case KeyEvent.VK_BACK_SPACE:
                break;
            default:
                String b = ctxtRazonSocial1.getText();
                Clientes cliente = new Clientes();
                String autoCompletar = (cliente.obtenerSegunRazonSocial(b));
                if(autoCompletar.length()>0){
                    autoCompletar = autoCompletar.substring(b.length(),autoCompletar.length());
                    ctxtRazonSocial1.setText(b+autoCompletar);
                    ctxtRazonSocial1.setSelectionStart(ctxtRazonSocial1.getText().indexOf(autoCompletar));
                    ctxtRazonSocial1.setSelectionEnd(ctxtRazonSocial1.getText().length());
                }
            break;
        }
    }//GEN-LAST:event_ctxtRazonSocial1KeyReleased

    private void chkSoloCuotasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSoloCuotasActionPerformed

    }//GEN-LAST:event_chkSoloCuotasActionPerformed

    private void chkSoloCuotasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkSoloCuotasItemStateChanged
        TableRowSorter sorter = new TableRowSorter<DefaultTableModel>(modeloTablaMovimientos);
        tablaMovimientos.setRowSorter(sorter);
        RowFilter<DefaultTableModel, Object> filter = null;
        if(chkSoloCuotas.isSelected()){
            try {
                filter = RowFilter.regexFilter("CUOTA");
            }catch (java.util.regex.PatternSyntaxException e) {
                return;
            }
        }
        sorter.setRowFilter(filter);
    }//GEN-LAST:event_chkSoloCuotasItemStateChanged

    private void btnFacturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFacturarActionPerformed
        if (bLista.getSelectedIndex() != -1 ) {
            if(noHayCuotasIntermedias()){
                DefaultTableModel model = new DefaultTableModel();
                Object[] columnas = new Object[5];
                columnas[0]="Id";
                columnas[2]="Mes";
                columnas[3]="Curso/Año";
                columnas[1]="Tipo Mov";
                columnas[4]="Saldo";

                Object[][] filas = new Object[tablaMovimientos.getSelectedRowCount()][5];
                int f = 0;
                int  [] filasSeleccionadas = tablaMovimientos.getSelectedRows();
                for (int i : filasSeleccionadas){
                    for(int j = 0; j < 5; j++){
                        filas[f][j] = modeloTablaMovimientos.getValueAt(i, j);
                    }
                    f++;
                }
                model.setDataVector(filas,columnas);

                String item = bLista.getSelectedValue().toString();
                String dni = item.substring(0, item.indexOf("-") - 1);
                Matriculas m = new Matriculas();
                m.buscarPorDni(dni);
                DialogoFacturar df = new DialogoFacturar(m,"factura",model);
                df.setTitle("Facturar");
                df.pack();
                df.setAlwaysOnTop(true);
                df.setModal(true);
                df.setLocationRelativeTo(jTabbedPane1);
                df.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(rootPane, "No se pueden seleccionar cuotas intermedias");
            }
        }
    }//GEN-LAST:event_btnFacturarActionPerformed

    private void tablaMovimientosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMovimientosMouseReleased
        /*if(tablaMovimientos.getSelectedRow()!=-1){
            if(tablaMovimientos.getSelectedRow()!=0){
                tablaMovimientos.getSelectionModel().clearSelection();
            }
        }*/
    }//GEN-LAST:event_tablaMovimientosMouseReleased

    private void tablaMovimientosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMovimientosMouseClicked
        /*if(tablaMovimientos.getSelectedRow()!=-1){
            if(tablaMovimientos.getSelectedRow()!=0){
                tablaMovimientos.getSelectionModel().clearSelection();
            }
        }*/
    }//GEN-LAST:event_tablaMovimientosMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(bLista.getSelectedIndex()>-1){
            String item = bLista.getSelectedValue().toString();
            String dni = item.substring(0, item.indexOf("-") - 1);
            Matriculas m = new Matriculas();
            m.buscarPorDni(dni);
            DialogoMovimientos dmov = new DialogoMovimientos(m.getMatricula());

            dmov.setTitle("Movimientos");
            dmov.pack();
            dmov.setAlwaysOnTop(true);
            dmov.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            dmov.setLocationRelativeTo(jTabbedPane1);
            //dmov.matricula = m.getMatricula();
            dmov.setVisible(true);
            bListaMouseClicked(null);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void matriculaAgregarMatACursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matriculaAgregarMatACursoActionPerformed
        if(bLista.getSelectedIndex()>-1){
            String item = bLista.getSelectedValue().toString();
            String dni = item.substring(0, item.indexOf("-") - 1);
            Matriculas m = new Matriculas();
            m.buscarPorDni(dni);
            DialogoAgregarMatACurso damac = new DialogoAgregarMatACurso();
            damac.setTitle("Agregar a Curso");
            damac.pack();
            damac.setAlwaysOnTop(true);
            damac.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            damac.setLocationRelativeTo(jTabbedPane1);
            damac.matricula = m.getMatricula();
            damac.setVisible(true);
            bListaMouseClicked(null);
        }
    }//GEN-LAST:event_matriculaAgregarMatACursoActionPerformed

    private void matriculaTablaCursosInscriptosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_matriculaTablaCursosInscriptosMouseClicked
        if(matriculaTablaCursosInscriptos.getSelectedRow()!=-1){
            try {
                DefaultTableModel modeloTablaCursosInscriptos = (DefaultTableModel) matriculaTablaCursosInscriptos.getModel();
                String value = modeloTablaCursosInscriptos.getValueAt(matriculaTablaCursosInscriptos.getSelectedRow(), 0).toString();
                String [] values = value.split("-");
                //("idAlumno"),("id_curso"),("id_horario")
                setearTablaMovimientos(values[0]);
            } catch (SQLException ex) {
                Logger.getLogger(ItteVentana.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_matriculaTablaCursosInscriptosMouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        matriculaLblMatricula.setText(String.valueOf(idSiguienteMatricula));

        matriculaCheckActivo.setSelected(false);
        matriculaTxtApellido.setText("");
        matriculaTxtNombre.setText("");
        matriculaTxtDni.setText("");

        //matriculaTxtFechaNacim.setText("");

        matriculaTxtDomicilio.setText("");
        matriculaTxtLocalidad.setText("");
        matriculaTxtZona.setText("");
        matriculaTxtProvincia.setText("");
        matriculaTxtTelefono.setText("");
        matriculaTxtCelular.setText("");
        matriculaTxtObservaciones.setText("");
        matriculaTxtComoLLego.setText("");
        matriculaTxtEmail.setText("");
        matriculaTxtCodigoPostal.setText("");

        seteosVarios();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void matriculasBtnGuardarNuevaMatriculaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matriculasBtnGuardarNuevaMatriculaActionPerformed
        Matriculas m = new Matriculas();
        Clientes c = new Clientes();
        boolean ok = true;
        String mnsjerror = "Hay campos sin completar.";
        String hayError = "";

        matriculaLblFechaAlta.setText(Funciones.getFechaActual());
        m.setFecha_alta(java.time.LocalDate.now().toString());

        m.setActivo(true);

        boolean fallo = matriculaTxtApellido.getText().equals("")
        || matriculaTxtNombre.getText().equals("")
        || matriculaTxtDni.getText().equals("");

        if (!fallo) {
            m.setApellido(matriculaTxtApellido.getText());
            m.setNombre(matriculaTxtNombre.getText());
            m.setDni(matriculaTxtDni.getText());
        } else {
            ok = false;
            hayError = mnsjerror;
        }

        if (!jXDatePicker2.getEditor().getText().equals("")) {
            m.setFecha_nacimiento(jXDatePicker2.getEditor().getText());
        } else {
            ok = false;
            hayError = mnsjerror;
        }

        fallo = matriculaTxtDomicilio.getText().equals("")
        || matriculaTxtLocalidad.getText().equals("")
        || matriculaTxtZona.getText().equals("")
        || matriculaTxtProvincia.getText().equals("");

        if (!fallo) {
            m.setDireccion(matriculaTxtDomicilio.getText());
            m.setLocalidad(matriculaTxtLocalidad.getText());
            m.setZona(matriculaTxtZona.getText());
            m.setProvincia(matriculaTxtProvincia.getText());
        } else {
            ok = false;
            hayError = mnsjerror;
        }

        fallo = matriculaTxtTelefono.getText().equals("")
        || matriculaTxtCelular.getText().equals("")
        || matriculaTxtObservaciones.getText().equals("")
        || matriculaTxtComoLLego.getText().equals("")
        || matriculaTxtEmail.getText().equals("")
        || matriculaTxtCodigoPostal.getText().equals("");

        if (!fallo) {
            m.setTelefono(matriculaTxtTelefono.getText());
            m.setCelular(matriculaTxtCelular.getText());
            m.setObservaciones(matriculaTxtObservaciones.getText());
            m.setComo_llego(matriculaTxtComoLLego.getText());
            m.setEmail(matriculaTxtEmail.getText());
            m.setCodigo_postal(matriculaTxtCodigoPostal.getText());
        } else {
            ok = false;
            hayError = mnsjerror;
        }

        m.setSexo(false);

        if (ok) {
            if(!clienteSeleccionado){
                c.setId_cliente(c.getIdUltimoCliente());
                c.setRazonSocial(String.format("%s, %s",matriculaTxtApellido.getText(),matriculaTxtNombre.getText()));
                c.setLocalidad(matriculaTxtLocalidad.getText());
                c.setActividad("");
                c.setDireccion(matriculaTxtDomicilio.getText());
                c.setEmail(matriculaTxtEmail.getText());
                c.setFechaAlta(java.time.LocalDate.now().toString());
                c.setProvincia(matriculaTxtProvincia.getText());
                c.setTelefono(matriculaTxtTelefono.getText());
                c.setCuit(matriculaTxtDni.getText());
                c.setCondicionIva(c.CONSUMIDOR_FINAL);
                c.setCondicionVenta(c.CONTADO);
                c.guardarComoNueva();
                m.setId_cliente(c.getId_cliente());
                m.guardarComoNueva();
            } else {
                m.setId_cliente(Integer.valueOf(dialogoNuevoClienteIdCliente.getText()));
                m.guardarComoNueva();
            }
            DialogoAgregarMatACurso damac = new DialogoAgregarMatACurso();
            damac.setTitle("Agregar a Curso");
            damac.pack();
            damac.setAlwaysOnTop(true);
            damac.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            damac.setLocationRelativeTo(jTabbedPane1);
            damac.matricula = m.getIdUltimaMatricula();
            damac.setVisible(true);
            bListaMouseClicked(null);

        }

    }//GEN-LAST:event_matriculasBtnGuardarNuevaMatriculaActionPerformed

    private void matriculasBtnIgualesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matriculasBtnIgualesActionPerformed
        /*ctxtRazonSocial.setText(matriculaTxtApellido.getText() + ", " + matriculaTxtNombre.getText());
        ctxtDireccion.setText(matriculaTxtDomicilio.getText());
        ctxtLocalidad.setText(matriculaTxtLocalidad.getText());
        ctxtProvincia.setText(matriculaTxtProvincia.getText());
        ctxtTelefono.setText(matriculaTxtTelefono.getText());
        ctxtEmail.setText(matriculaTxtEmail.getText());
        //ctxtFechaAlta.setText(matriculaLblFechaAlta.getText());
        ctxtCuit2.setText(matriculaTxtDni.getText());
        crbConsumidorFinal.setSelected(true);
        crbContado.setSelected(true);*/

        dialogoNuevoCliente.setTitle("Seleccionar Cliente");
        dialogoNuevoCliente.pack();
        dialogoNuevoCliente.setAlwaysOnTop(true);
        dialogoNuevoCliente.setModal(true);
        dialogoNuevoCliente.setLocationRelativeTo(jTabbedPane1);
        dialogoNuevoCliente.setVisible(true);
    }//GEN-LAST:event_matriculasBtnIgualesActionPerformed

    private void btxtBusquedaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btxtBusquedaKeyReleased
        String s = btxtBusqueda.getText();
        if(s.isEmpty())
            obtenerUltimas30Matriculas();
        else
            armarListaSegunBusqueda(s);
    }//GEN-LAST:event_btxtBusquedaKeyReleased

    private void btxtBusquedaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_btxtBusquedaPropertyChange
        if ((evt.getPropertyName().equals("Text")) && (btxtBusqueda.getText().isEmpty())) {
            obtenerUltimas30Matriculas();
        }
    }//GEN-LAST:event_btxtBusquedaPropertyChange

    private void btxtBusquedaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btxtBusquedaFocusGained
        btxtBusqueda.setText("");
    }//GEN-LAST:event_btxtBusquedaFocusGained

    private void bListaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bListaMouseClicked
        if (bLista.getSelectedIndex() != -1) {

            try {
                String item = bLista.getSelectedValue().toString();
                String dni = item.substring(0, item.indexOf("-") - 1);
                Matriculas m = new Matriculas();
                m.buscarPorDni(dni);

                matriculaLblMatricula.setText(String.valueOf(m.getMatricula()));

                matriculaLblFechaAlta.setText(Funciones.convertirFecha(m.getFecha_alta()));

                matriculaCheckActivo.setSelected(m.getActivo());
                matriculaTxtApellido.setText(m.getApellido());
                matriculaTxtNombre.setText(m.getNombre());
                matriculaTxtDni.setText(m.getDni());

                jXDatePicker2.getEditor().setText(m.getFecha_nacimiento());

                matriculaTxtDomicilio.setText(m.getDireccion());
                matriculaTxtLocalidad.setText(m.getLocalidad());
                String zona = m.getZona();
                matriculaTxtZona.setText(zona);
                matriculaTxtProvincia.setText(m.getProvincia());
                matriculaTxtTelefono.setText(m.getTelefono());
                matriculaTxtCelular.setText(m.getCelular());
                matriculaTxtObservaciones.setText(m.getObservaciones());
                matriculaTxtComoLLego.setText(m.getComo_llego());
                matriculaTxtEmail.setText(m.getEmail());
                matriculaTxtCodigoPostal.setText(m.getCodigo_postal());

                m.setCliente();
                mostrarDatosClienteEnFormulario(m.getCliente());
                deshabilitarContenido((Container)dialogoNuevoCliente);

                if(matriculaModeloTablaCurso.getRowCount()>0){
                    for(int i = matriculaModeloTablaCurso.getRowCount() - 1; i>=0; i--){
                        matriculaModeloTablaCurso.removeRow(i);
                    }
                }

                ResultSet res =  m.obtenerCursosInscriptos();
                while(res.next()){
                    Object [] args = new Object[7];
                    args[0] = String.format("%d-%d-%d",res.getInt("idAlumno"),res.getInt("id_curso"),res.getInt("id_horario"));
                    args[1] = res.getString("nombreCurso");
                    Horarios horarios = new Horarios();
                    horarios.setRs(res);
                    horarios.getDatos();
                    args[2] = horarios.obtenerHorarioSeteado();
                    args[3] = res.getString("fechaAltaCurso");
                    args[4] = String.valueOf(res.getInt("notaFinal"));
                    args[5] = "";
                    String estado = "";
                    if(res.getString("fechaBaja")==null){
                        estado = "En Curso";
                    } else {
                        estado = "Baja";
                    }
                    args[6] = estado;
                    matriculaModeloTablaCurso.addRow(args);
                }
                matriculaTablaCursosInscriptos.getColumnModel().getColumn(0).setMinWidth(0);
                matriculaTablaCursosInscriptos.getColumnModel().getColumn(0).setMaxWidth(0);

                setearTablaMovimientos(m);
            } catch (SQLException ex) {
                Logger.getLogger(ItteVentana.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_bListaMouseClicked

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
            java.util.logging.Logger.getLogger(ItteVentana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ItteVentana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ItteVentana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ItteVentana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ItteVentana().setVisible(true);
            }
        });
    }
// </editor-fold>
    
// <editor-fold defaultstate="collapsed" desc="Controles"> 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> bLista;
    private javax.swing.JButton btnFacturar;
    private javax.swing.JTextField btxtBusqueda;
    private javax.swing.JPanel busqueda;
    private javax.swing.JCheckBox chkSoloCuotas;
    private javax.swing.JPanel clientes1;
    private javax.swing.ButtonGroup condIva;
    private javax.swing.ButtonGroup condVenta;
    private javax.swing.JRadioButton crbConsumidorFinal1;
    private javax.swing.JRadioButton crbContado1;
    private javax.swing.JRadioButton crbExento1;
    private javax.swing.JRadioButton crbMonotributista1;
    private javax.swing.JRadioButton crbOtra1;
    private javax.swing.JRadioButton crbResponsableInscripto1;
    private javax.swing.JRadioButton crbTarjeta1;
    private javax.swing.JTextField ctxtActividad1;
    private javax.swing.JTextField ctxtCuit4;
    private javax.swing.JTextField ctxtCuit5;
    private javax.swing.JTextField ctxtCuit6;
    private javax.swing.JTextField ctxtDireccion1;
    private javax.swing.JTextField ctxtEmail1;
    private javax.swing.JTextField ctxtLocalidad1;
    private javax.swing.JTextField ctxtProvincia1;
    private javax.swing.JTextField ctxtRazonSocial1;
    private javax.swing.JTextField ctxtTelefono1;
    private javax.swing.JDialog dialogoBusqueda;
    private javax.swing.JButton dialogoBusquedaBtnBuscar;
    private javax.swing.JTextField dialogoBusquedaTxtParametro;
    private javax.swing.JDialog dialogoConfiguracionSistema;
    private javax.swing.JButton dialogoConfiguracionSistemaBtnCancelar;
    private javax.swing.JButton dialogoConfiguracionSistemaBtnGuardar;
    private javax.swing.JPasswordField dialogoConfiguracionSistemaTxtPassBD;
    private javax.swing.JTextField dialogoConfiguracionSistemaTxtServidor;
    private javax.swing.JTextField dialogoConfiguracionSistemaTxtUsuarioBD;
    private javax.swing.JDialog dialogoDatePicker;
    private javax.swing.JDialog dialogoInicioSesion;
    private javax.swing.JButton dialogoInicioSesionBtnConfig;
    private javax.swing.JButton dialogoInicioSesionBtnIniciarSesion;
    private javax.swing.JPasswordField dialogoInicioSesionTxtPassword;
    private javax.swing.JTextField dialogoInicioSesionTxtUsuario;
    private javax.swing.JDialog dialogoMasterPass;
    private javax.swing.JButton dialogoMasterPassBtnAceptar;
    private javax.swing.JPasswordField dialogoMasterPassTxtPass;
    private javax.swing.JDialog dialogoNuevoCliente;
    private javax.swing.JButton dialogoNuevoClienteBtnGuardarSeleccionar;
    private javax.swing.JLabel dialogoNuevoClienteIdCliente;
    private javax.swing.JButton dialogoNuevoClienteLimpiarFormulario;
    private javax.swing.JButton dialogoNuevoClienteModificar;
    private javax.swing.JDialog dialogoSelecColor;
    private javax.swing.JButton dialogoSelecColorBtnAceptar;
    private javax.swing.JButton dialogoSelecColorBtnCancelar;
    private javax.swing.JColorChooser dialogoSelecColorColores;
    private javax.swing.JLabel dialogoSelecColorLblTextField;
    private javax.swing.JButton itteVentanaBtnConfigSistema;
    private javax.swing.JLabel itteVentanaLblUsuario;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker6;
    private javax.swing.JLabel lblSaldoTotal;
    private javax.swing.JButton matriculaAgregarMatACurso;
    private javax.swing.JCheckBox matriculaCheckActivo;
    private javax.swing.JLabel matriculaLblFechaAlta;
    private javax.swing.JLabel matriculaLblMatricula;
    private javax.swing.JTable matriculaTablaCursosInscriptos;
    private javax.swing.JTextField matriculaTxtApellido;
    private javax.swing.JTextField matriculaTxtCelular;
    private javax.swing.JTextField matriculaTxtCodigoPostal;
    private javax.swing.JTextField matriculaTxtComoLLego;
    private javax.swing.JTextField matriculaTxtDni;
    private javax.swing.JTextField matriculaTxtDomicilio;
    private javax.swing.JTextField matriculaTxtEmail;
    private javax.swing.JTextField matriculaTxtLocalidad;
    private javax.swing.JTextField matriculaTxtNombre;
    private javax.swing.JTextField matriculaTxtObservaciones;
    private javax.swing.JTextField matriculaTxtProvincia;
    private javax.swing.JTextField matriculaTxtTelefono;
    private javax.swing.JTextField matriculaTxtZona;
    private javax.swing.JButton matriculasBtnGuardarNuevaMatricula;
    private javax.swing.JButton matriculasBtnIguales;
    private javax.swing.JPanel tabMatriculas;
    private javax.swing.JTable tablaMovimientos;
    // End of variables declaration//GEN-END:variables
// </editor-fold>

    
}
