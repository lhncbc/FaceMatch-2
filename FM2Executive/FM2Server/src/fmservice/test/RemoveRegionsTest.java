/*
 * /*
 * Informational Notice:
 * This software was developed under contract funded by the National Library of Medicine, which is part of the National Institutes of Health, 
 * an agency of the Department of Health and Human Services, United States Government.
 *
 * The license of this software is an open-source BSD license.  It allows use in both commercial and non-commercial products.
 *
 * The license does not supersede any applicable United States law.
 *
 * The license does not indemnify you from any claims brought by third parties whose proprietary rights may be infringed by your usage of this software.
 *
 * Government usage rights for this software are established by Federal law, which includes, but may not be limited to, Federal Acquisition Regulation 
 * (FAR) 48 C.F.R. Part52.227-14, Rights in Dataï¿½General.
 * The license for this software is intended to be expansive, rather than restrictive, in encouraging the use of this software in both commercial and 
 * non-commercial products.
 *
 * LICENSE:
 *
 * Government Usage Rights Notice:  The U.S. Government retains unlimited, royalty-free usage rights to this software, but not ownership,
 * as provided by Federal law.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * -	Redistributions of source code must retain the above Government Usage Rights Notice, this list of conditions and the following disclaimer.
 *
 * -	Redistributions in binary form must reproduce the above Government Usage Rights Notice, this list of conditions and the following disclaimer 
 * in the documentation and/or other materials provided with the distribution.
 *
 * -	The names,trademarks, and service marks of the National Library of Medicine, the National Cancer Institute, the National Institutes 
 * of Health,  and the names of any of the software developers shall not be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package fmservice.test;

import fmservice.httputils.common.ServiceConstants;
import fmservice.server.result.FMServiceResult;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 *
 */
public class RemoveRegionsTest extends FMServerTestBase
{
    private static Logger log  = Logger.getLogger(RemoveRegionsTest.class);
    
     public RemoveRegionsTest(String testConfigFile) throws Exception
     {
         super (testConfigFile);         // realtime operations with the server     
     }
     
     protected void doRemoveRegionTests(String testFile)
     {
         FMTestSetBuilder  fmTestSetBuilder = new FMTestSetBuilder(testProperties);            // testProprties generated by Parent class
         ArrayList<HashMap> removeTestDataSet = fmTestSetBuilder.getTestParams(testFile);
         if (removeTestDataSet == null)
         {
             log.error("No valid  test  dataset for FaceMatch testing found in test file.");
         }
         // do only the last test
         for (int i = 0; i < removeTestDataSet.size(); i++)
         {
             performRemoveOpsTest( i, removeTestDataSet.get(i));
         }
     }
     /*---------------------------------------------------------------------------------------*/ 
     // Perform individual FaceFinder test - 
     // Return the server response as Strinf to the caller
    /*---------------------------------------------------------------------------------------*/
     protected int  performRemoveOpsTest( int testNum, HashMap inputParams)
     {
         System.out.println("-----------------------------------------------");
         System.out.println("Test case: " +(testNum+1)+ ", Input parameters: ");
         System.out.println(inputParams.toString());
         
         Integer service =   (Integer)inputParams.get("service");  
         String operation =   (String)inputParams.get("operation");  
        // int testId = ((Long) inputParams.get("testId")).intValue();
        
         HashMap requestParams = inputParams;
        requestParams.remove("operation");        // not a parameter to the server
        requestParams.remove("service");
        //requestParams.remove("testId");

        FMServiceResult fmResult = null;
         // removing an entire extent
        if (service.equals(ServiceConstants.IMAGE_EXTENT_SVC) && 
            operation.equals(ServiceConstants.REMOVE_EXTENT))
        {
             fmResult = fmManager.performService(
                 ServiceConstants.IMAGE_EXTENT_SVC, ServiceConstants.REMOVE_EXTENT_OP,  requestParams);
        }  
         else if (service.equals(ServiceConstants.FACE_MATCH_REGION_SVC) && 
             operation.equals(ServiceConstants.REMOVE))
        {
             boolean fullImage = true;                                      // removing a full image
             if(inputParams.containsKey("region"))
                 fullImage = false;                                                 // removing only a region (if more than one) of a ful image
                fmResult = fmManager.performService(
                 ServiceConstants.FACE_MATCH_REGION_SVC, ServiceConstants.REGION_REMOVE_OP, requestParams);
        }
        else
         {
             System.out.println("Invalid request specified for remove operation");
             return -1;
         }
     
        
        // Print out the return information:
        if (!fmResult.isValidRequest())
            System.out.println("Invalid client requst; Error message: "+  fmResult.getStatus().statusMessage);
        else if (!fmResult.isSuccess())
            System.out.println("Request not successful; Error message: "+  fmResult.getStatus().statusMessage);
        else
        {
               System.out.println("Romove ops  result:");
               System.out.println( fmResult.convertToJSONString());
        }
        return 1;
     }
  /********************************************************************************************/
/** Perform remote testing of the FaceMatch Web Server using the standard FMConfiguration file
 * and appropriate test data sets.
 * @param args 
 *********************************************************************************************/      
    public  static void main(String[] args)
    {

        boolean localTest = true;
         String defaultConfigFile = FMServerTestBase.getConfigFile(localTest);
         if (defaultConfigFile == null)
         {
             System.out.println("Cannor proceed. Exiting.");
             System.exit(-1);
         }
        String testConfigFile  = defaultConfigFile; 
      
        try
        {
           RemoveRegionsTest  removeTest =  new RemoveRegionsTest(testConfigFile);;
            String testFile = "RemoveOpsTestData.1.json";   //args[0];
            String homeDir  = FMServerTestBase.getHomeDir();
            removeTest.doRemoveRegionTests(homeDir+"/FaceMatch2/FM2JavaClient/testsets/testdata/scenario/"+testFile);  
        }
        catch (Exception e)
        {
                System.out.println("Error while activating/running RemoveRegionsTest application, error: " + e.getMessage() + ". Exiting.");
                e.printStackTrace();
                System.exit(-1);
        }
    }
}