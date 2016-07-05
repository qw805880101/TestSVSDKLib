package com.example.testsvsdklib;

import java.util.ArrayList;
import java.util.HashMap;

import topsec.sslvpn.svsdklib.SVSDKLib;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TestSVSDKLib extends Activity implements OnClickListener {
	private static final String TAG = "TestSVSDKLib";

	public static final int VPN_MSG_STATUS_UPDATE = 100; // VPN״̬֪ͨ��Ϣ��
	public static final int QUERY_VPN_MSG_STATUS_UPDATE = 101; // VPN״̬֪ͨ��Ϣ��

	// VPN��������ַ���˿ںš��û���������
		private final String VPN_SERVER = "59.49.15.130";
		private final int VPN_PORT = 443;
		private final String VPN_USERNAME = "oa";
		private final String VPN_PASSWORD = "123456";

	private Dialog mShowingDialog;

	public Handler MsgHandler = new Handler() {
		// ��������message,�÷����ɸ����н��м̳�.
		@Override
		public void handleMessage(Message msg) {
			int msgID = msg.what;

			Bundle bundle = (Bundle) msg.obj;

			switch (msg.what) {
			case VPN_MSG_STATUS_UPDATE: // VPN�ⷢ����Ϣ����
			{
				if (null != bundle) {
					String vpnStatus = bundle.getString("vpnstatus");
					String vpnErr = bundle.getString("vpnerror");
					
					if (vpnStatus.equalsIgnoreCase("6")) {
						// VPN��������ɹ�
						Log.i(TAG, "VPN����Ϣ֪ͨ��VPN��������ɹ�");
						// http://127.0.0.1:30080/cctv2/Jhsoft.mobileapp/login/loginbyurl.html?userName=trx&pwd=111111

						Toast.makeText(TestSVSDKLib.this, "VPN����Ϣ֪ͨ��VPN��������ɹ�",
								Toast.LENGTH_SHORT).show();
					}

					if (vpnStatus.equalsIgnoreCase("200")) {
						Toast.makeText(TestSVSDKLib.this, "VPN����Ϣ֪ͨ��VPN�����ʱ",
								Toast.LENGTH_SHORT).show();
					}

					if (!vpnErr.equalsIgnoreCase("0")) {

						if (vpnErr.equalsIgnoreCase("10")) {
							Log.i(TAG,
									"VPN����Ϣ֪ͨ��VPN��Ҫ���µ�½������ʾ�û�����ѡ���Ƿ��߳���һ���û���������ǿ���߳���һ���û�");
							SVSDKLib vpnlib = SVSDKLib.getInstance();
							vpnlib.reLoginVPN();

						} else {
							// VPN�����������
							Toast.makeText(TestSVSDKLib.this,
									"VPN����Ϣ֪ͨ����ǰVPN����Ϊ��" + vpnErr,
									Toast.LENGTH_SHORT).show();

						}
					}
				}
			}
				break;
			case QUERY_VPN_MSG_STATUS_UPDATE: // ��ѯ�߳���Ϣ����
			{
				if (null != bundle) {
					String vpnStatus = bundle.getString("vpnstatus");
					String vpnErr = bundle.getString("vpnerror");
					if (vpnStatus.equalsIgnoreCase("6")) {
						// VPN��������ɹ�
						Toast.makeText(TestSVSDKLib.this, "��ѯ�߳�֪ͨ��VPN��������ɹ�",
								Toast.LENGTH_SHORT).show();
					}

					if (!vpnErr.equalsIgnoreCase("0")) {
						// VPN�����������
						Toast.makeText(TestSVSDKLib.this,
								"��ѯ�߳�֪ͨ����ǰVPN����Ϊ��" + vpnErr, Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		findviewByid();

		// ��ʼ��VPNSDK��
		InitSVSDKLib();

	}

	private void findviewByid() {

		Button btnStart = (Button) findViewById(R.id.BTN_START);
		Button btnStop = (Button) findViewById(R.id.BTN_STOP);
		Button btnConnectSvr = (Button) findViewById(R.id.BTN_GETVPNSTATUS);


		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		btnConnectSvr.setOnClickListener(this);

	}

	// ��ʼ��VPN��
	private void InitSVSDKLib() {

		// ��ȡVPN��ʵ��
		SVSDKLib vpnlib = SVSDKLib.getInstance();

		// ����VPN�ͻ��˵��ͷ�Ŀ¼
		Context appContext = getApplicationContext();
		vpnlib.setSVClientPath(appContext.getFilesDir().getPath());

		// ����Ӧ�ó�����ʲ�������
		vpnlib.setAppam(this.getAssets());

		
				
		// ����VPNSDK�����Ϣ������
		vpnlib.setMsgHandler(MsgHandler);

		// ����VPNSDK���VPN״̬�����Ϣ��
		vpnlib.setVPNMsgID(VPN_MSG_STATUS_UPDATE);

		// ����VPN������Ϣ
		vpnlib.setVPNInfo(VPN_SERVER, VPN_PORT, VPN_USERNAME, VPN_PASSWORD);

		// VPN�ͻ�������ǰ��׼��
		vpnlib.prepareVPNSettings();

		Log.i(TAG, "InitSVSDKLib done");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.BTN_START: {
			doStart();

			break;
		}

		case R.id.BTN_STOP: {
			doStop();

			break;
		}

		case R.id.BTN_GETVPNSTATUS: {
			doGetVPNStatus();

			break;
		}

		}
	}

	private void showErrorDialog(String sErrInfo) {

		mShowingDialog = new AlertDialog.Builder(this).setTitle("����")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(sErrInfo)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						return;
					}
				}).create();
		mShowingDialog.show();
		return;
	}

	// ����VPN����
	private void doStart() {
		Log.i(TAG, "start vpn");
		SVSDKLib vpnlib = SVSDKLib.getInstance();

		// ��ȡ����
		EditText edtIP = (EditText) findViewById(R.id.edtip);
		EditText edtPort = (EditText) findViewById(R.id.edtport);
		EditText edtUname = (EditText) findViewById(R.id.edtuname);
		EditText edtUpwd = (EditText) findViewById(R.id.edtupwd);

		String sIP, sUname, sUpwd;
		int port;

		sIP = edtIP.getText().toString();
		sUname = edtUname.getText().toString();
		sUpwd = edtUpwd.getText().toString();

		if (sIP == null || sIP.length() == 0) {
			showErrorDialog("VPN��ַ����Ϊ��");
			return;
		}

		try {
			port = Integer.parseInt(edtPort.getText().toString());
		} catch (NumberFormatException e) {
			showErrorDialog("VPN�˿���Ч��Χ��1-65535");
			return;
		}

		if ((port <= 0) || (port > 65535)) {
			showErrorDialog("VPN�˿���Ч��Χ��1-65535");
			return;
		}

		if (sUname == null || sUname.length() == 0) {
			showErrorDialog("�û�������Ϊ��");
			return;
		}
		if (sUpwd == null || sUpwd.length() == 0) {
			showErrorDialog("�û����벻��Ϊ��");
			return;
		}

		// vpnlib.setVPNInfo("192.168.95.84", 443, "1", "111111");
		vpnlib.setVPNInfo(sIP, port, sUname, sUpwd);
		Log.i("ttt", "ip= " + sIP + " port= " + port + " uname= " + sUname);
		vpnlib.prepareVPNSettings();
		// ��ȡVPN��ʵ��
		vpnlib.stopVPN();
		// ����VPN����
		vpnlib.startVPN();
		// ����һ����ѯ�̣߳�������ѯVPN״̬��VPN�ɹ���
		// ��ѯ�̻߳��UI���̷߳�����Ϣ
		// ��VPNSDK���ʼ��ʱ������MsgHandler��MSGID
		// ����VPNSDK��������֪ͨ�����߳̿ɲ��ؿ���
//		 new GetVPNStatusThread().start();

	}

	// �ر�VPN����
	private void doStop() {
		// ֹͣVPN����
		SVSDKLib vpnlib = SVSDKLib.getInstance();
		vpnlib.stopVPN();
	}

	// ��ȡVPN״̬
	private void doGetVPNStatus() {
		// ��ȡVPN״̬
		String sVPNStatus;
		SVSDKLib vpnlib = SVSDKLib.getInstance();
		sVPNStatus = vpnlib.getVPNStatus();
		
		Toast.makeText(TestSVSDKLib.this, "��ǰVPN״̬Ϊ��" + sVPNStatus,
				Toast.LENGTH_SHORT).show();
		
		ArrayList<HashMap<String, String>> reslist = vpnlib.getResList();
		Log.i("test", "port1 :" + vpnlib.getResLocalPort("59.49.15.130", 443));
		Log.i("test", "port2 :" + vpnlib.getResLocalPort("59.49.15.130", 443));

	}

	// VPN״̬��ѯ�߳�
//	private class GetVPNStatusThread extends Thread {
//		// @Override
//		public void run() {
//			String sVPNStatus;
//			String sVPNErr;
//			SVSDKLib vpnlib = SVSDKLib.getInstance();
//
//			int nTimeOut = 10; // ��ʱʱ�䣬10��
//			int nTimeStep = 0;
//
//			try {
//				while (nTimeStep < nTimeOut) {
//					sVPNStatus = vpnlib.getVPNStatus();
//					sVPNErr = vpnlib.getVPNError();
//					if (sVPNStatus.equalsIgnoreCase("6")) {
//						// ֪ͨ������VPN���ӳɹ�
//						Message msg = Message.obtain(MsgHandler);
//						msg.what = QUERY_VPN_MSG_STATUS_UPDATE;
//
//						sVPNStatus = "6";
//
//						Bundle bundle = new Bundle();
//						bundle.putString("vpnerror", sVPNErr);
//						bundle.putString("vpnstatus", sVPNStatus);
//
//						msg.obj = bundle;
//						msg.sendToTarget();
//						break;
//					}
//
//					if (!sVPNErr.equalsIgnoreCase("0")) {
//						// ֪ͨ������VPN���ӳ�����
//						Message msg = Message.obtain(MsgHandler);
//						msg.what = QUERY_VPN_MSG_STATUS_UPDATE;
//
//						Bundle bundle = new Bundle();
//						bundle.putString("vpnerror", sVPNErr);
//						bundle.putString("vpnstatus", sVPNStatus);
//
//						
//						
//						msg.sendToTarget();
//						msg.obj = bundle;
//						break;
//					}
//
//				}
//				Thread.sleep(1000);
//				nTimeStep++;
//
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return;
//		}
//	}

}