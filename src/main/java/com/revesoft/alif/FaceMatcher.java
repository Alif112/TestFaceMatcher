package com.revesoft.alif;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alif
 */
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.*;

// import client.ClientDTO;
// import client.ClientRepository;
// import client.UpdateClientDAO;
// import login.LoginDTO;
// import pin.PinBatchDetailsDAO;

public class FaceMatcher extends Thread
{
   LinkedBlockingQueue<PictureDTO> pictureMatchQueue=null;
    static Logger logger = Logger.getLogger(FaceMatcher.class);
    private boolean keepDownloadedNidImages = true;
    public String pythonLibLocationPath = "";
    public static FaceMatcher faceMatcher = null;
    private int fileDownloadRetryCount = 5;
    boolean running=false;
    public void shouldKeepDownloadedNidImages(boolean keep) {
        keepDownloadedNidImages = keep;
    }
    public static FaceMatcher getInstance() {
		if (faceMatcher == null) {
			createInstance();
		}
		return faceMatcher;
	}

	protected static synchronized FaceMatcher createInstance() {
		if (faceMatcher == null) {
			faceMatcher = new FaceMatcher();
		}
		return faceMatcher;
	}
  
	private FaceMatcher()
	{
		pictureMatchQueue=new  LinkedBlockingQueue<PictureDTO>();
		start();
		
	}
	public synchronized void addToQueue(PictureDTO dto)
	{
		try
		{
			pictureMatchQueue.offer(dto);
			
		}
		catch(Exception ee)
		{
			logger.fatal("Exception at adding to queue:",ee);
		}
	}
	public void run()
	{
		running=true;
		
		while(running)
		{
			
			try
			{
				PictureDTO picDTO=pictureMatchQueue.take();
				matchImageAndUpdateuser(picDTO);
			}
			catch(Exception ee)
			{
				logger.fatal("Exception at matching picture:",ee);
			}
		}
	}
	
