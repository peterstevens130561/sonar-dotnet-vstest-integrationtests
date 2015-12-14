package com.stevpet.sonar.plugins.dotnet.integrationtests;

import org.sonar.api.BatchExtension;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.scan.filesystem.PathResolver;

import com.stevpet.sonar.plugins.dotnet.mscover.coveragesaver.defaultsaver.CoverageSaverBase;
import com.stevpet.sonar.plugins.dotnet.mscover.coveragesaver.nullsaver.NullBranchFileCoverageSaver;
import com.stevpet.sonar.plugins.dotnet.mscover.ittest.vstest.IntegrationTestLineFileCoverageSaver;
import com.stevpet.sonar.plugins.dotnet.mscover.resourceresolver.DefaultResourceResolver;
import com.stevpet.sonar.plugins.dotnet.utils.vstowrapper.MicrosoftWindowsEnvironment;


public class IntegrationTestsCoverageSaver extends CoverageSaverBase implements
		BatchExtension {

	public IntegrationTestsCoverageSaver(
			MicrosoftWindowsEnvironment microsoftWindowsEnvironment, 
			PathResolver pathResolver, 
			FileSystem fileSystem) {
		super(new NullBranchFileCoverageSaver(), 
				new IntegrationTestLineFileCoverageSaver(
						new DefaultResourceResolver(pathResolver,fileSystem)), 
						microsoftWindowsEnvironment);
	}

}
