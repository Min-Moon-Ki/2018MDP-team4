package d20180312;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class FPGA_Bluetooth implements Bluetooth, DiscoveryListener {

	private final Object temp = new Object();
	private String name;
	
    // The attributes to include in the agent.searchServices() method
    private int[] attributes = {0x0100};	
    
	private RemoteDevice device = null;
	private ServiceRecord service = null;
	
	private StreamConnection connection;
	private DataInputStream din;
	private DataOutputStream dout;
	
	private Vector<RemoteDevice> devicesFound;
	
	/**
	 * connect with designated device
	 */
	private void connectBluetooth() {
		System.out.println("start to connect Bluetooth");
		System.out.println("Connect to btspp://201607274399:1;authenticate=false;encrypt=false;master=false");
		try {
			connection = (StreamConnection)Connector.open("btspp://201607274399:1;authenticate=false;encrypt=false;master=false");
			din = connection.openDataInputStream();
			dout = connection.openDataOutputStream();
		} catch(BluetoothConnectionException e) {
			switch(e.getStatus()) {
				case BluetoothConnectionException.FAILED_NOINFO : System.out.println("FAILED_NOINFO"); break;
				case BluetoothConnectionException.NO_RESOURCES : System.out.println("NO_RESOURCES"); break;
				case BluetoothConnectionException.SECURITY_BLOCK : System.out.println("SECURITY_BLOCK"); break;
				case BluetoothConnectionException.TIMEOUT : System.out.println("TIMEOUT"); break;
				case BluetoothConnectionException.UNACCEPTABLE_PARAMS : System.out.println("UNACCEPTABLE_PARAMS"); break;
				case BluetoothConnectionException.UNKNOWN_PSM : System.out.println("UNKNOWN_PSM"); break;
			}
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Bluetooth connected");
	}
	/**
	 * reset the Data and connect Bluetooth with (name)
	 */
	@Override
	public void connectBluetooth(String name) {
		if(name != null) {
			System.out.println("Search with name : " + name);
			resetData(name);
			while(connection == null) {
				run();	
			}
		} else connectBluetooth();
	}
	/**
	 * connect Bluetooth with named (name)
	 */
	private void run() {
		DiscoveryAgent agent;
		
		devicesFound = new Vector<RemoteDevice>();

		// setup the server to listen for connection
		try {
			System.out.println("start to connect Bluetooth");
			agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
			
			
			synchronized(temp) {
		        agent.startInquiry(DiscoveryAgent.GIAC,this);
				temp.wait();
			}
			
			for(int i = 0; i < devicesFound.size(); i++) {
				//System.out.println(devicesFound.get(i).getFriendlyName(false));
				if(devicesFound.get(i).getFriendlyName(false).equals(name)) {
					device = devicesFound.get(i);
				}
			}
			
			if(device == null) {
				System.out.println("Can't find device");
				return;
			}
			
			UUID[] uuids = new UUID[]{ new UUID(0x1101) };

			synchronized (temp) {
				agent.searchServices(attributes,uuids,device,this);
				temp.wait();
			}
			if(service == null) {
				System.out.println("Can't find Service");
				return;
			}
			//ServiceRecord.AUTHENTICATE_ENCRYPT
			//ServiceRecord.AUTHENTICATE_NOENCRYPT
			//ServiceRecord.NOAUTHENTICATE_NOENCRYPT
			String url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
			System.out.println("Connect to " + url);
			
			try {
				connection = (StreamConnection)Connector.open(url);
				din = connection.openDataInputStream();
				dout = connection.openDataOutputStream();
				System.out.println("Bluetooth connected");
			} catch(BluetoothConnectionException e) {
				switch(e.getStatus()) {
					case BluetoothConnectionException.FAILED_NOINFO : System.out.println("FAILED_NOINFO"); break;
					case BluetoothConnectionException.NO_RESOURCES : System.out.println("NO_RESOURCES"); break;
					case BluetoothConnectionException.SECURITY_BLOCK : System.out.println("SECURITY_BLOCK"); break;
					case BluetoothConnectionException.TIMEOUT : System.out.println("TIMEOUT"); break;
					case BluetoothConnectionException.UNACCEPTABLE_PARAMS : System.out.println("UNACCEPTABLE_PARAMS"); break;
					case BluetoothConnectionException.UNKNOWN_PSM : System.out.println("UNKNOWN_PSM"); break;
				}
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	/**
	 * readData from Bluetooth
	 */
	@Override
	public byte readData() {
		byte b = -1;
		try {
			b = din.readByte();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	/**
	 * writeData to Bluetooth
	 */
	@Override
	public void writeData(byte msg) {
		try {
			dout.writeByte(msg);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 
     * This method is called by the javax.bluetooth.DiscoveryAgent (agent) whenever a bluetooth device is discovered
     * 
     * @param remoteDevice The device discovered
     * @param deviceClass The device class of the discovered device
     * 
     */
	@Override
    public void deviceDiscovered(RemoteDevice remoteDevice,DeviceClass deviceClass){
		devicesFound.add(remoteDevice);
    }

	@Override
	public void inquiryCompleted(int arg0) {
		synchronized(temp) {
			temp.notify();
		}
	}
	@Override
	public void serviceSearchCompleted(int arg0, int arg1) {
		synchronized (temp) {
			temp.notify();			
		}
	}
	
	/**
     * 
     * This method is called by the javax.bluetooth.DiscoveryAgent (agent) whenever one or more services (read: Peer2Me framework) 
     * are found on a remote device
     * 
     * @param transId The transaction ID of the service search that is posting the result
     * @param serviceRecord A list of services found during the search request
     * 
     */
	@Override
	public void servicesDiscovered(int arg0, ServiceRecord[] arg1) {
		service = arg1[0];
	}
	
	private void resetData(String name) {
		this.name = name;
		connection = null;
		device = null;
		service = null;
		try {
			if(din != null) din.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		try {
			if(dout != null) dout.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
