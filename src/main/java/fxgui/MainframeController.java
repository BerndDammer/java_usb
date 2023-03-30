package fxgui;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.usb.UsbDevice;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbPort;
import javax.usb.UsbServices;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainframeController extends GridPane {

	private final Stage stage;
	private final TreeView<String> treeView = new TreeView<>();
	private final TreeItem<String> treeRoot = new TreeItem<String>("Root node");

	public MainframeController(final Stage stage) {
		this.stage = stage;
		try {
			final UsbServices services = UsbHostManager.getUsbServices();
			ObservableList<TreeItem<String>> c = treeRoot.getChildren();
			c.add( getRootInfo(services));

			TreeItem<String> rootHubTI = new TreeItem<String>("RootHub");
			final UsbHub rootHub = services.getRootUsbHub();
			c.add(rootHubTI);
			addHub(rootHub, rootHubTI);
		} catch (Exception e) {
			e.printStackTrace();
		}

		treeView.setShowRoot(true);
		treeView.setRoot(treeRoot);
		treeRoot.setExpanded(true);
		add(treeView, 0, 0);
	}

	private static <X> void addHub(UsbHub hub, TreeItem<String> hubTI)
			throws UnsupportedEncodingException, UsbDisconnectedException, UsbException {
		ObservableList<TreeItem<String>> c = hubTI.getChildren();
		hubTI.setValue(hub.toString());
		// Dump information about the device itself
		final UsbPort port = hub.getParentUsbPort();
//        if (port != null)
//        {
//            c.add(new TreeItem<String>("Connected to port: " + port.getPortNumber()));
//            c.add(new TreeItem<String>("Parent: " + port.getUsbHub()));
//        }

		// Dump device descriptor
		// c.add(new TreeItem<String>(rootHub.getUsbDeviceDescriptor().toString()));
//		c.add(new TreeItem<String>(hub.getProductString()));
		c.add(new TreeItem<String>(hub.toString()));
		List<UsbDevice> devices = (List<UsbDevice>) hub.getAttachedUsbDevices();
		for (UsbDevice ud : devices) {
//			final TreeItem<String> thisTI = new TreeItem<String>(ud.getProductString());
			final TreeItem<String> thisTI = new TreeItem<String>(ud.toString());
			c.add(thisTI);
			if (ud.isUsbHub()) {
				UsbHub asHub = (UsbHub) ud;
				addHub(asHub, thisTI);
			}
		}

		// Process all configurations
//        for (UsbConfiguration configuration: (List<UsbConfiguration>) device
//            .getUsbConfigurations())
//        {
//            // Dump configuration descriptor
//            System.out.println(configuration.getUsbConfigurationDescriptor());
//
//            // Process all interfaces
//            for (UsbInterface iface: (List<UsbInterface>) configuration
//                .getUsbInterfaces())
//            {
//                // Dump the interface descriptor
//                System.out.println(iface.getUsbInterfaceDescriptor());
//
//                // Process all endpoints
//                for (UsbEndpoint endpoint: (List<UsbEndpoint>) iface
//                    .getUsbEndpoints())
//                {
//                    // Dump the endpoint descriptor
//                    System.out.println(endpoint.getUsbEndpointDescriptor());
//                }
//            }
//
	}

	private static TreeItem<String> getRootInfo(final UsbServices services) {
		TreeItem<String> result = new TreeItem<String>("Root Node Info");
		ObservableList<TreeItem<String>> c = result.getChildren();
		c.add(new TreeItem<String>("USB Service Implementation: " + services.getImpDescription()));
		c.add(new TreeItem<String>("Implementation version: " + services.getImpVersion()));
		c.add(new TreeItem<String>("Service API version: " + services.getApiVersion()));
		return result;
	}
}
