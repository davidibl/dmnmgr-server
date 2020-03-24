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
		TestSuite suite = new TestSuite();
		new DmnTestCreator().createDmnTests(null).forEach(suite::addTest);
		return suite;
	}

	public static Test getSuiteParameterized(Path basePath) {
		TestSuite suite = new TestSuite();
		new DmnTestCreator().createDmnTests(basePath).forEach(suite::addTest);
		return suite;
	}

	public List<TestSuite> createDmnTests(Path basePath) {
		// @formatter:off
		return findTests(basePath)
			.stream()
			.map(this::toJunitTestSuite)
			.collect(Collectors.toList());
		// @formatter:on
	}

	public List<DmnTestSuite> findTests(Path basePath) {

		// @formatter:off
		return getResourceFiles(basePath)
			.stream()
			.filter(file -> file.endsWith(PROJECT_FILE_EXTENSION))
			.map(file -> Paths.get(file).toString())
			.map(this::loadTests)
			.collect(Collectors.toList());
		// @formatter:on
	}

	private DmnTestSuite loadTests(String projectFilePath) {
		try {
			File projectFile = new File(projectFilePath);

			DmnProject dmnProject = MAPPER.readValue(new File(projectFilePath), DmnProject.class);
			DmnTestSuite suite = new DmnTestSuite();

			// @formatter:off
			suite.setTest(dmnProject.getTestsuite()
				.entrySet()
				.stream()
				.map(this::toDmnTest)
				.flatMap(List::stream)
				.collect(Collectors.toList()));
			// @formatter:on

			suite.setXml(readXmlFile(projectFile, dmnProject.getDmnPath()));
			suite.setName(dmnProject.getDmnPath());
			return suite;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String readXmlFile(File projectFile, String dmnPath) throws IOException {

		String dmnFilePath = getResourceRelativToBasePath(projectFile.getParentFile(), dmnPath);

		Path xmlFile = Paths.get(dmnFilePath);
		return new String(Files.readAllBytes(xmlFile), StandardCharsets.UTF_8);
	}

	private List<DmnTest> toDmnTest(Entry<String, DmnProjectTestContainer> testsRaw) {
		// @formatter:off
		return testsRaw
				.getValue()
				.getTests()
				.stream()
				.map(testRaw -> this.testRawToTest(testRaw, testsRaw.getKey()))
				.collect(Collectors.toList());
		// @formatter:on
	}

	private DmnTest testRawToTest(DmnProjectTestDefinition testRaw, String tableId) {
		DmnTest test = new DmnTest();
		test.setData(testRaw.getData());
		test.setExpectedData(testRaw.getExpectedData());
		String name = (testRaw.getName() != null && !testRaw.getName().trim().equals("")) ? testRaw.getName()
				: UUID.randomUUID().toString();
		test.setName(name);
		test.setTableId(tableId);
		return test;
	}

	private TestSuite toJunitTestSuite(DmnTestSuite dmnSuite) {
		TestSuite suite = new TestSuite(dmnSuite.getName());

		// @formatter:off
		DecisionEngine processEngine = DecisionEngine.createEngine();

		processEngine.parseDecision(dmnSuite.getXml());

		dmnSuite
			.getTest()
			.stream()
			.collect(Collectors.groupingBy(DmnTest::getTableId))
			.entrySet()
			.stream()
			.map(entry -> this.entrySetToJunitTestSuite(entry, processEngine))
			.forEach(suite::addTest);
		// @formatter:on
		return suite;
	}

	private TestSuite entrySetToJunitTestSuite(Entry<String, List<DmnTest>> entry, DecisionEngine engine) {
		// @formatter:off
		TestSuite suite = new TestSuite(entry.getKey());
		entry
			.getValue()
			.stream()
			.map(dmnTest -> new DmnTestExecutor(dmnTest, engine))
			.forEach(suite::addTest);
		return suite;
		// @formatter:on
	}

	private String getResourceRelativToBasePath(File baseDirectory, String filename) {
		// @formatter:off
		return getResources(baseDirectory)
			.stream()
			.filter(file -> file.endsWith(filename))
			.findFirst()
			.orElseThrow(() -> new RuntimeException(String.format("DMN File \"%s\" not found", filename)));
		// @formatter:on
	}

	private List<String> getResourceFiles(Path basePath) {
		if (basePath != null) {
			return getDmnProjectFilesByPath(basePath);
		}

		List<String> files = new ArrayList<String>();
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
		// @formatter:off
		List<String> files = Arrays.asList(base.list())
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
		// @formatter:on
	}
}
