package usbtest;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

public class USBTest1 {

	public static void main_dis(String[] args) {
		test2();
	}

	public static void test1() {
		try {
			Context context = new Context();
			int result = LibUsb.init(context);
			if (result != LibUsb.SUCCESS)
				throw new LibUsbException("Unable to initialize libusb.", result);
			Device device = findDevice((short) 0X02E8A, (short) 0X0003, context);
			System.out.println("Device " + device);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test2() {
		int result;
		DeviceList list = new DeviceList();
		try {
			Context context = new Context();
			result = LibUsb.init(context);
			if (result != LibUsb.SUCCESS)
				throw new LibUsbException("Unable to initialize libusb.", result);

			result = LibUsb.getDeviceList(context, list);
			if (result < 0)
				throw new LibUsbException("Unable to get device list", result);

			// Iterate over all devices and scan for the right one
			for (Device device : list) {
				// System.out.println( "SCAN : " + device.toString() );
				DeviceDescriptor descriptor = new DeviceDescriptor();
				result = LibUsb.getDeviceDescriptor(device, descriptor);
				if (result != LibUsb.SUCCESS)
					throw new LibUsbException("Unable to read device descriptor", result);
				{
					dumpDD(descriptor);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Ensure the allocated device list is freed
			LibUsb.freeDeviceList(list, true);
		}

	}

	public static Device findDevice(short vendorId, short productId, Context context) {
		// Read the USB device list
		DeviceList list = new DeviceList();
		int result = LibUsb.getDeviceList(context, list);
		if (result < 0)
			throw new LibUsbException("Unable to get device list", result);

		try {
			// Iterate over all devices and scan for the right one
			for (Device device : list) {
				System.out.println("SCAN : " + device.toString());
				DeviceDescriptor descriptor = new DeviceDescriptor();
				result = LibUsb.getDeviceDescriptor(device, descriptor);
				if (result != LibUsb.SUCCESS)
					throw new LibUsbException("Unable to read device descriptor", result);
				if (descriptor.idVendor() == vendorId && descriptor.idProduct() == productId)
					return device;
			}
		} finally {
			// Ensure the allocated device list is freed
			LibUsb.freeDeviceList(list, true);
		}

		// Device not found
		return null;
	}

	private static void dumpDD(DeviceDescriptor descriptor) {
		System.out.printf("Device %04X %04X %n", cut16(descriptor.idVendor()), cut16(descriptor.idProduct()));
		System.out.println(descriptor.dump());
		

	}
	private static int cut16(int i)
	{
		return( i & 0xFFFF);
	}
}
