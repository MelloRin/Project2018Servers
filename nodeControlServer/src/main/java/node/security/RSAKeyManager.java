package node.security;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Properties;

import node.fileIO.FileHandler;

public class RSAKeyManager
{
	public static final PublicKey DEFAULT_PUBLIC_KEY;
	public static final PrivateKey DEFAULY_PRIVATE_KEY;
	
    private static RSAKeyManager instance;
    
    private PublicKey publicKey;
    private PrivateKey privateKey;
    
    private byte[] publicKeyByte;
    private byte[] privateKeyByte;
    
    static
    {
    	/*
    	NodeControlCore.getProp(DEFAULT_PUBLIC_KEY);
    	NodeControlCore.getProp(DEFAULT_PRIVATE_KEY_PROP);
    	*/
    	
    	//TODO: this line is temp line (for get properties) need change
    	Properties prop = new Properties();
		try 
		{
			prop.load(FileHandler.getResourceAsStream("/config.properties"));
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: end.
		
    	String temp = prop.getProperty(RSAKeyUtill.DEFAULT_PUBLIC_KEY_PROP);
    	DEFAULT_PUBLIC_KEY = (PublicKey)RSAKeyUtill.convertArrToKey(RSAKeyUtill.convertByteStringtoByteArr(temp));
    	
    	temp = prop.getProperty(RSAKeyUtill.DEFAULT_PRIVATE_KEY_PROP);
    	DEFAULY_PRIVATE_KEY = (PrivateKey)RSAKeyUtill.convertArrToKey(RSAKeyUtill.convertByteStringtoByteArr(temp));
    }
    
    public static void main(String[] args)
    {
    	System.out.println(DEFAULT_PUBLIC_KEY.toString());
    	System.out.println(DEFAULY_PRIVATE_KEY.toString());
    }
    
    
    private RSAKeyManager() 
    {
    	try
    	{
    		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(RSAEncrypt.RSA_ENCRYPT_BYTE_SIZE);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
    	}
    	catch (Exception e) 
    	{
    		e.printStackTrace();
		}
    }
    
    public static RSAKeyManager getInstance()
    {
        if(instance == null)
            instance = new RSAKeyManager();
            
        return instance;
    }
    
    public Key getPublicKey() { return publicKey; }

    public Key getPrivateKey() { return privateKey; }
    /*
    public String getPublicB64()
    {
        if(b64PublicKey == null)
            b64PublicKey = RSAKeyUtill.convertKeyToB64(publicKey);
        
        return b64PublicKey;
    }
    
    public String getPrivateB64()
    {
        if(b64PrivateKey == null)
            b64PrivateKey = RSAKeyUtill.convertKeyToB64(privateKey);
        
        return b64PrivateKey;
    }*/
}