package com.example.rex2;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.os.Bundle;
import android.os.ParcelUuid;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.ApplicationInfo;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 
		txtAppend("rex2. Built: " + getBuildTime());  
		
		onDisconnected();
		
		findViewById(R.id.btnConnect).setOnClickListener(this);    
		findViewById(R.id.btnDisconnect).setOnClickListener(this);
		findViewById(R.id.btnMvFl).setOnClickListener(this);
		findViewById(R.id.btnMvFr).setOnClickListener(this);
		findViewById(R.id.btnMvBl).setOnClickListener(this);
		findViewById(R.id.btnMvBr).setOnClickListener(this);
		findViewById(R.id.btnFwd).setOnClickListener(this);
		findViewById(R.id.btnStop).setOnClickListener(this);    
		findViewById(R.id.btnLef).setOnClickListener(this);
		findViewById(R.id.btnRgt).setOnClickListener(this); 
		findViewById(R.id.btnClr).setOnClickListener(this);  
		findViewById(R.id.btnToggleIndivControls).setOnClickListener(this);
		setButtonsGrouped(true);
	}    
	 
	private Button findButton(int id) { 
		return ((Button) findViewById(id));
	}
 
	private void connect() {
		try {
		BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter(); 
		
		if (bta == null) {
			txtAppend("Bluetooth Adaptor is null");  
		} else {
			txtAppend("Bluetooth Adaptor found");
			
			Set<BluetoothDevice> pairs = bta.getBondedDevices();
			
			for (BluetoothDevice btd : pairs) {
				txtAppend("paired BT dev: " + btd.getName());
				
				if (btd.getName().equalsIgnoreCase("HC-06")) {
					txtAppend("discovering...");
					btd.fetchUuidsWithSdp();
					txtAppend("discovered"); 
					 
					if (btd.getUuids() == null) { 
						txtAppend("Supports nothing! Wtf."); 
						return; 
					} else {
						for (ParcelUuid parcelableUuid : btd.getUuids()) {
							txtAppend("- supports:" + lookupUuid(parcelableUuid.getUuid())); 
						} 
					}  
					 
					txtAppend("found rex, connecting");
					 
					try {  
						this.sock =  btd.createRfcommSocketToServiceRecord(serialPortServiceUuid);
						txtAppend("sock is: " + sock); 
						
						sock.connect();
						onConnected();  
						return; 
					} catch (IOException e) {
						txtAppendException(e);
					}
				}
			}
		}  
		} catch (Exception e) {
			txtAppend(e.toString()); 
		}
		
		onDisconnected();
	}
	
	private BluetoothSocket sock; 
	
	private static UUID serialPortServiceUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private static HashMap<String, String> btServices = new HashMap<String, String>();
	
	static {
		btServices.put("1105", "OBEXPush");  
		btServices.put("1106", "OBEXFileTransferServiceClass");
		btServices.put("1101", "SerialPort"); 
		btServices.put("110a", "AudioSourceServiceClass"); 
		btServices.put("110c", "AVRemoteControlTargetServiceClass");
		btServices.put("110e", "AVRemoteControlServiceClass");
		btServices.put("111f", "HandsfreeAudioGatewayServiceClass");
		btServices.put("1112", "HeadsetAudioGatewayServiceClass");
	} 
	
	private String lookupUuid(UUID uuid) {
		String btServiceId = uuid.toString().substring(4, 8);
		  
		if (btServices.containsKey(btServiceId)) {
			return btServices.get(btServiceId);
		} else { 
			return btServiceId;
		} 
	}
	
	private TextView findConsole() {
		 return (TextView) findViewById(R.id.TextView1);
	} 
	
	private void txtAppend(String txt) {
		TextView field = findConsole();
		field.append(txt + "\n");  
 
    	((ScrollView)findViewById(R.id.scrollView1)).fullScroll(View.FOCUS_DOWN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btnConnect:
			connect();
			break;
		case R.id.btnDisconnect:
			disconnect();
			break;
		case R.id.btnMvFl: send(0x1); break; 
		case R.id.btnMvFr: send(0x2); break;
		case R.id.btnMvBl: send(0x3); break;
		case R.id.btnMvBr: send(0x4); break;
		case R.id.btnStop: send(0x0); break;  
		case R.id.btnFwd: send(0x5); break; 
		case R.id.btnLef: send(0x6); break;
		case R.id.btnRgt: send(0x7); break;  
		case R.id.btnClr:  
			((TextView) findViewById(R.id.TextView1)).setText("");
			txtAppend("Cleared"); 
			break;
		case R.id.btnToggleIndivControls: 
			setButtonsGrouped(((ToggleButton)findButton(R.id.btnToggleIndivControls)).isChecked());
		}   
	}
	 
	private void setButtonsGrouped(boolean checked) {
		int viewChecked = checked ? View.GONE : View.VISIBLE;
		int viewUnchecked = checked ? View.VISIBLE : View.GONE;
		 
		((ToggleButton)findViewById(R.id.btnToggleIndivControls)).setChecked(checked);
		  
		findButton(R.id.btnMvFl).setVisibility(viewChecked);
		findButton(R.id.btnMvFr).setVisibility(viewChecked);    
		findButton(R.id.btnMvBl).setVisibility(viewChecked);
		findButton(R.id.btnMvBr).setVisibility(viewChecked);
		 
		findButton(R.id.btnFwd).setVisibility(viewUnchecked);
		findButton(R.id.btnLef).setVisibility(viewUnchecked);  
		findButton(R.id.btnRgt).setVisibility(viewUnchecked);
		findButton(R.id.btnBak).setVisibility(viewUnchecked);
	}  
	  
	private void send(BitSet set) {
		txtAppend(set.toString());
	}

	public String getBuildTime() {
		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), 0);
			ZipFile zf = new ZipFile(ai.sourceDir);
			ZipEntry ze = zf.getEntry("classes.dex");
			long time = ze.getTime();
			String s = SimpleDateFormat.getInstance().format(new java.util.Date(time));
			
			return s;
		} catch(Exception e) {
		} 
		    
		return "?";  
	}
	    
	private void send(int b) { 
		try {
			if (sock == null || !sock.isConnected()) {
				onDisconnected();
				txtAppend("Not connected");
				return; 
			}
			
			sock.getOutputStream().write(b);
			sock.getOutputStream().flush();   
		} catch (Exception e) {
			txtAppendException(e);
			onDisconnected();
		}   
	}
	
	private void onConnected() {
		findButton(R.id.btnConnect).setEnabled(false);
		findButton(R.id.btnDisconnect).setEnabled(true);
		
		findButton(R.id.btnMvFl).setEnabled(true);
		findButton(R.id.btnMvFr).setEnabled(true);
		findButton(R.id.btnMvBl).setEnabled(true);
		findButton(R.id.btnMvBr).setEnabled(true);
		 
		findButton(R.id.btnFwd).setEnabled(true);
		findButton(R.id.btnLef).setEnabled(true);
		findButton(R.id.btnStop).setEnabled(true);
		findButton(R.id.btnRgt).setEnabled(true);
		findButton(R.id.btnStop).setEnabled(true); 
	}
	
	private void onDisconnected() {
		findButton(R.id.btnConnect).setEnabled(true);
		findButton(R.id.btnDisconnect).setEnabled(false);   
		
		findButton(R.id.btnMvFl).setEnabled(false);
		findButton(R.id.btnMvFr).setEnabled(false);
		findButton(R.id.btnMvBl).setEnabled(false);
		findButton(R.id.btnMvBr).setEnabled(false);
		 
		findButton(R.id.btnFwd).setEnabled(false);
		findButton(R.id.btnLef).setEnabled(false);
		findButton(R.id.btnStop).setEnabled(false);
		findButton(R.id.btnRgt).setEnabled(false);
		findButton(R.id.btnStop).setEnabled(false);
	} 
	
	private void txtAppendException(Exception e ) {
		txtAppend(e.toString());    
		for (StackTraceElement ste : e.getStackTrace()) {
			txtAppend(ste.getFileName() + ":" + ste.getLineNumber());
		}   
	}
	
	private void disconnect() {
		try {
			if (sock == null) { 
				txtAppend("Already disconnected.");
				return; 
			}
			
			sock.close();
			txtAppend("Closed");
		} catch (IOException e) {
			txtAppendException(e);
		} 

	}

}
