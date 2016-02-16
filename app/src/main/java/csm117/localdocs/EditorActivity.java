package csm117.localdocs;

import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.app.Activity;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity {

	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	/**
	 * Name of the connected device
	 */
	private String mConnectedDeviceName = null;
	/**
	 * Local Bluetooth adapter
	 */
	private BluetoothAdapter mBluetoothAdapter = null;
	/**
	 * Member object for the bluetooth services
	 */
	//private BluetoothService mChatService = null;
	private ArrayList<BluetoothService> mServices = new ArrayList<BluetoothService>();

	private ArrayList<String> receivedData = new ArrayList<String>();

	// Start the merge activity.  Will later need to get text of merge.
	public void startMerge(View view) {
		Intent intent = new Intent(this, MergeActivity.class);
		startActivity(intent);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Sent doc", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
				EditText textView = (EditText) findViewById(R.id.editor);
				String message = "s" + textView.getText().toString();
				sendMessage(message);
			}
		});

		FloatingActionButton fabRetrieve = (FloatingActionButton) findViewById(R.id.fabRetrieve);
		fabRetrieve.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Retrieved doc", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
				EditText textView = (EditText) findViewById(R.id.editor);
				String message = "r";
				sendMessage(message);
			}
		});

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	private void sendMessage(String message) {
		for (int i = 0; i < mServices.size(); i++) {
			BluetoothService mChatService = mServices.get(i);
			// Check that we're actually connected before trying anything
			if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
				Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
				return;
			}

			// Check that there's actually something to send
			if (message.length() > 0) {
				// Get the message bytes and tell the BluetoothChatService to write
				byte[] send = message.getBytes();
				mChatService.write(send);
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else if (mBluetoothAdapter != null && mServices.size() == 0) {
			setupChat();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		for (int i = 0; i < mServices.size(); i++) {
			BluetoothService mChatService = mServices.get(i);
			if (mChatService != null) {
				mChatService.stop();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Toast.makeText(EditorActivity.this, "On Resume ", Toast.LENGTH_SHORT).show();

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
		for (int i = 0; i < mServices.size(); i++) {
			BluetoothService mChatService = mServices.get(i);
			if (mChatService != null) {
				// Only if the state is STATE_NONE, do we know that we haven't started already
				if (mChatService.getState() == BluetoothService.STATE_NONE) {
					// Start the Bluetooth chat services
					mChatService.start();
				}
			}
		}
	}

	/**
	 * Makes this device discoverable.
	 */
	private void ensureDiscoverable() {
		if (mBluetoothAdapter != null && mBluetoothAdapter.getScanMode() !=
				BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}
	/**
	 * Updates the status on the action bar.
	 *
	 * @param resId a string resource ID
	 */
	private void setStatus(int resId) {
		final ActionBar actionBar = this.getActionBar();
		if (null == actionBar) {
			return;
		}
		actionBar.setSubtitle(resId);
	}

	/**
	 * Updates the status on the action bar.
	 *
	 * @param subTitle status
	 */
	private void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = this.getActionBar();
		if (null == actionBar) {
			return;
		}
		actionBar.setSubtitle(subTitle);
	}

	/**
	 * The Handler that gets information back from the BluetoothChatService
	 */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Constants.MESSAGE_STATE_CHANGE:
					switch (msg.arg1) {
						case BluetoothService.STATE_CONNECTED:
							setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
							setupChat();
							break;
						case BluetoothService.STATE_CONNECTING:
							setStatus(R.string.title_connecting);
							break;
						case BluetoothService.STATE_LISTEN:
						case BluetoothService.STATE_NONE:
							setStatus(R.string.title_not_connected);
							break;
					}
					break;
				case Constants.MESSAGE_WRITE:
					byte[] writeBuf = (byte[]) msg.obj;
					// construct a string from the buffer
					String writeMessage = new String(writeBuf);
					//mConversationArrayAdapter.add("Me:  " + writeMessage);
					break;
				case Constants.MESSAGE_READ:
					byte[] readBuf = (byte[]) msg.obj;
					// construct a string from the valid bytes in the buffer
					String readMessage = new String(readBuf, 0, msg.arg1);
					if (readMessage.charAt(0) == 's') {
						EditText textView = (EditText) EditorActivity.this.findViewById(R.id.editor);
						textView.setText(readMessage.substring(1), EditText.BufferType.EDITABLE);
						//mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
					}
					else if (readMessage.charAt(0) == 'r') {
						EditText textView = (EditText) EditorActivity.this.findViewById(R.id.editor);
						String message = "s" + textView.getText().toString();
						EditorActivity.this.sendMessage(message);
					}
					break;
				case Constants.MESSAGE_DEVICE_NAME:
					// save the connected device's name
					mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
						Toast.makeText(EditorActivity.this, "Connected to "
								+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
					break;
				case Constants.MESSAGE_TOAST:
						Toast.makeText(EditorActivity.this, msg.getData().getString(Constants.TOAST),
								Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

	private void setupChat() {
		boolean started = false;
		for (int i = 0; i < mServices.size(); i++) {
			BluetoothService mChatService = mServices.get(i);
			if (mChatService != null) {
				if (mChatService.getState() == BluetoothService.STATE_LISTEN)
					return;
				// Only if the state is STATE_NONE, do we know that we haven't started already
				if (mChatService.getState() == BluetoothService.STATE_NONE) {
					// Start the Bluetooth chat services
					mChatService.start();
					started = true;
				}
			}
		}
		if (!started) {
			mServices.add(new BluetoothService(this, mHandler));
			mServices.get(mServices.size() - 1).start();
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CONNECT_DEVICE_SECURE:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) {
					connectDevice(data, true);
				}
				break;
			case REQUEST_CONNECT_DEVICE_INSECURE:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) {
					connectDevice(data, false);
				}
				break;
			case REQUEST_ENABLE_BT:
				// When the request to enable Bluetooth returns
				if (resultCode == Activity.RESULT_OK) {
					// Bluetooth is now enabled, so set up a chat session
					setupChat();
				} else {
					// User did not enable Bluetooth or an error occurred
					Toast.makeText(this, R.string.bt_not_enabled_leaving,
							Toast.LENGTH_SHORT).show();
					finish();
				}
		}
	}

	/**
	 * Establish connection with other divice
	 *
	 * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
	 * @param secure Socket Security type - Secure (true) , Insecure (false)
	 */
	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras()
				.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		BluetoothService mChatService = null;
		for (int i = 0; i < mServices.size(); i++) {
			if (mServices.get(i).getState() != BluetoothService.STATE_CONNECTED && mServices.get(i).getState() != BluetoothService.STATE_CONNECTING)
				mChatService = mServices.get(i);
		}
		if (mChatService == null) {
			mServices.add(new BluetoothService(this, mHandler));
			mChatService = mServices.get(mServices.size() - 1);
		}
		mChatService.connect(device, secure);
		setupChat();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_editor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}
		else if (id == R.id.discoverable) {
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		else if (id == R.id.secure_connect_scan) {
			//Dennis test of DeviceListActivity
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
