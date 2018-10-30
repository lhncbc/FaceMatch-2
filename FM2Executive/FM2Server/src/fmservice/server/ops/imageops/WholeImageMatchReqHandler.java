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
package fmservice.server.ops.imageops;

import fmservice.httputils.common.ServiceConstants;
import  fmservice.server.result.FMServiceResult;

import fmservice.server.ops.FMDataManager;
import java.util.HashMap;

/**
 *
 *
 */
public class WholeImageMatchReqHandler implements ServiceConstants
{
    protected FMDataManager  fmDataManager;
    
    public WholeImageMatchReqHandler(FMDataManager dataManager)
    {
       fmDataManager = dataManager;
    }   
 
    public HashMap handleRequest(int operation, HashMap <String, Object>inputParams) throws Exception
    {
         HashMap  <String, Object>outputParams = new HashMap();
         if (operation == WHOLEIMAGE_INGEST_OP)
             return null;
        else  if (operation == WHOLEIMAGE_QUERY_OP)
             return null;
         else  if (operation == WHOLEIMAGE_REMOVE_OP)
             outputParams = removeImage(inputParams);
         return outputParams;
     }
  
    /*---------------------------------------------------------------------------------------------------*/
    // Remove the specified image from the system.
    // Involves: Deleting the image from the FM image repository, fm database and  
    // deleting the correspondingindex file from the system
    protected HashMap <String, Object>  removeImage
        ( HashMap  <String, Object>inputParams)
    {
        int extent = (Integer) inputParams.get(EXTENT_NAME);
        String imageTag = (String) inputParams.get(IMAGE_TAG);

        // request the datamanager to delete the image
       HashMap  <String, Object> outParams = null; //fmDataManager.removeImage(eventID, imageID); 
       return outParams;
    }       
}
