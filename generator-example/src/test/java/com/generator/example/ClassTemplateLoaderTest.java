package com.generator.example;

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
		Template template = configuration.getTemplate("model.ftl");
		System.out.println("template:" + template);
	}
}
