/*
Informational Notice:
This software was developed under contract funded by the National Library of Medicine, which is part of the National Institutes of Health, 
an agency of the Department of Health and Human Services, United States Government.

- The license of this software is an open-source BSD license.  It allows use in both commercial and non-commercial products.

- The license does not supersede any applicable United States law.

- The license does not indemnify you from any claims brought by third parties whose proprietary rights may be infringed by your usage of this software.

Government usage rights for this software are established by Federal law, which includes, but may not be limited to, Federal Acquisition Regulation 
(FAR) 48 C.F.R. Part52.227-14, Rights in Data—General.
The license for this software is intended to be expansive, rather than restrictive, in encouraging the use of this software in both commercial and 
non-commercial products.

LICENSE:

Government Usage Rights Notice:  The U.S. Government retains unlimited, royalty-free usage rights to this software, but not ownership,
as provided by Federal law.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
•	Redistributions of source code must retain the above Government Usage Rights Notice, this list of conditions and the following disclaimer.

•	Redistributions in binary form must reproduce the above Government Usage Rights Notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.

•	The names,trademarks, and service marks of the National Library of Medicine, the National Cancer Institute, the National Institutes 
of Health,  and the names of any of the software developers shall not be used to endorse or promote products derived from this software without 
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITEDTO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/*
 * BatchRequestProcessor.java
 */

package workbench.reqproc;

import java.util.HashMap;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import workbench.control.FMMenuCommands;
import workbench.control.WBProperties;
import workbench.display.DisplayUtils;
import workbench.event.ActionCompletionListener;
import workbench.reqproc.input.BatchInputSelectionDialog;
import workbench.util.Utils;
import workbench.util.WBUtils;

import fm2client.app.FM2RegionBatchApp;

public class BatchRequestProcessor implements RequestProcessor, FMMenuCommands {

	private static Logger log = Logger.getLogger(BatchRequestProcessor.class);

	// The following map translated the human readable menu command/subcommands
	// to
	// corresponding FacematchClient-defined text-based operationNames (without
	// spaces etc.)
	// as provided to the FM2RegionTestApp.
	//
	protected static HashMap<String, String> External2InternalString = new HashMap<String, String>() {
		{
			put(INGEST_BATCH_RT, FM_REGION_INGEST_OP);
			put(QUERY_BATCH_RT, FM_REGION_QUERY_OP);
			put(FF_BATCH_RT, FM_FACE_FIND_OP);
			put(REMOVE_BATCH_RT, FM_REGION_REMOVE_OP);
		}
	};

	protected static JFrame batchInputFrame = null;
	// protected static JFrame singleOpsInputFrame = null;

	protected ActionCompletionListener parent;
	HashMap<String, Object> initialParams;
	HashMap<String, Object> selectedParams;

	public BatchRequestProcessor() throws Exception {
		initialParams = getInitialBatchInputParams();
	}

	// *-------------------------------------------------------------------------------------------------------------
	// Create the input Selection panel.
	// Check for parameters specified in the config file
	// and set as initial parameters. Provide defaults if not specified
	// *---------------------------------------------------------------------------------------------------------------
	protected HashMap<String, Object> getInitialBatchInputParams() {
		String fileSystemRoot = Utils.getFileSystemRoot();
		HashMap<String, Object> batchParams = new HashMap();

		Properties configProperties = WBProperties.configProperties;
		String testDataDir = configProperties.getProperty("fm2test.datadir", fileSystemRoot);
		if (testDataDir != null)
			batchParams.put("dataFilename", testDataDir);
		// Select file with BatchRequest data

		// show the checkbox whether or not to store FaceMatch return results

		String resultStoreDir = configProperties.getProperty("fm2test.resultstore.dir", fileSystemRoot);
		if (resultStoreDir != null)
			batchParams.put("resultsDirectory", resultStoreDir);

		HashMap<String, String[]> clientToExtentMap = WBUtils.getClient2ExtentMap(configProperties);
		batchParams.put("clientInfoMap", clientToExtentMap);

		String testType = configProperties.getProperty("operationType", "Face Find"); // default
		batchParams.put("operationType", testType);

		batchParams.put("displayResults", new Boolean(true));
		batchParams.put("storeResults", new Boolean(true));

		return batchParams;
	}

