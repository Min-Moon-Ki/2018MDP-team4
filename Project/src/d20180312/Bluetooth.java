package d20180312;
/**
 * Interface to connect FPGA by bluetooth
 * @author admin
 *
 */
public interface Bluetooth {

	public void connectBluetooth(String name);
	public byte readData();
	public void writeData(byte msg);
}
