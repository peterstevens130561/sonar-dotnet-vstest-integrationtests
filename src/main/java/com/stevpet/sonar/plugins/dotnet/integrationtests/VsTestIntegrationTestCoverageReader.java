package com.stevpet.sonar.plugins.dotnet.integrationtests;

import org.sonar.api.BatchExtension;
import org.sonar.api.batch.fs.FileSystem;

import com.stevpet.sonar.plugins.common.commandexecutor.DefaultProcessLock;
import com.stevpet.sonar.plugins.dotnet.mscover.MsCoverConfiguration;
import com.stevpet.sonar.plugins.dotnet.mscover.coverageparsers.vstestcoverageparser.VsTestFilteringCoverageParser;
import com.stevpet.sonar.plugins.dotnet.mscover.ittest.vstest.IntegrationTestCoverageReaderBase;
import com.stevpet.sonar.plugins.dotnet.utils.vstowrapper.MicrosoftWindowsEnvironment;

public class VsTestIntegrationTestCoverageReader extends
		IntegrationTestCoverageReaderBase implements BatchExtension {

	public VsTestIntegrationTestCoverageReader(
			MicrosoftWindowsEnvironment microsoftWindowsEnvironment,
			MsCoverConfiguration msCoverProperties,
			FileSystem fileSystem) {
		super(microsoftWindowsEnvironment, 
				new VsTestFilteringCoverageParser(),
				new DefaultProcessLock());
	}

}
