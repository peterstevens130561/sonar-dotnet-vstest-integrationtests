/*******************************************************************************
 *
 * SonarQube Dotnet Integration Tests for VsTest Plugin
 * Copyright (C) 2015 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 *
 * Author: Peter Stevens, peter@famstevens.eu
 *******************************************************************************/
package com.stevpet.sonar.plugins.dotnet.integrationtests;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;

import com.stevpet.sonar.plugins.dotnet.mscover.MsCoverConfiguration;
import com.stevpet.sonar.plugins.dotnet.mscover.coveragereader.CoverageReader;
import com.stevpet.sonar.plugins.dotnet.mscover.coveragesaver.CoverageSaver;
import com.stevpet.sonar.plugins.dotnet.mscover.coveragetoxmlconverter.BinaryCoverageToXmlConverter;
import com.stevpet.sonar.plugins.dotnet.mscover.ittest.vstest.IntegrationTestsCoverageReader;
import com.stevpet.sonar.plugins.dotnet.mscover.model.sonar.SonarCoverage;
import com.stevpet.sonar.plugins.dotnet.mscover.workflow.sensor.IntegrationTestCache;
import com.stevpet.sonar.plugins.dotnet.mscover.workflow.sensor.LogChanger;
import com.stevpet.sonar.plugins.dotnet.utils.vstowrapper.MicrosoftWindowsEnvironment;


/**
 * ProjectBuilder for dotnet projects
 * 
 * The build method will be invoked by sonar in the ProjectBuild phase, and
 * populates the MicrosoftWindowsEnvironment
 * 
 * @author stevpet
 * 
 */
public class VsTestSensor implements Sensor {

    private static final String LOGPREFIX = "IntegrationTest VsTest Sensor : ";
    
	private MicrosoftWindowsEnvironment microsoftWindowsEnvironment;
	private IntegrationTestCache cache;
	private CoverageReader reader;
	private CoverageSaver saver;

	private MsCoverConfiguration msCoverConfiguration;

	private BinaryCoverageToXmlConverter coverageToXmlConverter;

	public VsTestSensor(MsCoverConfiguration msCoverConfiguration, MicrosoftWindowsEnvironment microsoftWindowsEnvironment,
			IntegrationTestCache cache, 
			CoverageReader coverageReader,
			CoverageSaver coverageSaver,
			BinaryCoverageToXmlConverter coverageToXmlConverter) {
		this.microsoftWindowsEnvironment = microsoftWindowsEnvironment;
		this.cache=cache;
		this.reader=coverageReader;
		this.saver=coverageSaver;
		this.msCoverConfiguration=msCoverConfiguration;
		this.coverageToXmlConverter=coverageToXmlConverter;
	}
	private static final Logger LOG = LoggerFactory
			.getLogger(VsTestSensor.class);


	@Override
	public boolean shouldExecuteOnProject(Project project) {
        return project.isModule();
	}

	@Override
	public void analyse(Project module, SensorContext context) {
        LogInfo("Starting");
        if(microsoftWindowsEnvironment.isUnitTestProject(module)) {
            LogInfo("Skipping as it is a test project");
            return;
        }
        File coverageFile = getCoverageFile();
        if(coverageFile==null) {
        	return;
        }
        
        LogChanger.setPattern();
        
        SonarCoverage sonarCoverage;
        if(cache.getHasRun()) {
            sonarCoverage=cache.getCoverage();
        } else {
            sonarCoverage = new SonarCoverage();
            File xmlFile=coverageToXmlConverter.convertFiles(coverageFile);
            reader.read(sonarCoverage,coverageFile);
            cache.setHasRun(true);
            cache.setCoverage(sonarCoverage);
        } 

        saver.save(context,sonarCoverage);
        LogInfo("Done");
	}
	
	private File getCoverageFile() {
		File result=null;
		String integrationTestsPath = msCoverConfiguration
				.getIntegrationTestsDir();
		String coveragePath = msCoverConfiguration.getIntegrationTestsPath();
		if (StringUtils.isNotEmpty(integrationTestsPath)) {
			result = new File(integrationTestsPath);
		} else if (StringUtils.isNotEmpty(coveragePath)) {
			result = new File(coveragePath);
		}	
		return result;
	}
	
    private void LogInfo(String msg, Object... objects) {
        LOG.info(LOGPREFIX + msg, objects);
    }
}