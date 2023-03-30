package fxgui;

import java.util.List;

import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbEndpoint;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbServices;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
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
			c.add(getRootInfo(services));

			final UsbHub rootHub = services.getRootUsbHub();
			c.add(getHubItem(rootHub));

		} catch (Exception e) {
			e.printStackTrace();
		}

		treeView.setShowRoot(true);
		treeView.setRoot(treeRoot);
		treeRoot.setExpanded(true);
		add(treeView, 0, 0);
		extend();
	}

	private TreeItem<String> getHubItem(UsbHub hub) {
		TreeItem<String> result = new TreeItem<>("Hub: " + hub.toString());
		ObservableList<TreeItem<String>> c = result.getChildren();
		// Dump information about the device itself
//		final UsbPort port = hub.getParentUsbPort();
//        if (port != null)
//        {
//            c.add(new TreeItem<String>("Connected to port: " + port.getPortNumber()));
//            c.add(new TreeItem<String>("Parent: " + port.getUsbHub()));
//        }

		// Dump device descriptor
		// c.add(new TreeItem<String>(rootHub.getUsbDeviceDescriptor().toString()));
//		c.add(new TreeItem<String>(hub.getProductString()));
		List<UsbDevice> devices = (List<UsbDevice>) hub.getAttachedUsbDevices();
		for (UsbDevice ud : devices) {
			if (ud.isUsbHub()) {
				c.add(getHubItem((UsbHub) ud));
			} else {
				c.add(getDeviceItem(ud));
			}
		}
		return result;
	}

	private TreeItem<String> getDeviceItem(UsbDevice usbDevice) {
		final TreeItem<String> result = new TreeItem<>("Device: " + usbDevice.toString());
		final ObservableList<TreeItem<String>> c = result.getChildren();

		@SuppressWarnings(value = "unchecked")
		final List<UsbConfiguration> configurations = (List<UsbConfiguration>) usbDevice.getUsbConfigurations();

		if (configurations.size() == 1) {
			final UsbConfiguration usbConfiguration = configurations.get(0);

			@SuppressWarnings("unchecked")
			final List<UsbInterface> interfaces = (List<UsbInterface>) usbConfiguration.getUsbInterfaces();

			for (UsbInterface interfacce : interfaces) {
				c.add(getInterfaceItem(interfacce, false));
			}
		} else {
			for (UsbConfiguration configuration : configurations) {
				c.add(getConfigurationItem(configuration));
			}
		}
		return result;
	}

	private static TreeItem<String> getConfigurationItem(UsbConfiguration configuration) {
		final TreeItem<String> result = new TreeItem<>("Configuration: " + configuration.toString());
		final ObservableList<TreeItem<String>> c = result.getChildren();
		return result;
	}

	private static TreeItem<String> getInterfaceItem(UsbInterface interfacce, boolean isAlternate) {
		final TreeItem<String> result = new TreeItem<>("Interface: " + interfacce.toString());
		final ObservableList<TreeItem<String>> c = result.getChildren();
		
		c.add(new TreeItem<String>( interfacce.getUsbInterfaceDescriptor().toString()));
		
		if (interfacce.getNumSettings() != 1) {
			c.add(new TreeItem<String>("Num Settings :  " + interfacce.getNumSettings()));
		}
		@SuppressWarnings(value = "unchecked")
		final List<UsbEndpoint> endpoints = (List<UsbEndpoint>) interfacce.getUsbEndpoints();
		for (UsbEndpoint endpoint : endpoints) {
			c.add(getEndpointItem(endpoint));
		}
		return result;
	}

	private static TreeItem<String> getEndpointItem(UsbEndpoint endpoint) {
		final TreeItem<String> result = new TreeItem<>("Endpoint: " + endpoint.toString());
		final ObservableList<TreeItem<String>> c = result.getChildren();
		c.add( new TreeItem<String>( endpoint.getUsbEndpointDescriptor().toString() ));
		return result;
	}

	private static TreeItem<String> getRootInfo(final UsbServices services) {
		TreeItem<String> result = new TreeItem<String>("Root Node Info");
		ObservableList<TreeItem<String>> c = result.getChildren();
		c.add(new TreeItem<String>("USB Service Implementation: " + services.getImpDescription()));
		c.add(new TreeItem<String>("Implementation version: " + services.getImpVersion()));
		c.add(new TreeItem<String>("Service API version: " + services.getApiVersion()));
		return result;
	}

	private void extend() {
		setVgap(4.0);
		setHgap(4.0);

		ColumnConstraints c = new ColumnConstraints();
		c.setHgrow(Priority.SOMETIMES);
		for (int i = 0; i < 1; i++) {
			getColumnConstraints().add(c);
		}
		RowConstraints r = new RowConstraints();
		r.setVgrow(Priority.SOMETIMES);
		for (int i = 0; i < 1; i++) {
			getRowConstraints().add(r);
		}
		setBorder(new Border(new BorderStroke(Color.DARKGRAY,
				  BorderStrokeStyle.SOLID,
				  CornerRadii.EMPTY, new BorderWidths(3.0))));
	}
}
