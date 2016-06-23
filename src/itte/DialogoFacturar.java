/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package itte;

import itte.clases.Funciones;
import itte.clases.modelos.Certificado;
import itte.clases.modelos.Matriculas;
import itte.clases.modelos.PlanPagoCuotas;
import itte.clases.modelos.Recargos;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Juan M S
 */
public class DialogoFacturar extends javax.swing.JDialog {

    private final int CAMPO_ID      = 0;
    private final int CAMPO_MOV     = 1;
    private final int CAMPO_MES     = 2;
    private final int CAMPO_CURSO   = 3;
    private final int CAMPO_SALDO   = 4;
    
    private Matriculas matricula;
    private DefaultTableModel modeloTabla;
    
    /**
     * [0]idDeuda          
     * [1]conceptoDeuda    
     * [2]tipoDeuda        
     * [3]valorCuota       
     */
    
    private ArrayList<Double> listaSubTotales;
    private String tipoFactura;
    
    /**
     * Creates new form DialogoFacturar
     * @param matricula
     * @param factura: true=factura false=recibo
     */
    public DialogoFacturar(Matriculas matricula, String tipoFactura, DefaultTableModel idsAFacturarB) {
        super();
        
        this.tipoFactura = tipoFactura;
        this.matricula = matricula;
        this.matricula.obtenerCliente();
        listaSubTotales = new ArrayList<Double>();
        initComponents();
        
        modeloTabla = (DefaultTableModel )tabla.getModel();     
        
        
        setearTablaMovimientos(idsAFacturarB);
        tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                if(tabla.getSelectedRowCount()>0){
                    int filaABorrar = tabla.getSelectedRow();
                    tabla.getSelectionModel().clearSelection();
                    modeloTabla.removeRow(filaABorrar);
                    listaSubTotales.remove(filaABorrar);
                    double saldoTotal = 0;
                    for(int i = 0; i<modeloTabla.getRowCount(); i++){
                        double v = Double.valueOf(modeloTabla.getValueAt(i, 3).toString().replace(",", "."));
                        saldoTotal += v;
                    }
                    lblSaldoTotal.setText(String.format("%.2f", saldoTotal));
                }
            }
        });
    }
    
    public void printComponenet(){

        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setJobName(" Print Component ");

        pj.setPrintable (new Printable() {    
          @Override
          public int print(Graphics pg, PageFormat pf, int pageNum){
            if (pageNum > 0){
                return Printable.NO_SUCH_PAGE;
            }
            Graphics2D g2 = (Graphics2D) pg;
            g2.translate(pf.getImageableX(), pf.getImageableY());
            paintAll(g2);
            return Printable.PAGE_EXISTS;
          }
        });
        if (pj.printDialog()){
            try {
                pj.print();
            } catch (PrinterException ex) {
                // handle exception
            }
        }
    }
    
    private void setearTablaMovimientos(DefaultTableModel idsAFacturarB){
            if(modeloTabla.getRowCount()>0){
                for(int i = modeloTabla.getRowCount() - 1; i>=0; i--){
                    modeloTabla.removeRow(i);
                }
            }
            
            
            
            double saldoTotal=0;
            for (int i = 0; i < idsAFacturarB.getRowCount(); i++){
            
            modeloTabla.addRow((Vector)idsAFacturarB.getDataVector().elementAt(i));
            
            saldoTotal += Double.parseDouble(idsAFacturarB.getValueAt(i, 4).toString());
        }
        //modeloTabla.setDataVector(filas,columnas);
        Recargos descOrec = new Recargos();
        Object [] fila = new Object[5];
        int cantFilas = modeloTabla.getRowCount();
        for(int i = 0; i < cantFilas; i++){
            
            switch(modeloTabla.getValueAt(i, CAMPO_MOV).toString()){
                case "CUOTA":
                    PlanPagoCuotas ppc = new PlanPagoCuotas();
                    ppc.obtenerSegunId(Integer.parseInt(modeloTabla.getValueAt(i, CAMPO_ID).toString()));
                    ppc.sig();
                    descOrec.calcularRecargoCuota(ppc.getFechapago());
                break;
                
                case "CERTIFICADO":
                    String [] idAlumnoYmes = modeloTabla.getValueAt(i, CAMPO_ID).toString().split("-");
                    Certificado cert = new Certificado();
                    cert.obtenerSegunIdAlumnoYMes(idAlumnoYmes[0],idAlumnoYmes[1]);
                    cert.sig();
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(cert.getSoloAnioI(), cert.getSoloMesI(), 1);
                    descOrec.calcularRecargoCertificado(calendar.getTime());
                break;
            }
            if(descOrec.getId_concepto()!=0){
                fila[CAMPO_ID] = "";
                fila[CAMPO_MOV] = descOrec.getConcepto();
                fila[CAMPO_MES] = "";
                fila[CAMPO_CURSO] = modeloTabla.getValueAt(i, CAMPO_CURSO).toString();
                fila[CAMPO_SALDO] = String.valueOf(descOrec.getMonto());
                modeloTabla.insertRow(i+1,fila);
                i++;
                cantFilas++;
                saldoTotal += descOrec.getMonto();
            }
        }
            
            
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            tabla.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
            tabla.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
            tabla.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
            
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblCliente = new javax.swing.JLabel();
        lblDomicilio = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblCondVenta = new javax.swing.JLabel();
        lblCondIva = new javax.swing.JLabel();
        lblCuit = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        lblSaldoTotal = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        btnFacturar = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/itte/otros/logo-itte.png"))); // NOI18N

        jLabel2.setText("Resumen de cuenta perteneciente a:");

        jLabel3.setText("CLIENTE:");

        jLabel4.setText("DOMICILIO:");

        jLabel5.setText("EMAIL:");

        lblCliente.setText(matricula.getCliente().getRazonSocial());

        lblDomicilio.setText(matricula.getCliente().getDireccion());

        lblEmail.setText(matricula.getCliente().getEmail());

        jLabel9.setText("COND. VENTA:");

        jLabel10.setText("COND. IVA:");

        String tipo = matricula.getCliente().getCuit();
        tipo = tipo.contains("-")?"CUIT:":"DNI:";
        jLabel11.setText(tipo);

        String cv = "";
        if(matricula.getCliente().getCondicionVenta()==0){
            cv="Contado";
        } else {
            if(matricula.getCliente().getCondicionVenta()==1){
                cv="Tarjeta";
            } else {
                cv="Otro";
            }
        }
        lblCondVenta.setText(cv);

        String ci = "";
        if(matricula.getCliente().getCondicionIva()==0){
            cv="Consumidor Final";
        } else {
            if(matricula.getCliente().getCondicionIva()==1){
                cv="Mono Tributista";
            } else {
                cv="Responsable Inscripto";
            }
        }
        lblCondIva.setText(cv);

        String cuit = matricula.getCliente().getCuit();
        lblCuit.setText(cuit);

        jLabel15.setText("FECHA:");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        lblFecha.setText(Funciones.obtenerFechaFormateada(calendar));

        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "Mov", "Mes", "Curso/Año", "Subtotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tabla);
        if (tabla.getColumnModel().getColumnCount() > 0) {
            tabla.getColumnModel().getColumn(0).setMinWidth(0);
            tabla.getColumnModel().getColumn(0).setPreferredWidth(0);
            tabla.getColumnModel().getColumn(0).setMaxWidth(0);
            tabla.getColumnModel().getColumn(1).setMinWidth(100);
            tabla.getColumnModel().getColumn(1).setPreferredWidth(100);
            tabla.getColumnModel().getColumn(1).setMaxWidth(100);
            tabla.getColumnModel().getColumn(2).setMinWidth(200);
            tabla.getColumnModel().getColumn(2).setPreferredWidth(200);
            tabla.getColumnModel().getColumn(2).setMaxWidth(200);
            tabla.getColumnModel().getColumn(4).setMinWidth(100);
            tabla.getColumnModel().getColumn(4).setPreferredWidth(100);
            tabla.getColumnModel().getColumn(4).setMaxWidth(100);
        }
        modeloTabla = (DefaultTableModel)tabla.getModel();

        jLabel6.setFont(new java.awt.Font("Times New Roman", 3, 14)); // NOI18N
        jLabel6.setText("Total:");

        lblSaldoTotal.setFont(new java.awt.Font("Times New Roman", 3, 14)); // NOI18N
        lblSaldoTotal.setText("1050.00");

        jButton1.setText("Cancelar");

        btnFacturar.setText("Facturar");
        btnFacturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFacturarActionPerformed(evt);
            }
        });

        jButton2.setText("Impr");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnFacturar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1))
                            .addComponent(jButton2)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel5))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(lblEmail, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblCliente, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblDomicilio, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addGap(123, 123, 123)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(44, 44, 44)
                                                .addComponent(jLabel11))
                                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addGap(15, 15, 15)
                                                .addComponent(jLabel10)))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblCondVenta)
                                            .addComponent(lblCondIva)
                                            .addComponent(lblCuit))
                                        .addGap(98, 98, 98)
                                        .addComponent(jLabel15)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblFecha)))
                                .addGap(0, 140, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblSaldoTotal)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(btnFacturar))
                        .addGap(10, 10, 10)
                        .addComponent(jButton2)))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lblCliente)
                    .addComponent(jLabel9)
                    .addComponent(lblCondVenta)
                    .addComponent(jLabel15)
                    .addComponent(lblFecha))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblDomicilio)
                    .addComponent(jLabel10)
                    .addComponent(lblCondIva))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblEmail)
                    .addComponent(jLabel11)
                    .addComponent(lblCuit))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblSaldoTotal))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFacturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFacturarActionPerformed
