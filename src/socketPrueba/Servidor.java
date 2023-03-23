package socketPrueba;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

public class Servidor {

	public static void main(String[] args) {
		new VentanaServidor().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

class VentanaServidor extends JFrame {
	
	public VentanaServidor() {
		setTitle("Ventana Servidor");
		setBounds(500,200,300,200);
		setVisible(true);
		
		add(new LaminaServidor());

	}
}

class LaminaServidor extends JPanel implements Runnable{
	
	public LaminaServidor() {
		setLayout(new GridLayout(1,0));
		
		textoArea = new JTextArea();
		add(textoArea);
		
		hiloUno = new Thread(this);
		hiloUno.start();
	}
	
	@Override
	public void run() {
		
		String nick,ip,mensajeTexto;
		ArrayList<String> listaUsuario = new ArrayList<String>();
		
		try {
			ServerSocket miServer = new ServerSocket(9999);
			
			while(true) {
				Socket miSocket = miServer.accept();
							
			/*	DataInputStream flujoEntrada = new DataInputStream(miSocket.getInputStream());
				
				mensajeTexto = flujoEntrada.readUTF();
				
				textoArea.append(mensajeTexto+"\n");
				
				flujoEntrada.close();*/
				
				//Paquete recibido :)
				
				ObjectInputStream flujoEntrada = new ObjectInputStream(miSocket.getInputStream());
				
				EnviarDatos datosRecibido = new EnviarDatos();
				
				datosRecibido = (EnviarDatos) flujoEntrada.readObject();
				nick = datosRecibido.getNick();
				ip = datosRecibido.getIp();
				mensajeTexto = datosRecibido.getMensaje();
				flujoEntrada.close();
				
				
				if(!mensajeTexto.equals("Online")) {
					textoArea.append("Usuario "+nick+": "+mensajeTexto+"\n");
					
					//Reenviar paquete :)
					
					Socket reenvioUno = new Socket(ip,9090);
					ObjectOutputStream reenviarPaquete = new ObjectOutputStream(reenvioUno.getOutputStream());
					reenviarPaquete.writeObject(datosRecibido);
					reenviarPaquete.close();
					
				}else {
					//---------- ONLINE --------------------------
					
					InetAddress localizacion = miSocket.getInetAddress();
					
					String ipRemota = localizacion.getHostAddress();
					
					textoArea.append("Online: "+ipRemota+"\n");
					
					listaUsuario.add(ipRemota);
					
					datosRecibido.setIps(listaUsuario);	
					
					for(String z: listaUsuario) {
						Socket reenvioDos = new Socket(z,9090);
						ObjectOutputStream reenviarPaquete = new ObjectOutputStream(reenvioDos.getOutputStream());
						reenviarPaquete.writeObject(datosRecibido);
						reenviarPaquete.close();
					}
					
					//-----------------------------------------------------
				}				
			}			
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(null, "Error, "+e.getMessage());
		} catch( ClassNotFoundException e) {
			JOptionPane.showConfirmDialog(null, "Error, "+e.getMessage());
		}
	}
	
	Thread hiloUno;
	private JTextArea textoArea;
}
