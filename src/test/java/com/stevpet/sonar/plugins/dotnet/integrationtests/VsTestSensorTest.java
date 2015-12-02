package com.stevpet.sonar.plugins.dotnet.integrationtests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;

import com.stevpet.sonar.plugins.dotnet.mscover.MsCoverConfiguration;
import com.stevpet.sonar.plugins.dotnet.mscover.coveragereader.CoverageReader;
import com.stevpet.sonar.plugins.dotnet.mscover.coveragesaver.CoverageSaver;
import com.stevpet.sonar.plugins.dotnet.mscover.coveragetoxmlconverter.BinaryCoverageToXmlConverter;
import com.stevpet.sonar.plugins.dotnet.mscover.model.sonar.SonarCoverage;
import com.stevpet.sonar.plugins.dotnet.mscover.workflow.sensor.IntegrationTestCache;
import com.stevpet.sonar.plugins.dotnet.utils.vstowrapper.MicrosoftWindowsEnvironment;

public class VsTestSensorTest {

	@Mock private MsCoverConfiguration msCoverConfiguration;
	@Mock private MicrosoftWindowsEnvironment microsoftWindowsEnvironemnt;
	@Mock private IntegrationTestCache integrationTestCache;
	@Mock private CoverageReader coverageReader;
	@Mock private CoverageSaver coverageSaver;
	@Mock private Project project;
	private Sensor sensor;
	@Mock private SensorContext context;
	@Mock private BinaryCoverageToXmlConverter coverageToXmlCoverter;

	
	@Before
	public void creation() {
		org.mockito.MockitoAnnotations.initMocks(this);
		try {
			sensor = new VsTestSensor(
				msCoverConfiguration,
				microsoftWindowsEnvironemnt,
				integrationTestCache,
				coverageReader,
				coverageSaver, coverageToXmlCoverter);
		} catch (Exception e) {
			fail("could not create sensor " + e.getMessage());
		}
		
	}
	
	@Test
	public void notAModule() {
		when(project.isModule()).thenReturn(false);
		boolean shouldExecuteOnProject = sensor.shouldExecuteOnProject(project);
		assertFalse("should not execute on root",shouldExecuteOnProject);
	}
	
	@Test
	public void isAModule() {
		when(project.isModule()).thenReturn(true);
		boolean shouldExecuteOnProject = sensor.shouldExecuteOnProject(project);
		assertTrue("should execute on module",shouldExecuteOnProject);		
	}
	
	@Test
	public void noConfig() {
		when(microsoftWindowsEnvironemnt.isUnitTestProject(project)).thenReturn(false);
		when(msCoverConfiguration.getIntegrationTestsDir()).thenReturn(null);
		when(msCoverConfiguration.getIntegrationTestsPath()).thenReturn(null);
		sensor.analyse(project,context);
		verify(integrationTestCache,times(0)).getHasRun();
	}
	
	@Test
	public void configHasNotRun() {
		File file= new File("boy");
		when(microsoftWindowsEnvironemnt.isUnitTestProject(project)).thenReturn(false);
		when(msCoverConfiguration.getIntegrationTestsDir()).thenReturn(null);
		when(msCoverConfiguration.getIntegrationTestsPath()).thenReturn(file.getAbsolutePath());
		sensor.analyse(project,context);
		verify(integrationTestCache,times(1)).getHasRun();
		verify(integrationTestCache,times(1)).setHasRun(true);
		verify(coverageReader,times(1)).read(any(SonarCoverage.class), any(File.class));
		verify(coverageSaver,times(1)).save(eq(context), any(SonarCoverage.class));
	}
	
	@Test
	public void configHasRun() {
		File file= new File("boy");
		when(microsoftWindowsEnvironemnt.isUnitTestProject(project)).thenReturn(false);
		when(msCoverConfiguration.getIntegrationTestsDir()).thenReturn(null);
		when(msCoverConfiguration.getIntegrationTestsPath()).thenReturn(file.getAbsolutePath());
		when(integrationTestCache.getHasRun()).thenReturn(true);
		sensor.analyse(project,context);
		verify(integrationTestCache,times(1)).getHasRun();
		verify(integrationTestCache,times(0)).setHasRun(true);
		verify(coverageReader,times(0)).read(any(SonarCoverage.class), any(File.class));
		verify(coverageSaver,times(1)).save(eq(context), any(SonarCoverage.class));
	}
	
	@Test
	public void configHasNotRunWithDir() {
		File file= new File("boy");
		when(microsoftWindowsEnvironemnt.isUnitTestProject(project)).thenReturn(false);
		when(msCoverConfiguration.getIntegrationTestsDir()).thenReturn(file.getAbsolutePath());
		when(msCoverConfiguration.getIntegrationTestsPath()).thenReturn(null);
		sensor.analyse(project,context);
		verify(integrationTestCache,times(1)).getHasRun();
		verify(integrationTestCache,times(1)).setHasRun(true);
		verify(coverageReader,times(1)).read(any(SonarCoverage.class), any(File.class));
		verify(coverageSaver,times(1)).save(eq(context), any(SonarCoverage.class));
	}
}