/*       if(idsAFacturar.size()>0){
           Clientes cliente = new Clientes(matricula.getId_cliente());
           int ultimoId=-1;
           Detalles detalles = new Detalles();
           String detalleCaja = "";
           switch(tipoFactura){
                case "factura":
                    Factura factura = new Factura();
                    factura.setId_local(1);
                    factura.setFecha(new Date());
                    factura.setId_cliente(matricula.getId_cliente());
                    factura.setCondicion_iva(cliente.getStringCondicionIva());
                    factura.setCondicion_venta(cliente.getStringCondicionVenta());
                    factura.setCuit(cliente.getCuit());
                    factura.setMonto(Double.valueOf(lblSaldoTotal.getText()));
                    ultimoId = factura.guardarComoNueva(); 
                    detalleCaja = "(INGRESO) FACTURACI\u00d3N (F)";
                    break;
                case "recibo":
                    Recibo recibo = new Recibo();
                    recibo.setId_local(1);
                    recibo.setFecha(new Date());
                    recibo.setId_cliente(matricula.getId_cliente());
                    recibo.setCondicion_iva(cliente.getStringCondicionIva());
                    recibo.setCondicion_venta(cliente.getStringCondicionVenta());
                    recibo.setCuit(cliente.getCuit());
                    recibo.setMonto(Double.valueOf(lblSaldoTotal.getText()));
                    ultimoId = recibo.guardarComoNueva();
                    detalleCaja = "(INGRESO) FACTURACI\u00d3N (R)";
                    break;
                case "credito":
                    NotaCredito notaCredito = new NotaCredito();
                    notaCredito.setId_local(1);
                    notaCredito.setFecha(new Date());
                    notaCredito.setId_cliente(matricula.getId_cliente());
                    notaCredito.setCondicion_iva(cliente.getStringCondicionIva());
                    notaCredito.setCondicion_venta(cliente.getStringCondicionVenta());
                    notaCredito.setCuit(cliente.getCuit());
                    notaCredito.setMonto(Double.valueOf(lblSaldoTotal.getText()));
                    ultimoId = notaCredito.guardarComoNueva();
                    detalleCaja = "(INGRESO) FACTURACI\u00d3N -NC-";
                    break;
           }
           detalles.setTipoComprobante(tipoFactura);
           detalles.setId_comprobante(ultimoId);
           detalles.setCantidad(1);
           
           if(ultimoId!=-1){
               ConceptosCaja cc = new ConceptosCaja();
               Caja ca = new Caja();
               if(!cc.obtenerConcepto(detalleCaja)){
                   cc.setDetalle(detalleCaja);
                   cc.setId_concepto_caja(cc.obtenerIdUltimo()+1);
                   cc.guardarComoNueva();
               }
               ca.setDetalle(String.valueOf(ultimoId));
               ca.setDetalle2(String.format(" (Mat.%d)", matricula.getMatricula()));
               ca.setId_concepto_caja(cc.getId_concepto_caja());
               ca.setConcepto_caja(cc.getDetalle());
               ca.setFecha(Funciones.obtenerDate(new Date()));
               ca.setIngreso(Double.valueOf(lblSaldoTotal.getText()));
               ca.setEgreso(0);
               ca.setActivo(1);
               ca.setAuditoria(String.format("%s[Alta]", (new Configuraciones()).getSesion()));
               ca.setSaldo(ca.getIngreso() - ca.getEgreso() + ca.obtenerSaldoAnterior());
               ca.guardarComoNueva();              
           }
           
           Movimientos mov = new Movimientos();
           Movimientos movViejo = new Movimientos();
           
           mov.setMatricula(matricula.getMatricula());
           mov.setId_alumno(0);
           mov.setFecha(Funciones.obtenerDate(new Date()));
           
           
           /*
           A continuacion se recorren todos los movimientos en busca de notas de credito
           si se encuentra una nota de credito se recorre la lista de movimientos en busca de cuotas
           cuando se encuentra una cuota se pregunta si el valor de la nota de credito es mayor que la cuota
           en cuyo caso se le resta a la nota de credito el valor de la cuota, y se pone la cuota en 0
           si no es mayor, se le resta a la cuota el valor de la nota de credito, y se pone la nc en 0
           i es la posicion que hace referencia a la nota de credito actual
           j es la posicion que hace referencia a la cuota actual
           */
           /*int i = 0;
           for(Movimientos movs : idsAFacturar){ 
               if(movs.getId_concepto()==15){
                   if(listaSubTotales.get(i)<0){
                       for(int j = idsAFacturar.size(); 0<j; j--){
                           if(idsAFacturar.get(j).getId_concepto()==5){
                               if(-listaSubTotales.get(i)>listaSubTotales.get(j)){
                                   listaSubTotales.set(i,listaSubTotales.get(i)+listaSubTotales.get(j));
                                   listaSubTotales.set(j, 0d);
                               } else {
                                   listaSubTotales.set(j,listaSubTotales.get(j)+listaSubTotales.get(i));
                                   listaSubTotales.set(i, 0d);
                                   j = 0; //como ya no le queda resto a la NC salimos del for
                               }
                           }
                       }
                   }
               }
               i++;
           }
           
           for(i = 0; i< idsAFacturar.size(); i++){
               
           }
           
           
           
       }*/
    }//GEN-LAST:event_btnFacturarActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        printComponenet();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFacturar;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblCondIva;
    private javax.swing.JLabel lblCondVenta;
    private javax.swing.JLabel lblCuit;
    private javax.swing.JLabel lblDomicilio;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblSaldoTotal;
    private javax.swing.JTable tabla;
    // End of variables declaration//GEN-END:variables
}
