/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package itte;

import itte.clases.modelos.Alumno;
import itte.clases.modelos.Certificado;
import itte.clases.modelos.Clientes;
import itte.clases.Configuraciones;
import itte.clases.modelos.Convenio;
import itte.clases.modelos.Cursos;
import itte.clases.Funciones;
import itte.clases.Log;
import itte.clases.modelos.Horarios;
import itte.clases.modelos.PlanPago;
import itte.clases.modelos.PlanPagoCuotas;
import itte.clases.validacion.OnlyNumberValidator;
import itte.clases.validacion.WantsValidationStatus;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import org.jdesktop.swingx.JXDatePicker;

/**
 *
 * @author Juan M S
 */
public class DialogoAgregarMatACurso extends javax.swing.JDialog implements WantsValidationStatus {

    public int matricula;
    public int idalumno;
    public int idCurso;
    public int idHorario;
    private int idConvenio;
    
    /**
     * Creates new form DialogoAgregarMatACurso
     */
    public DialogoAgregarMatACurso() {
        super();
        
        matricula = -1;
        idalumno = -1;
        
        initComponents();
        
        setearListeners();
        
        setearInicioDeClases();
        
        String sIdCurso = CursosagregarAlumnoCBCurso.getSelectedItem().toString();
        sIdCurso = sIdCurso.substring(0, sIdCurso.indexOf("-"));
        int idCurso = Integer.valueOf(sIdCurso);
        setearGananciaProfesorRecargosYCuotas(idCurso);
    }
    
    public void setearDatos(){
        Alumno alumno = new Alumno();
        alumno.obtenerSegunId(idalumno);
        Cursos curso = new Cursos();
        curso.obtenerSegunIdCurso(idCurso);
        if(alumno.sig() && curso.sig()){
            matricula = alumno.getMatricula();
            CursosagregarAlumnoCBCiclo.setSelectedItem(curso.getObservaciones());
            CursosagregarAlumnoCBCurso.setSelectedItem(String.format("%d-%s",curso.getId_curso(),curso.getNombre()));
            for(int i = 0; i<CursosagregarAlumnoCBHorario.getItemCount(); i++){
                String item = String.valueOf(CursosagregarAlumnoCBHorario.getItemAt(i));
                if(item.substring(0,item.indexOf("-")).equals(String.valueOf(idHorario))){
                    CursosagregarAlumnoCBHorario.setSelectedIndex(i);
                    break;
                }
            }
            for(int i = 0; i<CursosagregarAlumnoCBConvenio.getItemCount(); i++){
                String item = String.valueOf(CursosagregarAlumnoCBConvenio.getItemAt(i));
                if(item.substring(0,item.indexOf("-")).equals(String.valueOf(alumno.getId_convenio()))){
                    CursosagregarAlumnoCBConvenio.setSelectedIndex(i);
                    break;
                }
            }
            CursosagregarAlumnoTxtInscripcion.setText(String.valueOf(alumno.getArancelMatricula()));
            agregarAlumnoTxtCantCuotas.setText(String.valueOf(alumno.getCantidadCuotas()));
            agregarAlumnoTxtCuotasNormal.setText(String.valueOf(alumno.getArancelCursado()));
            CursosagregarAlumnoTxtGProfesor.setText(String.valueOf(curso.getG_profesor()));
            CursosagregarAlumnoTxtFechaAltaDia.setText(String.valueOf(alumno.getFecha_alta_curso()));
            CursosagregarAlumnoCBModalidad.setSelectedIndex(alumno.getModalidad());
            primeraCuotaFecha.setDate(Funciones.obtenerDate(alumno.getPrimera_cuota()));
            
            Certificado certificado = new Certificado();
            certificado.obtenerSegunIdAlumno(idalumno);
            
            int cantCuotas = 0;
            double monto = 0;
            boolean ultimosMeses = false;
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Funciones.obtenerDate(curso.getFecha_fin()));
            
            Calendar fechaMasAntigua = Calendar.getInstance();
            
            while(certificado.sig()){
                monto += certificado.getMonto();
                String mes = String.valueOf(certificado.getMes());
                String anio = mes.substring(mes.length()-4,mes.length());
                mes = mes.substring(0, mes.length()-3);//aca es -3 porque el segundo parametro del substring ya le resta 1 a la posicion
                if(cantCuotas==0){
                    fechaMasAntigua.set(Integer.valueOf(mes), Integer.valueOf(anio), 1);
                } else {
                    Calendar fechaTemp = Calendar.getInstance();
                    fechaTemp.set(Integer.valueOf(mes), Integer.valueOf(anio), 1);
                    if(fechaMasAntigua.after(fechaTemp))
                        fechaMasAntigua = fechaTemp;
                }
                ultimosMeses = certificado.getMes()==Integer.valueOf(String.format("%d%d", calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR)));
                cantCuotas++;
            }
            
