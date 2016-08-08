package com.generator.example;

import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class ClassTemplateLoaderTest {
	public static void main(String[] args) throws Exception {
		// Resource resource = ResourceUtils.getResource("template/model.ftl");
		// String fileName = resource.getFilename();
		// String value = IOUtils.toString(resource.getInputStream());
		// System.out.println(value);
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
		ClassTemplateLoader ctl = new ClassTemplateLoader(ClassTemplateLoaderTest.class, "/template");
		configuration.setTemplateLoader(ctl);
		Map<String, String> params = new HashMap<String, String>();
		params.put("packageName", "com.generator.example");
		Template template = configuration.getTemplate("test.ftl");
		template.process(params, new OutputStreamWriter(System.out));
	}
}
