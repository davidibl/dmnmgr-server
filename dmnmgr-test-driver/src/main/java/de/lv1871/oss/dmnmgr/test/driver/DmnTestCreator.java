package de.lv1871.oss.dmnmgr.test.driver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.lv1871.oss.dmnmgr.test.driver.model.DmnProject;
import de.lv1871.oss.dmnmgr.test.driver.model.DmnProjectTestContainer;
import de.lv1871.oss.dmnmgr.test.driver.model.DmnProjectTestDefinition;
import de.lv1871.oss.dmnmgr.test.driver.model.DmnTest;
import de.lv1871.oss.dmnmgr.test.driver.model.DmnTestSuite;
import de.lv1871.oss.tester.test.domain.DecisionEngine;
import junit.framework.Test;
import junit.framework.TestSuite;

public class DmnTestCreator {

	private static String PROJECT_FILE_EXTENSION = "dmnapp.json";
	private static ObjectMapper MAPPER = new ObjectMapper();

	public static Test suite() {
		var suite = new TestSuite();
		new DmnTestCreator().createDmnTests(null).forEach(suite::addTest);
		return suite;
	}

	public static Test getSuiteParameterized(Path basePath) {
		var suite = new TestSuite();
		new DmnTestCreator().createDmnTests(basePath).forEach(suite::addTest);
		return suite;
	}

	public List<TestSuite> createDmnTests(Path basePath) {
		return findTests(basePath)
			.stream()
			.map(this::toJunitTestSuite)
			.collect(Collectors.toList());
	}

	public List<DmnTestSuite> findTests(Path basePath) {
		return getResourceFiles(basePath)
			.stream()
			.filter(file -> file.endsWith(PROJECT_FILE_EXTENSION))
			.map(file -> Paths.get(file).toString())
			.map(this::loadTests)
			.collect(Collectors.toList());
	}

	private DmnTestSuite loadTests(String projectFilePath) {
		try {
			var projectFile = new File(projectFilePath);

			var dmnProject = MAPPER.readValue(new File(projectFilePath), DmnProject.class);
			var suite = new DmnTestSuite();

			suite.setTest(dmnProject.getTestsuite()
				.entrySet()
				.stream()
				.map(this::toDmnTest)
				.flatMap(List::stream)
				.collect(Collectors.toList()));

			suite.setXml(readXmlFile(projectFile, dmnProject.getDmnPath()));
			suite.setName(dmnProject.getDmnPath());
			return suite;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String readXmlFile(File projectFile, String dmnPath) throws IOException {

		var dmnFilePath = getResourceRelativToBasePath(projectFile.getParentFile(), dmnPath);

		var xmlFile = Paths.get(dmnFilePath);
		return new String(Files.readAllBytes(xmlFile), StandardCharsets.UTF_8);
	}

	private List<DmnTest> toDmnTest(Entry<String, DmnProjectTestContainer> testsRaw) {
		return testsRaw
				.getValue()
				.getTests()
				.stream()
				.map(testRaw -> this.testRawToTest(testRaw, testsRaw.getKey()))
				.collect(Collectors.toList());
	}

	private DmnTest testRawToTest(DmnProjectTestDefinition testRaw, String tableId) {
		var test = new DmnTest();
		test.setData(testRaw.getData());
		test.setExpectedData(testRaw.getExpectedData());
		var testName = (testRaw.getName() != null && !testRaw.getName().trim().equals("")) ? testRaw.getName()
				: UUID.randomUUID().toString();
		test.setName(testName);
		test.setTableId(tableId);
		return test;
	}

	private TestSuite toJunitTestSuite(DmnTestSuite dmnSuite) {
		var suite = new TestSuite(dmnSuite.getName());

		var processEngine = DecisionEngine.createEngine().parseDecision(dmnSuite.getXml());

		dmnSuite
			.getTest()
			.stream()
			.collect(Collectors.groupingBy(DmnTest::getTableId))
			.entrySet()
			.stream()
			.map(entry -> this.entrySetToJunitTestSuite(entry, processEngine))
			.forEach(suite::addTest);
		return suite;
	}

	private TestSuite entrySetToJunitTestSuite(Entry<String, List<DmnTest>> entry, DecisionEngine engine) {
		TestSuite suite = new TestSuite(entry.getKey());
		entry
			.getValue()
			.stream()
			.map(dmnTest -> new DmnTestExecutor(dmnTest, engine))
			.forEach(suite::addTest);
		return suite;
	}

	private String getResourceRelativToBasePath(File baseDirectory, String filename) {
		return getResources(baseDirectory)
			.stream()
			.filter(file -> file.endsWith(filename))
			.findFirst()
			.orElseThrow(() -> new RuntimeException(String.format("DMN File \"%s\" not found", filename)));
	}

	private List<String> getResourceFiles(Path basePath) {
		if (basePath != null) {
			return getDmnProjectFilesByPath(basePath);
		}

		var files = new ArrayList<String>();
		files.addAll(getResources(Paths.get("src", "main", "resources").toFile()));
		files.addAll(getResources(Paths.get("src", "test", "resources").toFile()));
		return files;
	}

	private List<String> getDmnProjectFilesByPath(Path basePath) {
		if (basePath.toFile().isFile()) {
			return Arrays.asList(basePath.toFile().getAbsolutePath());
		}

		return getResourceFiles(basePath);
	}

	private List<String> getResources(File base) {
		if (base == null || !base.exists() || !base.isDirectory()) {
			return new ArrayList<>();
		}

		var files = Arrays.asList(base.list())
				.stream()
				.map(file -> Paths.get(base.getPath(), file).toString())
				.collect(Collectors.toList());
		
		files.addAll(files.stream()
			.filter(path -> Files.isDirectory(Paths.get(path)))
			.map(File::new)
			.map(this::getResources)
			.flatMap(List::stream)
			.collect(Collectors.toList()));
		
		return files;
	}
}