            txtCostoCertificado.setText(String.valueOf(monto));
            jSpinner1.setValue(cantCuotas);
            if(ultimosMeses) {
                certificadoFecha2.setSelected(true);
            } else {
                certificadoFecha1.setSelected(true);
                certificadoFecha.setDate(fechaMasAntigua.getTime());
            }
        }
    }
    
    public void deshabilitarContenido(Container padre, boolean desOHab) {
        for (int i = padre.getComponentCount() - 1; i >= 0; i--) {
            final Component hijo = padre.getComponent(i);
            if(hijo != null){
                if (hijo instanceof JTextField || hijo instanceof JButton || 
                        hijo instanceof JComboBox || hijo instanceof JSpinner || 
                        hijo instanceof JRadioButton || hijo instanceof JXDatePicker) {
                    hijo.setEnabled(desOHab); 
                } else {
                    if(hijo instanceof Container){
                        deshabilitarContenido((Container) hijo,desOHab);
                    }

                }
            }
        }
    }
    
    private void setearListeners(){
        CursosagregarAlumnoCBCiclo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                setearDatosCursos();
            }
        });
        
        CursosagregarAlumnoCBCurso.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                setearDatosHorarios();
            }
        });
        
        CursosagregarAlumnoCBConvenio.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                String sIdConvenio = CursosagregarAlumnoCBConvenio.getSelectedItem().toString();
                sIdConvenio = sIdConvenio.substring(0,sIdConvenio.indexOf("-"));
                idConvenio = Integer.valueOf(sIdConvenio);
            }
        });
    }
    
    private void setearDatosCursos(){
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
        Cursos cursos = new Cursos();
        cursos.obtenerCursosSegunAnio(CursosagregarAlumnoCBCiclo.getSelectedItem().toString());
        boolean primerElemento = true;
        while(cursos.sig()){
            String s = String.format("%d-%s",cursos.getId_curso(),cursos.getNombre());
            model.addElement(s);
            if(primerElemento){
                CursosagregarAlumnoTxtGProfesor.setText(String.valueOf(cursos.getG_profesor()));
                primerElemento = false;
            }
        }
        if(primerElemento){
            CursosagregarAlumnoTxtGProfesor.setText("");
        }
        
        CursosagregarAlumnoCBCurso.setModel(model);
        setearDatosHorarios();
    }
    
    private void setearDatosHorarios(){
        try {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            if(CursosagregarAlumnoCBCurso.getItemCount()>0){
                String sIdCurso = CursosagregarAlumnoCBCurso.getSelectedItem().toString();
                sIdCurso = sIdCurso.substring(0, sIdCurso.indexOf("-"));
                int idCurso = Integer.valueOf(sIdCurso);
                Horarios horarios = new Horarios();
                horarios.obtenerSegunIdCurso(idCurso);
                setearGananciaProfesorRecargosYCuotas(idCurso);
                do {
                    model.addElement(horarios.obtenerHorarioSeteado());
                } while (horarios.sig());
            }
            CursosagregarAlumnoCBHorario.setModel(model);
            setearInicioDeClases();
        } catch (SQLException ex) {
            Logger.getLogger(DialogoAgregarMatACurso.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setearGananciaProfesorRecargosYCuotas(int idCurso) {
        try {
            Cursos curso = new Cursos();
            curso.obtenerCursoSegunIdCurso(idCurso);
            
            double cuotaSugerida = curso.getCuota_sugerida();
            
            setearRecargos(cuotaSugerida);
            
            CursosagregarAlumnoTxtGProfesor.setText(String.format("%.2f",curso.getG_profesor()));
            
            agregarAlumnoTxtCantCuotas.setText(String.valueOf(curso.getCantidad_cuotas()));
            
            CursosagregarAlumnoTxtInscripcion.setText(String.format("%.2f", curso.getCosto_inscripcion()));
        } catch (SQLException ex) {
            Logger.getLogger(DialogoAgregarMatACurso.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void setearRecargos(double cuotaSugerida){
        Configuraciones config = new Configuraciones();
        CursosagregarAlumnoLblAntesDel5.setText(String.format("%.2f",cuotaSugerida-config.getDescuentoAntes6()));
        CursosagregarAlumnoLblDel6Al10.setText(String.format("%.2f",cuotaSugerida));
        CursosagregarAlumnoLblDel11Al20.setText(String.format("%.2f",cuotaSugerida+config.getRecargo11A20()));
        CursosagregarAlumnoLbl21aFinDeMes.setText(String.format("%.2f",cuotaSugerida+config.getRecargo21A30()));
        CursosagregarAlumnoLblPasadoElMes.setText(String.format("%.2f",cuotaSugerida+config.getRecargoPasado30()));

        agregarAlumnoTxtCuotasNormal.setText(String.format("%.2f",cuotaSugerida));
    }
    
    private void setearInicioDeClases(){
        try {
            String inicioClases = "//";
            if(CursosagregarAlumnoCBCurso.getItemCount()>0){
                String idCurso = CursosagregarAlumnoCBCurso.getSelectedItem().toString();
                idCurso = idCurso.substring(0, idCurso.indexOf("-"));
                Cursos curso = new Cursos();
                curso.obtenerCursoSegunIdCurso(Integer.valueOf(idCurso));
                inicioClases = curso.getFecha_inicio();
            }
            CursosagregarAlumnoLblInicioDeClases.setText(inicioClases);
        } catch (SQLException ex) {
            Logger.getLogger(DialogoAgregarMatACurso.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setearConvenios(){
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            Convenio convenios = new Convenio();
            convenios.obtenerTodos();
            while(convenios.sig()) {
                model.addElement(String.format("%d-%s", convenios.getId_convenio(),convenios.getDetalle()));
            }
            
            CursosagregarAlumnoCBConvenio.setModel(model);
    }
    
    private void setearDatePicker(JXDatePicker jxdp){
        jxdp.setDate(new java.sql.Date((Calendar.getInstance()).getTimeInMillis()));

        jxdp.setFormats(new SimpleDateFormat("yyyy-MM-dd"));

        jxdp.setTimeZone(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
    }

    @Override
    public void validatePassed() {
        CursosagregarAlumnoBtnAceptar.setEnabled(true);
    }

    @Override
    public void validateFailed() {
        CursosagregarAlumnoBtnAceptar.setEnabled(false);
    }
    
    private boolean validarRadiosYFechas() {
            if(!certificadoFecha1.isSelected() && !certificadoFecha2.isSelected())
                return false;
            
            if(primeraCuotaFecha.getEditor().getText().equals(""))
                return false;
            
            if(certificadoFecha1.isSelected() && certificadoFecha.getEditor().getText().equals(""))
                return false;
            
            return true;
    }
    
    private int obtenerIdHorarioSeleccionado(){
        String sIdHorarioSeleccionado = CursosagregarAlumnoCBHorario.getSelectedItem().toString();
        return Integer.valueOf(sIdHorarioSeleccionado.substring(0,sIdHorarioSeleccionado.indexOf("-")));
    }
    
    private int obtenerIdConvenioSeleccionado(){
        String sIdConvenioSeleccionado = CursosagregarAlumnoCBConvenio.getSelectedItem().toString();
        return Integer.valueOf(sIdConvenioSeleccionado.substring(0,sIdConvenioSeleccionado.indexOf("-")));
    }
    
    private int obtenerIdCursoSeleccionado(){
        String sIdCursoSeleccionado = CursosagregarAlumnoCBCurso.getSelectedItem().toString();
        return Integer.valueOf(sIdCursoSeleccionado.substring(0,sIdCursoSeleccionado.indexOf("-")));
    }
    
    private boolean generarPlanPagoCertificados(int idalumno) {
        Certificado planCertificado = new Certificado();
        boolean ok = true;
        Calendar calendar = Calendar.getInstance();
        int cantCuotas = Integer.valueOf(jSpinner1.getValue().toString());
        double costoCertificado = Funciones.getDouble(txtCostoCertificado.getText());
        int [] mesesPago = new int[cantCuotas];
        if(certificadoFecha1.isSelected()){
            calendar.setTime(certificadoFecha.getDate());
            for(int i=0; i<cantCuotas; i++){                
                mesesPago[i]=Integer.valueOf(String.format("%d%d",calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR)));
                calendar.add(Calendar.MONTH, 1);
            }
        } else {
            try {
                Cursos curso = new Cursos();
                curso.obtenerCursoSegunIdCurso(obtenerIdCursoSeleccionado());
                calendar.setTime(Funciones.obtenerDate(curso.getFecha_fin()));
                for(int i=0; i<cantCuotas; i++){                
                    mesesPago[i]=Integer.valueOf(String.format("%d%d",calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR)));
                    calendar.add(Calendar.MONTH, -1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(DialogoAgregarMatACurso.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        planCertificado.setId_alumno(idalumno);
        planCertificado.setMonto(costoCertificado/cantCuotas);
        for(int i = 0; i<cantCuotas; i++){
            planCertificado.setMes(mesesPago[i]);
            ok = ok && planCertificado.guardarComoNueva();
        }
        return ok;
    }
    
    private boolean generarPlanPagoCuotas(int idalumno) throws IOException, SQLException{
        boolean ok = true;
        Alumno alum = new Alumno();
        alum.obtenerSegunId(idalumno);
        alum.sig();
        alum.getDatos();
        PlanPago pp = new PlanPago();
        pp.setIdAlumno(idalumno);
        pp.setCantidadCuotas(alum.getCantidadCuotas());
        pp.setValorCuota(alum.getArancelCursado());
        if(pp.guardarComoNueva()==false){
            ok = false;
        }
        
        PlanPagoCuotas ppc = new PlanPagoCuotas();
        ppc.setIdplanpago(pp.getUltimoId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Funciones.obtenerDate(alum.getPrimera_cuota()));
        for(int i = 0; i<pp.getCantidadCuotas(); i++){
            ppc.setCuota(i+1);
            ppc.setFechapago(calendar.getTime());
            calendar.add(Calendar.MONTH, 1);
            if(ppc.guardarComoNueva()==false){
                ok = false;
            }
        }
        return ok;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel15 = new javax.swing.JPanel();
        jDialog1 = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        nuevoConvenioDescripcion = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        nuevoConvenioMatriculaAnual = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        nuevoConvenioCursado = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        nuevoConvenioCantidadDeCuotas = new javax.swing.JTextField();
        nuevoConvenioBtnVolver = new javax.swing.JButton();
        nuevoConvenioBtnGuardar = new javax.swing.JButton();
        nuevoConvenioBtnModificar = new javax.swing.JButton();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel6 = new javax.swing.JPanel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        CursosagregarAlumnoCBCiclo = new javax.swing.JComboBox<>();
        jLabel76 = new javax.swing.JLabel();
        CursosagregarAlumnoCBCurso = new javax.swing.JComboBox<>();
        jLabel77 = new javax.swing.JLabel();
        CursosagregarAlumnoCBHorario = new javax.swing.JComboBox<>();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        CursosagregarAlumnoCBConvenio = new javax.swing.JComboBox<>();
        CursosagregarAlumnoBtnMas = new javax.swing.JButton();
        CursosagregarAlumnoBtnEditar = new javax.swing.JButton();
        jLabel81 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        agregarAlumnoTxtCuotasNormal = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        agregarAlumnoTxtCantCuotas = new javax.swing.JTextField();
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        CursosagregarAlumnoTxtInscripcion = new javax.swing.JTextField();
        jLabel87 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        CursosagregarAlumnoLblAntesDel5 = new javax.swing.JLabel();
        CursosagregarAlumnoLblDel6Al10 = new javax.swing.JLabel();
        CursosagregarAlumnoLblDel11Al20 = new javax.swing.JLabel();
        CursosagregarAlumnoLbl21aFinDeMes = new javax.swing.JLabel();
        CursosagregarAlumnoLblPasadoElMes = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtCostoCertificado = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        certificadoFecha1 = new javax.swing.JRadioButton();
        certificadoFecha = new org.jdesktop.swingx.JXDatePicker();
        certificadoFecha2 = new javax.swing.JRadioButton();
        CursosagregarAlumnoTxtGProfesor = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        CursosagregarAlumnoCBInscripcionesMultiples = new javax.swing.JComboBox();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        CursosagregarAlumnoTxtFechaAltaDia = new javax.swing.JTextField();
        jLabel98 = new javax.swing.JLabel();
        CursosagregarAlumnoCBModalidad = new javax.swing.JComboBox<>();
        jLabel99 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        CursosagregarAlumnoBtnAceptar = new javax.swing.JButton();
        CursosagregarAlumnoBtnCancelar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        CursosagregarAlumnoLblInicioDeClases = new javax.swing.JLabel();
        primeraCuotaFecha = new org.jdesktop.swingx.JXDatePicker();

        jPanel15.setBackground(new java.awt.Color(204, 255, 255));

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 56, Short.MAX_VALUE)
        );

        jLabel1.setBackground(java.awt.Color.blue);
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(java.awt.Color.white);
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Agregar Nuevo Convenio");
        jLabel1.setOpaque(true);

        jLabel2.setText("Descripción:");

        jLabel3.setText("Matrícula Anual:");

        jLabel4.setText("Cursado:");

        jLabel5.setText("Cantidad de Cuotas:");

        nuevoConvenioBtnVolver.setText("Volver");
        nuevoConvenioBtnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoConvenioBtnVolverActionPerformed(evt);
            }
        });

        nuevoConvenioBtnGuardar.setText("Guardar Nuevo");
        nuevoConvenioBtnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoConvenioBtnGuardarActionPerformed(evt);
            }
        });

        nuevoConvenioBtnModificar.setText("Modificar");
        nuevoConvenioBtnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoConvenioBtnModificarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(nuevoConvenioBtnGuardar))
                .addGap(18, 18, 18)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nuevoConvenioCantidadDeCuotas, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                            .addComponent(nuevoConvenioCursado)
                            .addComponent(nuevoConvenioMatriculaAnual)
                            .addComponent(nuevoConvenioDescripcion))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addComponent(nuevoConvenioBtnModificar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nuevoConvenioBtnVolver)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(nuevoConvenioDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(nuevoConvenioMatriculaAnual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(nuevoConvenioCursado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(nuevoConvenioCantidadDeCuotas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevoConvenioBtnVolver)
                    .addComponent(nuevoConvenioBtnGuardar)
                    .addComponent(nuevoConvenioBtnModificar))
                .addContainerGap())
        );

        buttonGroup1.add(certificadoFecha1);
        buttonGroup1.add(certificadoFecha2);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel6.setBackground(new java.awt.Color(102, 153, 255));

        jLabel71.setBackground(new java.awt.Color(0, 102, 255));
        jLabel71.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel71.setForeground(new java.awt.Color(255, 255, 255));
        jLabel71.setText("Curso / Carrera que realiza");
        jLabel71.setOpaque(true);

        jLabel72.setText(String.format("ciclo = %s",java.time.Year.now().toString()));

        jLabel75.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel75.setText("Ciclo");

        CursosagregarAlumnoCBCiclo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        CursosagregarAlumnoCBCiclo.setForeground(new java.awt.Color(0, 102, 255));
        CursosagregarAlumnoCBCiclo.setModel(new javax.swing.DefaultComboBoxModel(new String[] {  String.valueOf(java.time.Year.now().getValue()), String.valueOf(java.time.Year.now().getValue()-1),String.valueOf(java.time.Year.now().getValue()+1)}));
        CursosagregarAlumnoCBCiclo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CursosagregarAlumnoCBCicloActionPerformed(evt);
            }
        });

        jLabel76.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel76.setText("Curso");

        CursosagregarAlumnoCBCurso.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        CursosagregarAlumnoCBCurso.setForeground(new java.awt.Color(0, 102, 255));
        setearDatosCursos();

        jLabel77.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel77.setText("Horario");

        CursosagregarAlumnoCBHorario.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        CursosagregarAlumnoCBHorario.setForeground(new java.awt.Color(0, 102, 255));

        jLabel78.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel78.setText("Inicio de Clases");

        jLabel79.setBackground(new java.awt.Color(0, 102, 255));
        jLabel79.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel79.setForeground(new java.awt.Color(255, 255, 255));
        jLabel79.setText("Arancel");
        jLabel79.setOpaque(true);

        jLabel80.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel80.setText("Convenio");

        CursosagregarAlumnoCBConvenio.setForeground(new java.awt.Color(0, 102, 255));
        setearConvenios();
        CursosagregarAlumnoCBConvenio.setSelectedItem("9-Normal");

        CursosagregarAlumnoBtnMas.setText("+");
        CursosagregarAlumnoBtnMas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CursosagregarAlumnoBtnMasActionPerformed(evt);
            }
        });

        CursosagregarAlumnoBtnEditar.setText("Editar");

        jLabel81.setBackground(new java.awt.Color(204, 255, 255));
        jLabel81.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel81.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel81.setText("ARANCELES RESULTANTES");
        jLabel81.setOpaque(true);

        jPanel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel17.setToolTipText("");

        agregarAlumnoTxtCuotasNormal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        agregarAlumnoTxtCuotasNormal.setForeground(new java.awt.Color(0, 102, 255));
        agregarAlumnoTxtCuotasNormal.setInputVerifier(new OnlyNumberValidator(this, txtCostoCertificado, ""));
        agregarAlumnoTxtCuotasNormal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                agregarAlumnoTxtCuotasNormalFocusLost(evt);
            }
        });

        jLabel84.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel84.setText("Cuota Normal");

        jLabel85.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel85.setText("$");

        jLabel86.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel86.setText("Cuotas.");

        agregarAlumnoTxtCantCuotas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        agregarAlumnoTxtCantCuotas.setForeground(new java.awt.Color(0, 102, 255));
        agregarAlumnoTxtCantCuotas.setText("6");
        agregarAlumnoTxtCantCuotas.setInputVerifier(new OnlyNumberValidator(this, txtCostoCertificado, ""));

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel86, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(agregarAlumnoTxtCantCuotas))
                .addGap(41, 41, 41)
                .addComponent(jLabel85)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel84)
                    .addComponent(agregarAlumnoTxtCuotasNormal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(agregarAlumnoTxtCantCuotas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel84)
                            .addComponent(jLabel86))
                        .addGap(1, 1, 1)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel85)
                            .addComponent(agregarAlumnoTxtCuotasNormal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(37, 37, 37))
        );

        jLabel82.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel82.setText("Inscripción");

        jLabel83.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel83.setText("$");

        CursosagregarAlumnoTxtInscripcion.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        CursosagregarAlumnoTxtInscripcion.setForeground(new java.awt.Color(0, 102, 255));
        CursosagregarAlumnoTxtInscripcion.setText("0.00");
        CursosagregarAlumnoTxtInscripcion.setInputVerifier(new OnlyNumberValidator(this, txtCostoCertificado, ""));

        jLabel87.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel87.setText("Antes del 5:");

        jLabel88.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel88.setText("del 6 al 10:");

        jLabel89.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel89.setText("11 al 20:");

        jLabel90.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel90.setText("21 a fin de mes:");

        jLabel91.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel91.setText("Pasado mes:");

        CursosagregarAlumnoLblAntesDel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        CursosagregarAlumnoLblAntesDel5.setText("$100");

        CursosagregarAlumnoLblDel6Al10.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        CursosagregarAlumnoLblDel6Al10.setText("$200");

        CursosagregarAlumnoLblDel11Al20.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        CursosagregarAlumnoLblDel11Al20.setText("$300");

        CursosagregarAlumnoLbl21aFinDeMes.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        CursosagregarAlumnoLbl21aFinDeMes.setText("$400");

        CursosagregarAlumnoLblPasadoElMes.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        CursosagregarAlumnoLblPasadoElMes.setText("$500");

        jLabel6.setText("Costo Certif");

        txtCostoCertificado.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtCostoCertificado.setForeground(new java.awt.Color(0, 102, 255));
        txtCostoCertificado.setText("300");
        txtCostoCertificado.setInputVerifier(new OnlyNumberValidator(this, txtCostoCertificado, ""));

        jLabel7.setText("Cant pagos Certif:");

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(1, 1, 3, 1));

        certificadoFecha1.setText("Mes y Año Primera Cuota Certif");
        certificadoFecha1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                certificadoFecha1StateChanged(evt);
            }
        });

        setearDatePicker(certificadoFecha);
        certificadoFecha.setEnabled(false);

        certificadoFecha2.setText("Ultimo/s mes/es de cursado");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel82))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel83)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CursosagregarAlumnoTxtInscripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel87)
                            .addComponent(jLabel88)
                            .addComponent(jLabel89))
                        .addGap(35, 35, 35)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CursosagregarAlumnoLblDel11Al20)
                            .addComponent(CursosagregarAlumnoLblDel6Al10)
                            .addComponent(CursosagregarAlumnoLblAntesDel5)))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel90)
                            .addComponent(jLabel91))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CursosagregarAlumnoLblPasadoElMes)
                            .addComponent(CursosagregarAlumnoLbl21aFinDeMes))))
                .addGap(34, 34, 34))
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtCostoCertificado, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(certificadoFecha1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(certificadoFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(certificadoFecha2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel87)
                            .addComponent(CursosagregarAlumnoLblAntesDel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel88)
                            .addComponent(CursosagregarAlumnoLblDel6Al10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel89)
                            .addComponent(CursosagregarAlumnoLblDel11Al20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel90)
                            .addComponent(CursosagregarAlumnoLbl21aFinDeMes))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel91)
                            .addComponent(CursosagregarAlumnoLblPasadoElMes)))
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel82)
                        .addGap(1, 1, 1)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel83)
                            .addComponent(CursosagregarAlumnoTxtInscripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(36, 36, 36)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtCostoCertificado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(certificadoFecha1)
                    .addComponent(certificadoFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(certificadoFecha2))
                .addContainerGap())
        );

        CursosagregarAlumnoTxtGProfesor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        CursosagregarAlumnoTxtGProfesor.setForeground(new java.awt.Color(0, 102, 255));

        jLabel92.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel92.setText("G. Profesor:");

        jLabel93.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel93.setText("Inscripciones Múltiples");

        CursosagregarAlumnoCBInscripcionesMultiples.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel94.setBackground(new java.awt.Color(0, 102, 255));
        jLabel94.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel94.setForeground(new java.awt.Color(255, 255, 255));
        jLabel94.setText("Datos de Alta del alumno a este curso/carrera");
        jLabel94.setOpaque(true);

        jLabel95.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel95.setText("Fecha Alta al curso");

        CursosagregarAlumnoTxtFechaAltaDia.setForeground(new java.awt.Color(0, 102, 255));
        CursosagregarAlumnoTxtFechaAltaDia.setText(java.time.LocalDate.now().toString());
        CursosagregarAlumnoTxtFechaAltaDia.setEnabled(false);

        jLabel98.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel98.setText("Modalidad");

        CursosagregarAlumnoCBModalidad.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Presencial", "A Distancia" }));

        jLabel99.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel99.setText("Mes Primera Cuota");

        jPanel18.setBackground(new java.awt.Color(204, 255, 255));

        CursosagregarAlumnoBtnAceptar.setBackground(new java.awt.Color(51, 255, 51));
        CursosagregarAlumnoBtnAceptar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        CursosagregarAlumnoBtnAceptar.setText("ACEPTAR");
        CursosagregarAlumnoBtnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CursosagregarAlumnoBtnAceptarActionPerformed(evt);
            }
        });

        CursosagregarAlumnoBtnCancelar.setText("Cancelar");

        btnModificar.setText("Modificar");
        btnModificar.setEnabled(false);
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(250, 250, 250)
                .addComponent(CursosagregarAlumnoBtnAceptar)
                .addGap(57, 57, 57)
                .addComponent(btnModificar)
                .addGap(70, 70, 70)
                .addComponent(CursosagregarAlumnoBtnCancelar)
                .addGap(250, 250, 250))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CursosagregarAlumnoBtnAceptar)
                    .addComponent(CursosagregarAlumnoBtnCancelar)
                    .addComponent(btnModificar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        CursosagregarAlumnoLblInicioDeClases.setText("jLabel1");

        setearDatePicker(primeraCuotaFecha);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel94, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel79, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel72)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addGap(28, 28, 28)
                            .addComponent(jLabel80)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(CursosagregarAlumnoCBConvenio, javax.swing.GroupLayout.PREFERRED_SIZE, 737, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(10, 10, 10)
                            .addComponent(CursosagregarAlumnoBtnMas)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(CursosagregarAlumnoBtnEditar))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel81, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(CursosagregarAlumnoTxtGProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel92)
                                .addComponent(jLabel93)
                                .addComponent(CursosagregarAlumnoCBInscripcionesMultiples, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel95)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(CursosagregarAlumnoTxtFechaAltaDia, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(34, 34, 34)
                            .addComponent(jLabel98)
                            .addGap(18, 18, 18)
                            .addComponent(CursosagregarAlumnoCBModalidad, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(45, 45, 45)
                            .addComponent(jLabel99)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(primeraCuotaFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addGap(62, 62, 62)
                            .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 817, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel71, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel75)
                    .addComponent(CursosagregarAlumnoCBCiclo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel76)
                    .addComponent(CursosagregarAlumnoCBCurso, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel77)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(CursosagregarAlumnoCBHorario, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel78)
                    .addComponent(CursosagregarAlumnoLblInicioDeClases))
                .addGap(75, 75, 75))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel71)
                .addGap(0, 0, 0)
                .addComponent(jLabel72)
                .addGap(0, 0, 0)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel77)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(CursosagregarAlumnoCBHorario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CursosagregarAlumnoLblInicioDeClases)))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(jLabel76)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(CursosagregarAlumnoCBCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(jLabel75)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(CursosagregarAlumnoCBCiclo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(30, 30, 30)
                        .addComponent(jLabel79)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel80)
                            .addComponent(CursosagregarAlumnoCBConvenio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CursosagregarAlumnoBtnEditar)
                            .addComponent(CursosagregarAlumnoBtnMas))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel81)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(jLabel92)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CursosagregarAlumnoTxtGProfesor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel93)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CursosagregarAlumnoCBInscripcionesMultiples, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel94)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel95)
                            .addComponent(CursosagregarAlumnoTxtFechaAltaDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel98)
                            .addComponent(CursosagregarAlumnoCBModalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel99)
                            .addComponent(primeraCuotaFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel78))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CursosagregarAlumnoCBCicloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CursosagregarAlumnoCBCicloActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CursosagregarAlumnoCBCicloActionPerformed

    private void nuevoConvenioBtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoConvenioBtnGuardarActionPerformed
        Convenio convenio = new Convenio();
        convenio.setDetalle(nuevoConvenioDescripcion.getText());
        convenio.setArancelMatricula(Funciones.getDouble(nuevoConvenioMatriculaAnual.getText()));
        convenio.setCantidadCuotas(Integer.valueOf(nuevoConvenioCantidadDeCuotas.getText()));
        convenio.setArancelCursado(Funciones.getDouble(nuevoConvenioCursado.getText()));
        convenio.guardarComoNueva();
        setearConvenios();
        jDialog1.setVisible(false);
    }//GEN-LAST:event_nuevoConvenioBtnGuardarActionPerformed

    private void nuevoConvenioBtnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoConvenioBtnVolverActionPerformed
        jDialog1.setVisible(false);
    }//GEN-LAST:event_nuevoConvenioBtnVolverActionPerformed

    private void CursosagregarAlumnoBtnMasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CursosagregarAlumnoBtnMasActionPerformed
            jDialog1.pack();
            jDialog1.setAlwaysOnTop(true);
            jDialog1.setModal(true);
            jDialog1.setLocationRelativeTo(jPanel6);
            jDialog1.setVisible(true);
    }//GEN-LAST:event_CursosagregarAlumnoBtnMasActionPerformed

    private void nuevoConvenioBtnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoConvenioBtnModificarActionPerformed
        Convenio convenio = new Convenio();
        convenio.setId_convenio(idConvenio);
        convenio.setDetalle(nuevoConvenioDescripcion.getText());
        convenio.setArancelMatricula(Funciones.getDouble(nuevoConvenioMatriculaAnual.getText()));
        convenio.setCantidadCuotas(Integer.valueOf(nuevoConvenioCantidadDeCuotas.getText()));
        convenio.setArancelCursado(Funciones.getDouble(nuevoConvenioCursado.getText()));
        convenio.actualizar();
        setearConvenios();
        jDialog1.setVisible(false);
    }//GEN-LAST:event_nuevoConvenioBtnModificarActionPerformed

    private void certificadoFecha1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_certificadoFecha1StateChanged
        certificadoFecha.setEnabled(certificadoFecha1.isSelected());
    }//GEN-LAST:event_certificadoFecha1StateChanged

    private void agregarAlumnoTxtCuotasNormalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_agregarAlumnoTxtCuotasNormalFocusLost
        String v = agregarAlumnoTxtCuotasNormal.getText();
        if(v.contains(","))
            v = v.replace(",", ".");
        setearRecargos(Funciones.getDouble(v));
    }//GEN-LAST:event_agregarAlumnoTxtCuotasNormalFocusLost

    private void CursosagregarAlumnoBtnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CursosagregarAlumnoBtnAceptarActionPerformed
        if(validarRadiosYFechas()){
            Alumno alumno = new Alumno();
            try{ //carga datos del alumno, independientemente si es nuevo o viejo
                alumno.setMatricula(matricula);
                alumno.setId_horario(obtenerIdHorarioSeleccionado());
                alumno.setFecha_alta_curso(CursosagregarAlumnoTxtFechaAltaDia.getText());
                alumno.setFecha_recepcion("0000-00-00");
                alumno.setFecha_baja("0000-00-00");
                alumno.setMotivo_baja("");
                alumno.setNota_final(-1);
                alumno.setModalidad(CursosagregarAlumnoCBModalidad.getSelectedIndex());
                alumno.setId_convenio(obtenerIdConvenioSeleccionado());
                alumno.setArancelMatricula(Funciones.getDouble(CursosagregarAlumnoTxtInscripcion.getText()));
                alumno.setArancelCursado(Funciones.getDouble(agregarAlumnoTxtCuotasNormal.getText()));
                alumno.setCantidadCuotas(Integer.valueOf(agregarAlumnoTxtCantCuotas.getText()));
                alumno.setGanancia_profesor(Funciones.getDouble(CursosagregarAlumnoTxtGProfesor.getText()));
                alumno.setMesUltimaClase(0);
                alumno.setIdInscripcionMultiple(1);
                alumno.setTipoHabilitacion(0);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(primeraCuotaFecha.getDate());
                alumno.setPrimera_cuota(String.format("%d-%d-1", calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1));
            } catch(NumberFormatException ex){
                Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(idalumno>-1){ // si es mayor a -1, es viejo
                (new Certificado()).borrarSegunIdAlumno(idalumno); // borra datos del certificado para el alumno 
                alumno.setId_alumno(idalumno); // setea id porque es alumno ya existente
                if(alumno.actualizar()==false) {
                    JOptionPane.showMessageDialog(this, "Error al actualizar");
                } else {
                    if(generarPlanPagoCertificados(idalumno)) { //se actualiza certificado
                        JOptionPane.showMessageDialog(this, "Se actualizaron datos del alumno");
                        this.setVisible(false);
                    }
                }
            } else {
                if(alumno.guardarComoNueva()==false) //se crea nuevo alumno
                    JOptionPane.showMessageDialog(this, "Error al guardar");
                else {
                    try {
                        int idAlumno = alumno.obtenerIdUltimoAlumno();
                        if(generarPlanPagoCertificados(idAlumno) && generarPlanPagoCuotas(idAlumno)) {
                            
                            JOptionPane.showMessageDialog(this, "Se crearon datos del alumno");
                            this.setVisible(false);
                        }
                    } catch (IOException | SQLException ex) {
                        Logger.getLogger(DialogoAgregarMatACurso.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Hay campos que no se han completado");
        }        
    }//GEN-LAST:event_CursosagregarAlumnoBtnAceptarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        deshabilitarContenido(this,true);
        btnModificar.setEnabled(false);
    }//GEN-LAST:event_btnModificarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CursosagregarAlumnoBtnAceptar;
    private javax.swing.JButton CursosagregarAlumnoBtnCancelar;
    private javax.swing.JButton CursosagregarAlumnoBtnEditar;
    private javax.swing.JButton CursosagregarAlumnoBtnMas;
    private javax.swing.JComboBox<String> CursosagregarAlumnoCBCiclo;
    private javax.swing.JComboBox<String> CursosagregarAlumnoCBConvenio;
    private javax.swing.JComboBox<String> CursosagregarAlumnoCBCurso;
    private javax.swing.JComboBox<String> CursosagregarAlumnoCBHorario;
    private javax.swing.JComboBox CursosagregarAlumnoCBInscripcionesMultiples;
    private javax.swing.JComboBox<String> CursosagregarAlumnoCBModalidad;
    private javax.swing.JLabel CursosagregarAlumnoLbl21aFinDeMes;
    private javax.swing.JLabel CursosagregarAlumnoLblAntesDel5;
    private javax.swing.JLabel CursosagregarAlumnoLblDel11Al20;
    private javax.swing.JLabel CursosagregarAlumnoLblDel6Al10;
    private javax.swing.JLabel CursosagregarAlumnoLblInicioDeClases;
    private javax.swing.JLabel CursosagregarAlumnoLblPasadoElMes;
    private javax.swing.JTextField CursosagregarAlumnoTxtFechaAltaDia;
    private javax.swing.JTextField CursosagregarAlumnoTxtGProfesor;
    private javax.swing.JTextField CursosagregarAlumnoTxtInscripcion;
    private javax.swing.JTextField agregarAlumnoTxtCantCuotas;
    private javax.swing.JTextField agregarAlumnoTxtCuotasNormal;
    public javax.swing.JButton btnModificar;
    private javax.swing.ButtonGroup buttonGroup1;
    private org.jdesktop.swingx.JXDatePicker certificadoFecha;
    private javax.swing.JRadioButton certificadoFecha1;
    private javax.swing.JRadioButton certificadoFecha2;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JButton nuevoConvenioBtnGuardar;
    private javax.swing.JButton nuevoConvenioBtnModificar;
    private javax.swing.JButton nuevoConvenioBtnVolver;
    private javax.swing.JTextField nuevoConvenioCantidadDeCuotas;
    private javax.swing.JTextField nuevoConvenioCursado;
    private javax.swing.JTextField nuevoConvenioDescripcion;
    private javax.swing.JTextField nuevoConvenioMatriculaAnual;
    private org.jdesktop.swingx.JXDatePicker primeraCuotaFecha;
    private javax.swing.JTextField txtCostoCertificado;
    // End of variables declaration//GEN-END:variables

    

    

   




}
