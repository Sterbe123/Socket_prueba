package socketPrueba;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

public class Cliente {

	public static void main(String[] args) {
		new VentanaCliente().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

class VentanaCliente extends JFrame{
	
	public VentanaCliente() {
		setTitle("Ventana Cliente");
		setBounds(200,200,400,300);
							
		add(new LaminaCliente());
		setVisible(true);
		
		addWindowListener(new EnvioOnline());
	}
}

class EnvioOnline extends WindowAdapter{
	
	public void windowOpened(WindowEvent e) {

		try {
			Socket miSocketOyente = new Socket("192.168.0.4",9999);
			
			EnviarDatos paquete = new EnviarDatos();
			paquete.setMensaje("Online");
			
			ObjectOutputStream nuevoPaquete = new ObjectOutputStream(miSocketOyente.getOutputStream());
			nuevoPaquete.writeObject(paquete);
			nuevoPaquete.close();
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}

class LaminaCliente extends JPanel implements Runnable{
	
	public LaminaCliente() {
		setLayout(new BorderLayout());
		
		String usuarioNick = JOptionPane.showInputDialog("Ingrese su nombre de Usuario: ");
		
		//-------------------------------------------------------
		laminaDato = new JPanel();
		etiquetaNick = new JLabel("Nick:");
		cajaNick = new JLabel(usuarioNick);
		etiquetaIp = new JLabel("     Online: ");
		
		cajaIp = new JComboBox<String>();
		laminaDato.add(etiquetaNick);
		laminaDato.add(cajaNick);
		laminaDato.add(etiquetaIp);
		laminaDato.add(cajaIp);
		add(laminaDato,BorderLayout.NORTH);
		
		//------------------------------------------------------
		laminaChat = new JPanel();
		laminaChat.setLayout(new GridLayout(1,0));
		cajaChat = new JTextArea();	
		cajaChat.setEnabled(false);
		laminaChat.add(cajaChat);
		add(laminaChat,BorderLayout.CENTER);
			
		//----------------------------------------------------		
		laminaBoton = new JPanel();
		add(laminaBoton,BorderLayout.SOUTH);
		
		cajaDato = new JTextField(20);
		laminaBoton.add(cajaDato);
			
		botonEnviar = new JButton("Enviar");
		laminaBoton.add(botonEnviar);
		
		hilo = new Thread(this);
		hilo.start();		
		
		botonEnviar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// Enviar datos 
				
				misDatos = new EnviarDatos();
				misDatos.setNick(cajaNick.getText());
		//		misDatos.setIp(cajaIp.getSelectedItem().toString());
				misDatos.setMensaje(cajaDato.getText());
				
				cajaChat.append("Yo: "+cajaDato.getText()+"\n");
				
				try {
					Socket miSocket = new Socket("192.168.0.4",9999);
					
					ObjectOutputStream flujoSalida = new ObjectOutputStream(miSocket.getOutputStream());
					flujoSalida.writeObject(misDatos);
					flujoSalida.close();
									
					// este codigo sirve para  mandar paquetes por los sockets
					 
				/*	DataOutputStream flujoSalida = new DataOutputStream(miSocket.getOutputStream());
					flujoSalida.writeUTF(cajaDato.getText());
					flujoSalida.close();*/
										
				} catch (UnknownHostException e1) {
					JOptionPane.showMessageDialog(null, "Error, "+e1.getMessage());
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Error, "+e1.getMessage());
				}finally {
					cajaDato.setText("");
					cajaDato.grabFocus();
				}			
			}		
		});		
	}
	
	@Override
	public void run() {
		//Recibir datos :)
		ServerSocket datosRecibido;
		ArrayList<String> listaOnline = new ArrayList<String>();
		try {
			datosRecibido = new ServerSocket(9090);
			while(true) {	
				
				Socket recibirPaquete = datosRecibido.accept();
				ObjectInputStream paquete = new ObjectInputStream(recibirPaquete.getInputStream());
				misDatosRecibido = new EnviarDatos();
				misDatosRecibido = (EnviarDatos) paquete.readObject();

				if(misDatosRecibido.getMensaje().equals("Online")) {
					listaOnline = misDatosRecibido.getIps();
					cajaIp.removeAllItems();
					
					for(String z:listaOnline) {
						cajaIp.addItem(z);
					}
				}else {
					cajaChat.append(misDatosRecibido.getNick()+": "+misDatosRecibido.getMensaje()+"\n");
				}
			
				
				paquete.close();
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}		
	}
	
	private JPanel laminaChat;
	private JPanel laminaBoton;
	private JPanel laminaDato;
	private JTextField cajaDato;
	private JLabel cajaNick;
	private JComboBox cajaIp;
	private JTextArea cajaChat;
	private JLabel etiquetaNick;
	private JLabel etiquetaIp;
	private JButton botonEnviar;
	private Thread hilo;
	private EnviarDatos misDatos;
	private EnviarDatos misDatosRecibido;
}















