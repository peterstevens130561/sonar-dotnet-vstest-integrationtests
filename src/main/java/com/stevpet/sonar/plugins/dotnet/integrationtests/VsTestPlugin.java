package com.stevpet.sonar.plugins.dotnet.integrationtests;


import java.util.Arrays;
import java.util.List;

import org.sonar.api.SonarPlugin;
import com.stevpet.sonar.plugins.dotnet.mscover.DefaultMsCoverConfiguration;
import com.stevpet.sonar.plugins.dotnet.mscover.coveragetoxmlconverter.VsTestCoverageToXmlConverter;
import com.stevpet.sonar.plugins.dotnet.mscover.workflow.sensor.IntegrationTestCache;
import com.stevpet.sonar.plugins.dotnet.utils.vstowrapper.implementation.SimpleMicrosoftWindowsEnvironment;

public class VsTestPlugin extends SonarPlugin {

    @Override
    public List getExtensions() {
        List exported=Arrays.asList(
        		SimpleMicrosoftWindowsEnvironment.class,
        		DefaultMsCoverConfiguration.class,       		
        		VsTestIntegrationTestCoverageReader.class,
        		IntegrationTestCache.class,
        		IntegrationTestsCoverageSaver.class,
        		VsTestCoverageToXmlConverter.class,
                VsTestSensor.class
                );
   
        return exported;
    }

}
