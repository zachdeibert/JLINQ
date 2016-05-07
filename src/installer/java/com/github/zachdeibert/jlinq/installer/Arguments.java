package com.github.zachdeibert.jlinq.installer;

import java.io.File;

public final class Arguments {
	public boolean showHelp;
	public File outputDirectory;
	public File javaDirectory;
	public File jar;
	public ConversionType type;

	public Arguments() {
		showHelp = false;
		outputDirectory = javaDirectory = new File(System.getenv("JAVA_HOME"));
		jar = new File("output.jar");
		type = ConversionType.JdkInstall;
	}
}