	// ---------------------------------------------------------------------------------------------------------------------------
	// Execute the Menu command selectedby the user
	// invoked by the parent object
	// ---------------------------------------------------------------------------------------------------------------------------
	public void executeCommand(ActionCompletionListener listener, String command, Object commandParam) {
		try {
			parent = listener;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					BatchInputSelectionDialog inputSelectionDialog = createBatchInputSelectionDialog();
					inputSelectionDialog.setInitialChoices(command, initialParams);

					WBProperties.mainFrame.setVisible(false);
					batchInputFrame.toFront();
					inputSelectionDialog.start();

					// ---------------------------------------------------------------------------------------------------------------------//
					// We will come here and get the selected params only after
					// the selection window closes
					// ------------------------------------------------------------------------------------------------------------------------//
					HashMap<String, Object> userParams = inputSelectionDialog.getSelectedParams();
					batchInputFrame.dispose();
					WBProperties.mainFrame.setVisible(true);
					processBatchUserInput(command, userParams);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			notifyCompletion(command, -1, null);
		}
	}

	/*--------------------------------------------------------------------------------------------------------------*/
	// build the dialog to receive user input for performing Batch operation
	// requests
	protected BatchInputSelectionDialog createBatchInputSelectionDialog() {
		String dialogName = "Batch Operation Request";
		// String displayName = " Select parameters for FaceMatch Region
		// Operation Requests in Batch mode";

		batchInputFrame = new JFrame(dialogName);
		batchInputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // hide
																			// it
																			// for
																			// next
																			// time

		BatchInputSelectionDialog batchInputDialog = new BatchInputSelectionDialog(batchInputFrame, initialParams);

		// setFrameCenter(inputSelectionFrame,
		// inputSelectionFrame.getPreferredSize());
		batchInputFrame.pack();
		return batchInputDialog;
	}

	// *-----------------------------------------------------------------------------------------------------
	// Comes here after the user selects the parameters for the batch operation
	//
	protected void processBatchUserInput(String command, HashMap<String, Object> userParams) {
		selectedParams = userParams;
		System.out.println("\n>>> Received  Selected params: " + selectedParams);
		if (selectedParams == null) {
			System.out.println("\n>>> User canceled operation");
			notifyCompletion(command, 0, null);
			return;
		}
		try {
			int status = performBatchOperations(command, selectedParams);
			notifyCompletion(command, status, null);
		} catch (Exception e) {
			DisplayUtils.displayErrorMessage("Error executing batch command " + e.getMessage());
			e.printStackTrace();
		}
	}
	/*--------------------------------------------------------------------------------------------------------------*/

	protected int performBatchOperations(String command, HashMap<String, Object> requestParams) throws Exception {
		String imageExtent = (String) requestParams.get("imageExtent");
		String testDataFile = (String) requestParams.get("dataFilename");
		boolean displayResult = ((Boolean) requestParams.get("displayResults")).booleanValue();
		boolean storeResult = ((Boolean) requestParams.get("storeResults")).booleanValue();
		String resultStoreDir = storeResult ? "" : (String) requestParams.get("resultsDirectory");

		String operation = command;
		final String fm2testType = External2InternalString.get(operation);
		if (fm2testType == null) {
			DisplayUtils.displayErrorMessage("Internal Error; Internal name for operation " + command + " not found.");
			return 0;
		}

		try {
			boolean displayResults = true; // default set to true; currently
											// crashes on false;
			FM2RegionBatchApp regionTestApp = new FM2RegionBatchApp(WBProperties.configProperties, true);
			regionTestApp.setImageCollection(imageExtent);
			regionTestApp.performFacematchTests(fm2testType, testDataFile, displayResults, resultStoreDir, storeResult);
			
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void notifyCompletion(String function, int status, Object param) {
		
		parent.actionCompleted(null);
	}
}