    private void matchImageAndUpdateuser(PictureDTO picDTO) {
		
    	boolean matched=false;
    	if(ImageSource.faceMatcherServerPort>0 && ImageSource.faceMatcherServerIP!=null)
    	{
    	String urlString = "http://"+ImageSource.faceMatcherServerIP+":" + ImageSource.faceMatcherServerPort + "/face_match/";
        urlString += picDTO.nidPicturePath.replace("/", "+") + "/";
        urlString += picDTO.selfiePicturePath.replace("/", "+");
        logger.debug(urlString);
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
              //  System.out.println(line);
                responseBuilder.append(line);
            }

            String response = responseBuilder.toString();
            logger.debug(response);
            String[] tokens = response.split(",");
            if (tokens.length > 0) {
                matched = "1".equals(tokens[0]);
            }
            logger.debug(matched);

        } catch (Exception e) {
            logger.fatal("Exception at sending request to face matcher server:",e);
        }
    	}
    	else
    	{
    		logger.fatal("invalid face matcher ip:"+ImageSource.faceMatcherServerIP+" and port :"+ImageSource.faceMatcherServerPort);
    		return;
    	}
        
    // 	ClientDTO clientDTO=ClientRepository.getInstance().getClient(picDTO.accountID);
    // 	 int setnewStatus=clientDTO.getStatusID();
    // 	 String [] p_ids=new String[1];
		  // p_ids[0]=""+clientDTO.getAccountID();
    // 	  if(matched)
    //     	  {
    //     		  setnewStatus=ClientDTO.STATUS_ACTIVE;
    //     		  if(clientDTO.getStatusID()!=ClientDTO.STATUS_ACTIVE &&(clientDTO.getStatusID()==ClientDTO.STATUS_AWAITING_VERIFICATION||clientDTO.verificationStatus==ClientDTO.STATUS_AWAITING_DECLINDED))
    //     		  {
    //     			  PinBatchDetailsDAO pinBatchDetailsDAO=new PinBatchDetailsDAO();
    //     			  pinBatchDetailsDAO.updateClientStatus(p_ids, setnewStatus);
    //     			  logger.debug("client status set to :"+setnewStatus);  
    //     		  }
    //     	  }
    		
    //     	 else
    //     		  {
        			  
    //     		 setnewStatus=ClientDTO.STATUS_AWAITING_DECLINDED;
        		  
    //     		  if(clientDTO.getStatusID()==ClientDTO.STATUS_AWAITING_VERIFICATION||clientDTO.verificationStatus==ClientDTO.STATUS_AWAITING_DECLINDED)
    //     		  {
    //     			  logger.debug("updating client status to :"+setnewStatus);
    //     			  UpdateClientDAO updateClientDAO=new UpdateClientDAO();
    //     			  LoginDTO loginDTO=new LoginDTO();
    //     			  loginDTO.setAccountID(clientDTO.getAccountID());
    //     			  updateClientDAO.declineClientVerification(p_ids, loginDTO, setnewStatus);
    //     		  }
    //     		  }
    		
		
		}


	public FaceMatcher(String pythonLibLocationPath) {
        this.pythonLibLocationPath = pythonLibLocationPath;
    }

    public static void init(String pythonLibLocationPath) {
        faceMatcher = new FaceMatcher(pythonLibLocationPath);
    }

    public static FaceMatcher getAccess() throws NullPointerException {
        if (faceMatcher == null) {
            throw new NullPointerException("FaceMatcher is not initialized. Initialize FaceMatcher by FaceMatcher.init(python_lib_directory_path)");
        }
        return faceMatcher;
    }

     void setFileDownloadRetryCount(int retryCount) {
        fileDownloadRetryCount = retryCount;
    }

    public boolean checkNidImageFaceMatchesWithSelfiImage(String nidImagePath, String selfiImagePath) {
        boolean matched = false;
//        pythonLibLocationPath="/usr/local/jakarta-tomcat-7.0.61/webapps/btcl/WEB-INF/classes/face_py_lib/";
        pythonLibLocationPath="/usr/local/apache-tomcat-7.0.109/webapps/btcl/WEB-INF/ext_lib/face_py_lib";
        try {
            Runtime runtime = Runtime.getRuntime();
            String command = "python3 " + pythonLibLocationPath + "/face_compare.py " + nidImagePath + " " + selfiImagePath;
            logger.debug(command);
            Process process = runtime.exec(command);
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            /**  ------------------------------------------------------------------------------
             * Buffered Reader need to read 2 times as api will return 2 lines of output
             * first validation check (cosine similarity check)
             * the second one is the result part  (status, difference)
             * -----------------------------------------------------------------------------------
             * **/
            StringBuilder receiverBuilder=new StringBuilder();
            String str;
            while((str=bufferedReader.readLine())!=null){
                receiverBuilder.append(str);
                receiverBuilder.append("#");
            }
            String result = receiverBuilder.toString();
            System.out.println(result);
            String[] output = result.split("#");
            bufferedReader.close();
            inputStreamReader.close();

            if (output.length<2) {
                InputStreamReader errorInputStreamReader = new InputStreamReader(process.getErrorStream());
                BufferedReader errorBufferedReader = new BufferedReader(errorInputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = errorBufferedReader.readLine()) != null) stringBuilder.append(line);
                errorBufferedReader.close();
                errorInputStreamReader.close();
                throw new IOException("Can not run face_compare.py file. Error : " + stringBuilder.toString());
            }
            else{

                int status = Integer.parseInt(output[1].split(",")[0]);
                float difference = Float.parseFloat(output[1].split(",")[1]);
                matched = status == 1;
                logger.debug("Face matching result difference=" + difference + " tolerance=.6 is matched=" + matched);
            }
        } catch (Exception ee) {
            logger.fatal("Excepton at matching nid image:", ee);
        }

        return matched;
    }

     boolean downloadNidImageAndCheckNidImageFaceMatchesWithSelfiImage(String nidImageLink, String selfiImagePath) {
        boolean result = false;
        try {
            String nidImagePath = selfiImagePath + "_nid_image.jpg";
            if (!downloadNidImage(nidImageLink, nidImagePath)) {
                logger.fatal("Could not able to download Nid Image from " + nidImageLink);
                return result;
            }
            result = checkNidImageFaceMatchesWithSelfiImage(nidImagePath, selfiImagePath);
            if (!keepDownloadedNidImages) {
                File file = new File(nidImagePath);
                file.delete();
                logger.debug(" deleting downloaded images");
            }
        } catch (Exception ee) {
            logger.fatal("exception at matching nid image after downloading:", ee);
        }
        return result;
    }


    private boolean downloadNidImage(String imageLink, String imageFilePath) {

        for (int i = 0; i < fileDownloadRetryCount; i++) {
            try {
                URL url = new URL(imageLink);
                URLConnection urlConnection = url.openConnection();
                BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(imageFilePath);
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                in.close();
                fileOutputStream.close();
//
//                ReadableByteChannel readChannel = Channels.newChannel(new URL(imageLink).openStream());
//                FileOutputStream fileOS = new FileOutputStream(imageFilePath);
//                FileChannel writeChannel = fileOS.getChannel();
//                writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);

                return true;

            } catch (Exception ee) {
                logger.fatal("exception at downloading nid image :", ee);
            }
        }
        return false;


    }


}
