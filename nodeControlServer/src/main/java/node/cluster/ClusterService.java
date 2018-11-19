package node.cluster;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import node.IServiceModule;
import node.NodeControlCore;
import node.bash.CommandExecutor;
import node.cluster.spark.SparkManager;
import node.log.LogWriter;
import node.util.observer.Observable;
import node.detection.NodeDetectionEvent;
import node.detection.NodeDetectionService;

public class ClusterService implements IServiceModule {
	public static final int SPARK_INSTALLED = 0;
	public static final int SPARK_NOT_INSTALLED = 1;
	
	private boolean isMaster;
	private String masterIp;
	private int connectState;
	private int instFlag;
	
	public final NodeDetectionEventReceiver ndEventReceiver = new NodeDetectionEventReceiver(this);
	public final NodeInfoChangeEventSender nicEventSender = new NodeInfoChangeEventSender();
	public final SparkManager sparkManager = new SparkManager();
	public final Observable<NodeDetectionEvent> nds;
	
	public static final Logger clusterLogger = LogWriter.createLogger(ClusterService.class, "cluster");
	
	public ClusterService(Observable<NodeDetectionEvent> nds) {
		this.isMaster = false;
		this.instFlag = SPARK_NOT_INSTALLED;
		this.connectState = NodeDetectionEvent.STATE_FAIL;
		this.nds = nds;
		this.masterIp = null;
		nds.addObserver(ndEventReceiver);
	}
	public void instSpark() {
		clusterLogger.log(Level.INFO, "스파크 설치확인");
		if(sparkManager.initSpark()) {
			instFlag = SPARK_INSTALLED;
		}
	}

	public boolean reciveEvent(NodeDetectionEvent eventInfo) {
		
		if(eventInfo == null) {
			clusterLogger.log(Level.SEVERE, "Not Given Network State Change Event", new Exception("Not Given Network State Change Event"));
			return false;
		}
		clusterLogger.log(Level.INFO, "NodeDetectionEvent 이벤트 받음, Master IP : " + eventInfo.masterIP.getHostAddress() + "Is Master? : " + eventInfo.isMaster);
		
		if(this.masterIp == null || (this.isMaster != eventInfo.isMaster && this.isMaster == false) ) {	//마스터IP가 바뀔때 마스터, 워커 프로세스를 종료시켜준다.(잔존 프로세스 제거)
			sparkManager.stopSparkMaster();
			sparkManager.stopSparkWorker();
		}
		this.isMaster = eventInfo.isMaster;
		this.masterIp = eventInfo.masterIP.getHostAddress();
		this.connectState = eventInfo.state;
		
		return true;
		
	}
	public boolean startSpark() {		//스파크의 서버(마스터일때만) 와 워커를 실행
		clusterLogger.log(Level.INFO, "스파크 시작");
		
		if(this.instFlag == SPARK_NOT_INSTALLED) {
			clusterLogger.log(Level.SEVERE, "Not Installed Spark", new Exception("Not Installed Spark"));
			
			return false;
		}
		
		if(isMaster == true)	{
			sparkManager.startSparkMaster(masterIp, "");
		}
		sparkManager.startSparkWorker(masterIp, "");
		return true;
	}
	
	
	@Override
	public boolean startModule() {		//객체 초기화 생성및 쓰레드 초기화 생성
		// TODO Auto-generated method stub
		this.instSpark();
		if(this.instFlag == SPARK_NOT_INSTALLED) {
			clusterLogger.log(Level.SEVERE, "Not Installed Spark", new Exception("Not Installed Spark"));
			
			return false;
		}
		clusterLogger.log(Level.INFO, "Spark 설치 여부 : " + instFlag);
		
		return true;
	}

	@Override
	public void stopModule() {		//스타트 모듈에서 생성된 객체들을 다시 종료시키는곳.
		// TODO Auto-generated method stub
		
	}

}