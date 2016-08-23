package resources;

import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFrame;

import org.apache.commons.io.IOUtils;

import org.primefaces.context.RequestContext;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.swing.JRViewer;

/*
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.view.JasperViewer;*/

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

/**
 * Session Bean implementation class SelectOneMenuBean
 */

@SuppressWarnings("unused")
@ManagedBean
@RequestScoped

public class SelectOneMenuBean {

	private int counter = 0;
	private String lastMessage = null;
	private List<String> messages = new ArrayList<>();
	private boolean action = false;

	private Connection conexion;
	private String cedula;
	private Long num_factur;
	private String concepto;
	private String val_iva;
	private String valor;
	private String fecha_emi;

	private String now() {
		return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(counter));
	}

	public Long getNum_factur() {
		return num_factur;
	}

	public void setNum_factur(Long num_factur) {
		System.out.println(num_factur);
		this.num_factur = num_factur;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public String getVal_iva() {
		return val_iva;
	}

	public void setVal_iva(String val_iva) {
		this.val_iva = val_iva;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public List<SelectOneMenuBean> getFactura() {
		return factura;
	}

	public void setFactura(ArrayList<SelectOneMenuBean> factura) {
		this.factura = factura;
	}

	private List<SelectItem> personas = new ArrayList<SelectItem>();
	private List<SelectItem> facturas = new ArrayList<SelectItem>();
	private ArrayList<SelectOneMenuBean> factura = new ArrayList<SelectOneMenuBean>();

	public List<SelectItem> getFacturas() {
		return facturas;
	}

	public void setFacturas(List<SelectItem> facturas) {
		this.facturas = facturas;
	}

	public List<SelectItem> getPersonas() {
		return personas;
	}

	public void setPersonas(List<SelectItem> personas) {
		this.personas = personas;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getFecha_emi() {
		return fecha_emi;
	}

	public void setFecha_emi(String fecha_emi) {
		this.fecha_emi = fecha_emi;
	}

	/**
	 * Default constructor.
	 */
	public SelectOneMenuBean() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Items constructor.
	 */
	public SelectOneMenuBean(Long num_factur, String cedula, String concepto, String val_iva, String valor,
			String fecha_emi) {
		this.num_factur = num_factur;
		this.cedula = cedula;
		this.concepto = concepto;
		this.val_iva = val_iva;
		this.valor = valor;
		this.fecha_emi = fecha_emi;
	}

	public Connection getConexion() {
		return conexion;
	}

	public void setConexion(Connection conexion) {
		this.conexion = conexion;
	}

	public SelectOneMenuBean conectar() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String BaseDeDatos = "jdbc:mysql://localhost:3306/afianzate";
			conexion = DriverManager.getConnection(BaseDeDatos, "afianzate", "ticos1013");
			if (conexion != null) {
				System.out.println("Conexion exitosa!");
			} else {
				System.out.println("Conexion fallida!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	public void desconectar() {
		try {
			this.conexion.close();
			System.out.println("Desconexion exitosa!");
		} catch (Exception e) {
			System.out.println("ERROR_CLOSE_BD" + e);
		}
	}

	public ResultSet consultar(String sql) {
		ResultSet resultado = null;
		try {
			Statement sentencia;
			sentencia = getConexion().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			resultado = sentencia.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return resultado;
	}

	public List<SelectItem> getlistarFacturas() {

		SelectOneMenuBean f = new SelectOneMenuBean();
		f.conectar();
		String sql = "SELECT CEDULA, NUM_FACTUR FROM FACTURACAB";
		ResultSet resultados = f.consultar(sql);
		try {
			while (resultados.next()) {
				System.out.println(resultados.getString("NUM_FACTUR") + " " + resultados.getLong("CEDULA"));
				facturas.add(new SelectItem(resultados.getString("NUM_FACTUR"), resultados.getString("CEDULA"),
						resultados.getString("NUM_FACTUR")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return facturas;
	}

	// Metodo para MySQL
	public ArrayList<SelectOneMenuBean> getDatosFacturaByCedula() {

		System.out.println("CEDULA:" + this.cedula);
		// System.out.println("Cedula:"+id);
		if (this.cedula != null && this.cedula != "" && !(this.cedula.isEmpty())) {
			// System.out.println("Cedula:"+this.cedula);
			SelectOneMenuBean f = new SelectOneMenuBean();
			SelectOneMenuBean con = f.conectar();
			String sql = "SELECT CEDULA, NUM_FACTUR, CONCEPTO, " + " FORMAT(VAL_IVA,2) AS VAL_IVA, "
					+ " FORMAT(VALOR,2) AS VALOR, " + " FECHA_EMI " + " FROM FACTURACAB " + " WHERE CEDULA = '"
					+ this.cedula + "'";
			System.out.println("Query1:" + sql);
			ResultSet resultados = f.consultar(sql);
			try {
				while (resultados.next()) {
					/*
					 * System.out.println(resultados.getLong("NUM_FACTUR")+" "
					 * +resultados.getString("CEDULA")+" "+resultados.getString(
					 * "CONCEPTO")+" "+resultados.getLong("VAL_IVA")+" "
					 * +resultados.getLong("VALOR"));
					 */
					this.cedula = resultados.getString("CEDULA");
					this.num_factur = resultados.getLong("NUM_FACTUR");
					this.concepto = resultados.getString("CONCEPTO");
					this.val_iva = resultados.getString("VAL_IVA");
					this.valor = resultados.getString("VALOR");
					this.fecha_emi = resultados.getString("FECHA_EMI");
					factura.add(new SelectOneMenuBean(this.num_factur, this.cedula, this.concepto, this.val_iva,
							this.valor, this.fecha_emi));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			f.desconectar();
			return factura;

		} else {
			return null;
		}
	}

	private void report(String event) {
		if (event.equals(lastMessage)) {
			counter++;
			messages.set(0, now() + event + " (" + counter + ")");
		} else {
			counter = 1;
			messages.add(0, now() + event);
		}
		lastMessage = event;
		// System.out.println(event);
	}

	// Metodo para MySQL
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void pdfFromXmlFile() throws SQLException, IOException {
		// System.out.println("PDFFROMXML METHOD");

		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		String value = ec.getRequestParameterMap().get("hidden1");

		System.out.println("Valor:" + value);

		ServletContext ctx = (ServletContext) ec.getContext();
		String realPath_in_jrxml = ctx.getRealPath("/jaspertemplate/invoice.jrxml");
		String realPath_in_jasper = ctx.getRealPath("/jaspertemplate/invoice.jasper");

		System.out.println("JRXML:" + realPath_in_jrxml);

		if (value != null && value != "" && !value.isEmpty()) {

			/* Definir la ruta absoluta del PDF (OUT) a partir de la relativa */
			String realPath_out = ctx.getRealPath("/invoices");
			File pdfAbsolutePath = new File(realPath_out + "/invoice_" + value + ".pdf");
			System.out.println("realPath_out:" + realPath_out + " pdfAbsolutePath:" + pdfAbsolutePath);
			File fichero = new File(pdfAbsolutePath.toString());

			if (!fichero.exists()) {
				SelectOneMenuBean f = new SelectOneMenuBean();
				SelectOneMenuBean con = f.conectar();
				try {
					String sql = "SELECT GYR_CLIENTE.CEDULA, " + "   NUM_FACTUR, " + "   CONCEPTO," + "   VAL_IVA,"
							+ "   FORMAT(VAL_IVA,2) AS VAL_IVA_F," + "   VALOR," + "   FORMAT(VALOR,2) AS VALOR_F,"
							+ "   (VALOR+VAL_IVA) AS TOTAL, " + "   FORMAT(VALOR+VAL_IVA,2) AS TOTAL_F," + "   NOMBRE, "
							+ "   DIRECCION, " + "   TELEFONO, " + "   FECHA_EMI "
							+ "   FROM FACTURACAB, GYR_CLIENTE WHERE NUM_FACTUR = " + value
							+ " AND FACTURACAB.CEDULA = GYR_CLIENTE.CEDULA";
					System.out.println(sql);
					ResultSet resultado = f.consultar(sql);

					try {

						JRResultSetDataSource resultSetDataSource = new JRResultSetDataSource(resultado);
						System.out.println("ResultJasper:" + resultSetDataSource);

						String jsFile = JasperCompileManager.compileReportToFile(realPath_in_jrxml);

						JasperPrint jasperPrint = JasperFillManager.fillReport(jsFile, new HashMap(),
								resultSetDataSource);

						/*
						 * JRViewer jv = new JRViewer(jasperPrint); JFrame jf =
						 * new JFrame(); jf.getContentPane().add(jv);
						 * jf.validate(); jf.setVisible(true); jf.setSize(new
						 * Dimension(800,600)); jf.setLocation(300,100);
						 * jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						 */

						fc.release();
						ec.responseReset();

						ec.setResponseContentType("application/pdf");
						ec.setResponseHeader("Content-Disposition", "attachment; filename='invoice_" + value + ".pdf'");
						// ec.setResponseContentLength(ec.getResponseBufferSize());
						// //Genera problemas en Tomcat

						OutputStream output = ec.getResponseOutputStream();
						// JasperExportManager.exportReportToPdfStream(jasperPrint,
						// output);

						Exporter exporter = new JRPdfExporter();
						exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
						exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(output));
						exporter.exportReport();

						fc.responseComplete();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().put("hidden1",
					// null);
					f.desconectar();
				}
			} else {
				// System.out.println("El fichero " + pdfAbsolutePath + "
				// existe");
			}
		} else {
			System.out.println("Value:" + value);
		}
	}

}
