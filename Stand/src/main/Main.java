package main;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.util.Console;

import stand.communicate.Declearation;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel camPane;
	private JLabel image;
	private JList<String> users;
	private JScrollPane jcr;
	private JTable inform;
	private JButton declaration;
	private LinkedList<Declearation> dec_list;
	
	private boolean view;

	private ServerSocket sc;
	private Thread accepter;
	private Thread informer;
	private ArrayList<Users> userlist;
	
	private GpioPinDigitalOutput lamp;
	
	private Date now;
	private Thread lampTimer;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setExtendedState(MAXIMIZED_BOTH);
		setUndecorated(true);
		//contentPane.setBackground(Color.BLUE);
		GpioController gpio = GpioFactory.getInstance();
		lamp = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "Police Alam", PinState.HIGH);
		lamp.setShutdownOptions(true, PinState.HIGH);
		lampTimer = new Thread() {
			@Override
			public void run() {
				while(true) {
					if(now == null) 
						synchronized (lampTimer) {
							try {
								lampTimer.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					else 
						if(now.getTime() + 3000 <= Calendar.getInstance().getTime().getTime()) {
							lamp.high();
							synchronized (lampTimer) {
								try {
									lampTimer.wait();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						} else {
							if(lamp.isHigh())
								lamp.low();
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
				}
			}
		};
		lampTimer.start();
		view = false;
		
		JLabel jlb = new JLabel("Users : ");
		jlb.setBounds(getWidth()*3/4-10,10,getWidth()/4,10);
		add(jlb);
		
		users = new JList<String>();
		jcr = new JScrollPane(users);
		jcr.setBounds(getWidth()*3/4-10,22,getWidth()/4,getHeight()*8/10-52);
		DefaultListModel<String> dlm = new DefaultListModel<String>();
		users.setModel(dlm);
		add(jcr);
		
		camPane = new JPanel();
		camPane.setBounds(10,10,getWidth()*3/4-30,getHeight()-20);
		camPane.setBorder(new EtchedBorder());
		camPane.setVisible(false);
		add(camPane);
		
		image = new JLabel();
		image.setSize(camPane.getSize());
		camPane.add(image);
		
		inform = new JTable();
		{
			String[] str = {"Name","Location","Time"};
			DefaultTableModel dm = new DefaultTableModel(null,str);
			inform.setModel(dm);
		}
		inform.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				if(inform.getSelectedRow() != -1) {
					view = true;
					declaration.setEnabled(view);
					jcr.setVisible(!view);
					camPane.setVisible(view);
					image.setIcon(new ImageIcon(dec_list.get(inform.getSelectedRow()).getImage()));
				}
			}
		});
		jcr = new JScrollPane(inform);
		jcr.setBounds(10,10,getWidth()*3/4-30,getHeight()-20);
		add(jcr);
		
		declaration = new JButton("Declaration");
		declaration.setBounds(getWidth()*3/4-10,getHeight()*8/10-20,getWidth()/4,getHeight()/10);
		declaration.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				view = false;
				declaration.setEnabled(view);
				jcr.setVisible(!view);
				camPane.setVisible(view);
			}
		});
		declaration.setEnabled(false);
		add(declaration);
		
		JButton shutdown = new JButton("Shutdown");
		shutdown.setBounds(getWidth()*3/4-10,getHeight()*9/10-10,getWidth()/4,getHeight()/10);
		shutdown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				destroy();
			}
		});
		add(shutdown);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowDeactivated(WindowEvent arg0) {
				super.windowDeactivated(arg0);
				requestFocus();
			}
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				destroy();
			}
		});
		
		try {
			sc = new ServerSocket(2580);
			
			accepter = new Thread() {
				public void run() {
					try {
						while(true) {
							Socket soc = sc.accept();
							BufferedReader br = new BufferedReader(new InputStreamReader(soc.getInputStream()));
							String name = br.readLine();
							userlist.add(new Users(soc,name));
							((DefaultListModel<String>)users.getModel()).addElement(name);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			accepter.start();
			
			informer = new Thread() {
				@Override
				public void run() {
					while(true) {
						if(userlist.isEmpty())
							synchronized (informer) {
								try {
									informer.wait();
								} catch (InterruptedException e) { 
									e.printStackTrace();
								}
							}
						else
							for(Users user : userlist) {
								addDeclaration(user.readDeclear());
							}
					}
				}
			};
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void addDeclaration(Declearation dec) {
		now = Calendar.getInstance().getTime();
		synchronized (lampTimer) {
			lampTimer.notify();
		}
		dec_list.add(dec);
		((DefaultTableModel)inform.getModel()).addRow(dec.getInfo());
	}
	
	public void destroy() {
		accepter.interrupt();
		informer.interrupt();
		new Console().goodbye();
		System.exit(0);
	}
}
