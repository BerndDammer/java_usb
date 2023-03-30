package usbtest;

import java.util.List;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;

import org.usb4java.javax.Services;

public class USBTest2 {

	public static void main(String[] args) {
		test1();
	}

	public static void test1() {
		UsbServices services;
		try {
			services = UsbHostManager.getUsbServices();
			UsbHub rootHub = services.getRootUsbHub();
			findDevice(rootHub, (short) 0X02E8A, (short) 0X0003);

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UsbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
		for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
			UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
			if (desc.idVendor() == vendorId && desc.idProduct() == productId)
				return device;
			if (device.isUsbHub()) {
				device = findDevice((UsbHub) device, vendorId, productId);
				if (device != null)
					return device;
			}
		}
		return null;
	}
}
